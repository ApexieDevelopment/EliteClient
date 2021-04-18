package org.apexie.eliteclient.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apexie.eliteclient.Config;
import org.apexie.eliteclient.MapsList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class SQLiteDataSource implements DatabaseManager {
    private final Logger LOGGER = LoggerFactory.getLogger(SQLiteDataSource.class);
    private final HikariConfig config = new HikariConfig();
    private final HikariDataSource ds;

    public SQLiteDataSource() {
        try {
            final File dbFile = new File("eliteclient.db");
            if (!dbFile.exists()) {
                if (dbFile.createNewFile()) {
                    LOGGER.info("Created SQLite database file");
                } else {
                    LOGGER.error("Could not create SQLite database file");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        config.setJdbcUrl("jdbc:sqlite:eliteclient.db");
        config.setConnectionTestQuery("SELECT 1");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(config);

        try (final Statement statement = getConnection().createStatement()) {


            // language=SQLite
            statement.execute("CREATE TABLE IF NOT EXISTS guild_settings (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "guild_id VARCHAR(20) NOT NULL," +
                    "prefix VARCHAR(255) NOT NULL DEFAULT '" + Config.get("prefix") + "'" +
                    ");");
            LOGGER.info("Table initialized");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    @Override
    public String getPrefix(long guildId) {
        try (final PreparedStatement preparedStatement = getConnection()
                // language=SQLite
                .prepareStatement("SELECT prefix FROM guild_settings where guild_id = ?")) {
            preparedStatement.setString(1, String.valueOf(guildId));

            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if(resultSet.next()) {
                    return resultSet.getString("prefix");
                }
            }

            try (final PreparedStatement insertStatement = getConnection()
                    // language=SQLite
                    .prepareStatement("INSERT INTO guild_settings(guild_id) VALUES(?)")) {
                insertStatement.setString(1, String.valueOf(guildId));

                insertStatement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Config.get("prefix");
    }

    @Override
    public void setPrefix(long guildId, String newPrefix) {
        try (final PreparedStatement preparedStatement = getConnection()
                // language=SQLite
                .prepareStatement("UPDATE guild_settings SET prefix = ? WHERE guild_id = ?")) {

            preparedStatement.setString(1, newPrefix);
            preparedStatement.setString(2, String.valueOf(guildId));

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
