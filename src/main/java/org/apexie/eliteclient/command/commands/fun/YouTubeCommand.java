package org.apexie.eliteclient.command.commands.fun;

import com.fasterxml.jackson.databind.JsonNode;
import me.duncte123.botcommons.messaging.EmbedUtils;
import me.duncte123.botcommons.web.WebUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.apexie.eliteclient.Config;
import org.apexie.eliteclient.command.CommandCategory;
import org.apexie.eliteclient.command.CommandContext;
import org.apexie.eliteclient.command.ICommand;

import java.util.List;

public class YouTubeCommand implements ICommand {
    @Override
    public void handle(CommandContext ctx) {
        final TextChannel channel = ctx.getChannel();

        final Member member = ctx.getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        if(!memberVoiceState.inVoiceChannel()) {
            channel.sendMessage("You need to be in a voice channel!").queue();
            return;
        }

        final VoiceChannel memberChannel = memberVoiceState.getChannel();

        WebUtils.ins.getJSONObject(
                "https://discord-together-generator.glitch.me/app/youtube/channel/" + memberChannel.getId() + "/token/" + Config.get("token")
        ).async((json) -> {
            if (!json.get("success").asBoolean()) {
                if(json.get("error").asText() == "NO_ARGUMENTS") {
                    channel.sendMessage("Something went wrong, try again later").queue();
                } else if(json.get("error").asText() == "NO_INVITE_CODE") {
                    channel.sendMessage("Something went wrong while getting the invite code, try again later.").queue();
                } else if(json.get("error").asText() == "INVALID_APPLICATION_ID") {
                    channel.sendMessage("Something went wrong, try again later").queue();
                }
                System.out.println(json);
                return;
            }

            final JsonNode data = json.get("data");
            final String invite = data.get("invite").asText();

            EmbedBuilder builder = EmbedUtils.embedMessage("")
                    .setAuthor("YouTube Together")
                    .setColor(ctx.getSelfMember().getColor())
                    .setDescription("This feature allows you to watch YouTube along with other people in a voice chat. Click the link down below to start the fun.")
                    .addField("Invite link", invite, false);
            channel.sendMessage(builder.build()).queue();
        });
    }

    @Override
    public String getName() {
        return "youtube";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.FUN;
    }

    @Override
    public String getHelp() {
        return "Allows users to watch YouTube Together in a Discord voice chat";
    }

    @Override
    public String getUsage() {
        return "youtube";
    }

    @Override
    public List<String> getAliases() {
        return List.of("yt", "youtubetogether", "ytparty", "yttogether");
    }
}
