package org.apexie.eliteclient.command.commands.music;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apexie.eliteclient.command.CommandCategory;
import org.apexie.eliteclient.command.CommandContext;
import org.apexie.eliteclient.command.ICommand;
import org.apexie.eliteclient.command.UsageMessage;
import org.apexie.eliteclient.lavaplayer.PlayerManager;

import java.net.URI;
import java.net.URISyntaxException;

public class PlayCommand implements ICommand {
    @SuppressWarnings("ConstantConditions")
    @Override
    public void handle(CommandContext ctx) {
        final TextChannel channel = ctx.getChannel();

        if (ctx.getArgs().isEmpty()) {
            new UsageMessage(this, ctx);
            return;
        }

        final Member member = ctx.getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        final Member self = ctx.getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();

        if (!selfVoiceState.inVoiceChannel()) {
            //channel.sendMessage("I need to be in a voice channel for this to work!").queue();
            if(!memberVoiceState.inVoiceChannel()) {
                channel.sendMessage("You need to be in a voice channel!").queue();
                return;
            }
            new JoinCommand().handle(ctx);
            //return;
        } else {
            if (!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())) {
                channel.sendMessage("You need to be in the same voice channel as me for this to work").queue();
                return;
            }
        }

        String link = String.join(" ", ctx.getArgs());

        if(!isUrl(link)) {
            link = "ytsearch:" + link;
        }

        PlayerManager.getInstance().loadAndPlay(channel, link);
    }

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MUSIC;
    }

    @Override
    public String getHelp() {
        return "Plays a song on your current voice channel";
    }

    @Override
    public String getUsage() {
        return "play <youtube link/search>";
    }

    private boolean isUrl(String url) {
        try {
            new URI(url);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
