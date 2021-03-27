package org.apexie.eliteclient.command.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apexie.eliteclient.command.CommandContext;
import org.apexie.eliteclient.command.ICommand;
import org.apexie.eliteclient.lavaplayer.GuildMusicManager;
import org.apexie.eliteclient.lavaplayer.PlayerManager;

public class SkipCommand implements ICommand {
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

        final AudioPlayer audioPlayer = musicManager.audioPlayer;

        if (audioPlayer.getPlayingTrack() == null) {
            channel.sendMessage("There's no track playing currently.").queue();
            return;
        }

        musicManager.scheduler.nextTrack();
        EmbedBuilder builder = EmbedUtils.embedMessage("")
                .setColor(ctx.getGuild().getSelfMember().getColor())
                .setDescription("Now playing [**__" + audioPlayer.getPlayingTrack().getInfo().title +
                        "__**](" + audioPlayer.getPlayingTrack().getInfo().uri + ") by **" +
                        audioPlayer.getPlayingTrack().getInfo().author + "**")
                .setThumbnail("https://img.youtube.com/vi/" + audioPlayer.getPlayingTrack().getInfo().identifier + "/maxresdefault.jpg");
        channel.sendMessage(builder.build()).queue();
    }

    @Override
    public String getName() {
        return "skip";
    }

    @Override
    public String getHelp() {
        return "Skips the current playing music and plays the next track.";
    }

    @Override
    public String getUsage() {
        return "skip";
    }
}
