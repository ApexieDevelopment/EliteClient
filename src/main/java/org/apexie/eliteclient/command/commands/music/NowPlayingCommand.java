package org.apexie.eliteclient.command.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apexie.eliteclient.command.CommandCategory;
import org.apexie.eliteclient.command.CommandContext;
import org.apexie.eliteclient.command.ICommand;
import org.apexie.eliteclient.lavaplayer.GuildMusicManager;
import org.apexie.eliteclient.lavaplayer.PlayerManager;

import java.util.List;

public class NowPlayingCommand implements ICommand {
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

        EmbedBuilder builder = EmbedUtils.embedMessage("")
                .setColor(ctx.getGuild().getSelfMember().getColor())
                .setThumbnail("https://img.youtube.com/vi/" + audioPlayer.getPlayingTrack().getInfo().identifier + "/maxresdefault.jpg")
                .setFooter("Requested by " + ctx.getAuthor().getName(), ctx.getAuthor().getEffectiveAvatarUrl());
        if(musicManager.scheduler.repeating) {
            builder.setDescription("Now playing [**__" + audioPlayer.getPlayingTrack().getInfo().title +
                    "__**](" + audioPlayer.getPlayingTrack().getInfo().uri + ") by **" +
                    audioPlayer.getPlayingTrack().getInfo().author + "** (repeating)");
        } else {
            builder.setDescription("Now playing [**__" + audioPlayer.getPlayingTrack().getInfo().title +
                    "__**](" + audioPlayer.getPlayingTrack().getInfo().uri + ") by **" +
                    audioPlayer.getPlayingTrack().getInfo().author + "**");
        }
        channel.sendMessage(builder.build()).queue();
    }

    @Override
    public String getName() {
        return "nowplaying";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MUSIC;
    }

    @Override
    public String getHelp() {
        return "Shows the info of the currently playing track";
    }

    @Override
    public String getUsage() {
        return "nowplaying";
    }

    @Override
    public List<String> getAliases() {
        return List.of("playing", "currenttrack", "currentmusic", "playingnow", "np");
    }
}
