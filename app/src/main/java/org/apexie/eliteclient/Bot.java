package org.apexie.eliteclient;

import me.duncte123.botcommons.messaging.EmbedUtils;
import me.duncte123.botcommons.web.WebUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.util.EnumSet;

public class Bot {

    public static final String ELITECLIENT_VERSION = "Alpha 1.1.1";
    public static final Boolean IS_DEVELOPMENT_BUILD = false;
    public static final String BOT_TOKEN = Config.get("token");
    public static final String APP_ID = Config.get("app_id");

    public Bot() throws LoginException {
        WebUtils.setUserAgent("Mozilla/5.0");

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
            .setRawEventsEnabled(true)
            .build();
    }

    public static void main(String[] args) throws LoginException {
        if (IS_DEVELOPMENT_BUILD == true) {
            DevBot.main(args);
        } else {
            new Bot();
        }
    }

}
