package org.apexie.eliteclient;

import me.duncte123.botcommons.BotCommons;
import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.apexie.eliteclient.database.SQLiteDataSource;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

public class Listener extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);
    private final CommandManager manager = new CommandManager();
    public static String username;
    public static String clientAvatar;

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        this.username = event.getJDA().getSelfUser().getName();
        this.clientAvatar = event.getJDA().getSelfUser().getEffectiveAvatarUrl();
        LOGGER.info("{} is ready to go!", event.getJDA().getSelfUser().getAsTag());
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                event.getJDA().getPresence().setActivity(Activity.playing(Config.get("prefix") + "help | " + event.getJDA().getGuilds().size() + " guilds"));
            }
        }, 0, 5000);
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        User user = event.getAuthor();

        if(user.isBot() || event.isWebhookMessage()) {
            return;
        }

        final long guildId = event.getGuild().getIdLong();
        String prefix = MapsList.PREFIXES.computeIfAbsent(guildId, this::getPrefix);
        String raw = event.getMessage().getContentRaw();

        if(raw.equalsIgnoreCase(prefix + "shutdown") && event.getAuthor().getId().equals(Config.get("owner_id"))) {
            event.getJDA().getGuilds().forEach(guild -> {
                final AudioManager audioManager = guild.getAudioManager();
                audioManager.closeAudioConnection();
            });
            EmbedBuilder shutdownEmbed = EmbedUtils.embedMessage("")
                    .setAuthor("Shutdown successful")
                    .setColor(event.getGuild().getSelfMember().getColor())
                    .setDescription("Your client's shutdown was done gracefully \n" +
                            "with exit code 0 with no errors at all.")
                    .addField("Log", "`Process finished with exit code 0`", false);
            event.getChannel().sendMessage(shutdownEmbed.build()).queue();
            LOGGER.info("Shutting down");
            // event.getJDA().shutdown();
            System.exit(0);
            return;
        }

        if (raw.startsWith(prefix)) {
            manager.handle(event, prefix);
        }
    }

    private String getPrefix(long guildId) {
        try (final PreparedStatement preparedStatement = SQLiteDataSource
                .getConnection()
                // language=SQLite
                .prepareStatement("SELECT prefix FROM guild_settings where guild_id = ?")) {
            preparedStatement.setString(1, String.valueOf(guildId));

            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if(resultSet.next()) {
                    return resultSet.getString("prefix");
                }
            }

            try (final PreparedStatement insertStatement = SQLiteDataSource
                    .getConnection()
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
}
