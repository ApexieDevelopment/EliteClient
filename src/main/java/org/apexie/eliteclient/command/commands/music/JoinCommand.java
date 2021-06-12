package org.apexie.eliteclient.command.commands.music;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import org.apexie.eliteclient.command.CommandCategory;
import org.apexie.eliteclient.command.CommandContext;
import org.apexie.eliteclient.command.ICommand;

@SuppressWarnings("ConstantConditions")
public class JoinCommand implements ICommand {
    @Override
    public void handle(CommandContext ctx) {
        final TextChannel channel = ctx.getChannel();
        final Member self = ctx.getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();

        if (selfVoiceState.inVoiceChannel()) {
            channel.sendMessage("I'm already in a voice channel!").queue();
            return;
        }

        final Member member = ctx.getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        if(!memberVoiceState.inVoiceChannel()) {
            channel.sendMessage("You need to be in a voice channel!").queue();
            return;
        }

        final AudioManager audioManager = ctx.getGuild().getAudioManager();
        final VoiceChannel memberChannel = memberVoiceState.getChannel();

        audioManager.openAudioConnection(memberChannel);
        channel.sendMessageFormat("Connecting to `\uD83D\uDD0A %s`", memberChannel.getName()).queue();
    }

    @Override
    public String getName() {
        return "join";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MUSIC;
    }

    @Override
    public String getHelp() {
        return "Makes the bot join your voice channel.";
    }

    @Override
    public String getUsage() {
        return "join";
    }
}
