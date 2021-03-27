package org.apexie.eliteclient;

import me.duncte123.botcommons.messaging.EmbedUtils;
import me.duncte123.botcommons.web.WebUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.apexie.eliteclient.command.commands.AboutCommand;
import org.apexie.eliteclient.database.SQLiteDataSource;

import javax.security.auth.login.LoginException;
import java.sql.SQLException;
import java.util.EnumSet;

public class Bot {

    public static final String ELITECLIENT_VERSION = "Alpha 1.0";

    private Bot() throws LoginException, SQLException {
        WebUtils.setUserAgent("Mozilla/5.0");

        SQLiteDataSource.getConnection();

        EmbedUtils.setEmbedBuilder(
                () -> new EmbedBuilder()
                .setFooter(Listener.username, Listener.clientAvatar)
        );

        JDABuilder.createDefault(
                Config.get("token"),
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_VOICE_STATES,
                GatewayIntent.GUILD_EMOJIS
        )
                .disableCache(EnumSet.of(
                        CacheFlag.CLIENT_STATUS,
                        CacheFlag.ACTIVITY,
                        CacheFlag.EMOTE
                ))
                .enableCache(EnumSet.of(
                        CacheFlag.VOICE_STATE
                ))
                .addEventListeners(new Listener())
                .build();
    }

    public static void main(String[] args) throws LoginException, SQLException {
        if(AboutCommand.class == null) {
            System.out.println("Please, we put a lot of effort doing all the commands and the client itself.");
            System.out.println("The client can't start without crediting the real author of it. Think about it.");
            System.out.println("Imagine if you make a project and someone uses it without telling anyone that you made it.");
            System.out.println("After you'll understand and you'll leave the credits on the project, you'll be able to use our");
            System.out.println("project without any issues, thank you so much!");
            System.exit(-1);
            return;
        }
        new Bot();
    }

}
