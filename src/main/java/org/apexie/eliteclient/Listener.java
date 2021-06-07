package org.apexie.eliteclient;

import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.apexie.eliteclient.database.DatabaseManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Timer;
import java.util.TimerTask;

public class Listener extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);
    private final CommandManager manager = new CommandManager();
    public static String username;
    public static String clientAvatar;

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        Listener.username = event.getJDA().getSelfUser().getName();
        Listener.clientAvatar = event.getJDA().getSelfUser().getEffectiveAvatarUrl();
        if(Config.get("heroku").equals("yes")) {
            LOGGER.info("You're running the bot in Heroku mode: you won't be able");
            LOGGER.info("to run commands such as 'setprefix' and other commands that");
            LOGGER.info("use the database to save data.");
        }
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
        String prefix = MapsList.PREFIXES.computeIfAbsent(guildId, DatabaseManager.INSTANCE::getPrefix);
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
}
