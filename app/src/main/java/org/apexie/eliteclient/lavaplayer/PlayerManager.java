package org.apexie.eliteclient.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerManager {
    private static PlayerManager INSTANCE;

    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;

    public PlayerManager() {
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public GuildMusicManager getMusicManager(Guild guild) {
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);
            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());
            return guildMusicManager;
        });
    }

    public void unQueue(TextChannel channel) {
        final GuildMusicManager musicManager = this.getMusicManager(channel.getGuild());
        musicManager.audioPlayer.destroy();
    }

    public void setPaused(boolean state, TextChannel channel) {
        final GuildMusicManager musicManager = this.getMusicManager(channel.getGuild());
        musicManager.audioPlayer.setPaused(state);
    }

    public void loadAndPlay(TextChannel channel, String trackUrl) {
        final GuildMusicManager musicManager = this.getMusicManager(channel.getGuild());
        this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                EmbedBuilder builder = EmbedUtils.embedMessage("")
                        .setColor(channel.getGuild().getSelfMember().getColor())
                        .setDescription("Adding to queue [**__" +
                                track.getInfo().title +
                                "__**](" + track.getInfo().uri + ") by **" +
                                track.getInfo().author + "**")
                        .setThumbnail("https://img.youtube.com/vi/" + track.getInfo().identifier + "/maxresdefault.jpg");
                channel.sendMessage(builder.build()).queue();
                musicManager.scheduler.queue(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if (playlist.isSearchResult()) {
                    AudioTrack firstTrack = playlist.getSelectedTrack();
                    if (firstTrack == null) {
                        firstTrack = playlist.getTracks().remove(0);
                    }
                    EmbedBuilder builder = EmbedUtils.embedMessage("")
                            .setColor(channel.getGuild().getSelfMember().getColor())
                            .setDescription("Adding to queue [**__" +
                                    firstTrack.getInfo().title +
                                    "__**](" + firstTrack.getInfo().uri + ") by **" +
                                    firstTrack.getInfo().author + "**")
                            .setThumbnail("https://img.youtube.com/vi/" + firstTrack.getInfo().identifier + "/maxresdefault.jpg");
                    channel.sendMessage(builder.build()).queue();
                    musicManager.scheduler.queue(firstTrack);
                } else {
                    final List<AudioTrack> tracks = playlist.getTracks();
                    AudioTrack firstTrack = playlist.getSelectedTrack();
                    EmbedBuilder builder = EmbedUtils.embedMessage("")
                            .setColor(channel.getGuild().getSelfMember().getColor())
                            .setDescription("Adding to queue `" + String.valueOf(tracks.size()) +
                                    "` tracks from playlist **__" + playlist.getName() + "__**");
                    channel.sendMessage(builder.build()).queue();
                    for (final AudioTrack track : tracks) {
                        musicManager.scheduler.queue(track);
                    }
                }
            }

            @Override
            public void noMatches() {

            }

            @Override
            public void loadFailed(FriendlyException exception) {
                channel.sendMessage("Could not play: " + exception.getMessage()).queue();
            }
        });
    }

    public static PlayerManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }

        return INSTANCE;
    }
}
