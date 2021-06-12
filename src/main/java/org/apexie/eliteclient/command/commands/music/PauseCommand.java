package org.apexie.eliteclient.command.commands.music;

import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apexie.eliteclient.command.CommandCategory;
import org.apexie.eliteclient.command.CommandContext;
import org.apexie.eliteclient.command.ICommand;
import org.apexie.eliteclient.lavaplayer.PlayerManager;

import java.util.List;

public class PauseCommand implements ICommand {
    @Override
    public void handle(CommandContext ctx) {
        final TextChannel channel = ctx.getChannel();
        final Member self = ctx.getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();

        final Member member = ctx.getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        if (!memberVoiceState.inVoiceChannel()) {
            channel.sendMessage("You need to be in a voice channel!").queue();
            return;
        }
        if (!selfVoiceState.inVoiceChannel()) {
            channel.sendMessage("I need to be in a voice channel for this to work").queue();
            return;
        }
        if (!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())) {
            channel.sendMessage("You need to be in the same voice channel as me for this to work").queue();
            return;
        }

        if (PlayerManager.getInstance().getMusicManager(ctx.getGuild()).audioPlayer.isPaused() == false) {
            EmbedBuilder builder = EmbedUtils.embedMessage("")
                    .setAuthor("Music Player")
                    .setColor(channel.getGuild().getSelfMember().getColor())
                    .setDescription("The music player was paused. To resume it, execute the same command.")
                    .setFooter("Requested by " + ctx.getAuthor().getName(), ctx.getAuthor().getEffectiveAvatarUrl());
            channel.sendMessage(builder.build()).queue();
            PlayerManager.getInstance().setPaused(true, channel);
        } else if (PlayerManager.getInstance().getMusicManager(ctx.getGuild()).audioPlayer.isPaused() == true) {
            EmbedBuilder builder = EmbedUtils.embedMessage("")
                    .setAuthor("Music Player")
                    .setColor(channel.getGuild().getSelfMember().getColor())
                    .setDescription("The music player was resumed. To pause it, execute the same command.")
                    .setFooter("Requested by " + ctx.getAuthor().getName(), ctx.getAuthor().getEffectiveAvatarUrl());
            channel.sendMessage(builder.build()).queue();
            PlayerManager.getInstance().setPaused(false, channel);
        }
    }

    @Override
    public String getName() {
        return "pause";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MUSIC;
    }

    @Override
    public String getHelp() {
        return "Pauses/Resumes the current track.";
    }

    @Override
    public String getUsage() {
        return "pause";
    }

    @Override
    public List<String> getAliases() {
        return List.of("resume");
    }
}
