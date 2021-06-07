package org.apexie.eliteclient.command.commands.music;

import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apexie.eliteclient.command.CommandContext;
import org.apexie.eliteclient.command.ICommand;
import org.apexie.eliteclient.lavaplayer.GuildMusicManager;
import org.apexie.eliteclient.lavaplayer.PlayerManager;

import java.util.List;

public class RepeatCommand implements ICommand {
    @Override
    public void handle(CommandContext ctx) {
        final TextChannel channel = ctx.getChannel();
        final Member self = ctx.getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();

        if (!selfVoiceState.inVoiceChannel()) {
            channel.sendMessage("I need to be in a voice channel for this to work").queue();
            return;
        }

        final Member member = ctx.getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        if (!memberVoiceState.inVoiceChannel()) {
            channel.sendMessage("You need to be in a voice channel for this command to work").queue();
            return;
        }

        if (!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())) {
            channel.sendMessage("You need to be in the same voice channel as me for this to work").queue();
            return;
        }

        final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());
        final boolean newRepeating = !musicManager.scheduler.repeating;

        if (musicManager.scheduler.repeating == false) {
            EmbedBuilder builder = EmbedUtils.embedMessage("")
                    .setAuthor("Music Player")
                    .setColor(channel.getGuild().getSelfMember().getColor())
                    .setDescription("The music player will repeat the current song. To disable this, execute this command again.")
                    .setFooter("Requested by " + ctx.getAuthor().getName(), ctx.getAuthor().getEffectiveAvatarUrl());
            channel.sendMessage(builder.build()).queue();
        } else if (musicManager.scheduler.repeating == true) {
            EmbedBuilder builder = EmbedUtils.embedMessage("")
                    .setAuthor("Music Player")
                    .setColor(channel.getGuild().getSelfMember().getColor())
                    .setDescription("The music player will not repeat the current song anymore and it will instead play the next track in the queue (if there's one). To re-enable this, execute this command again.")
                    .setFooter("Requested by " + ctx.getAuthor().getName(), ctx.getAuthor().getEffectiveAvatarUrl());
            channel.sendMessage(builder.build()).queue();
        }

        musicManager.scheduler.repeating = newRepeating;
    }

    @Override
    public String getName() {
        return "repeat";
    }

    @Override
    public String getHelp() {
        return "Loops the current track.";
    }

    @Override
    public String getUsage() {
        return "repeat";
    }

    @Override
    public List<String> getAliases() {
        return List.of("loop");
    }
}
