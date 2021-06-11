package org.apexie.eliteclient.command.commands;

import com.fasterxml.jackson.databind.JsonNode;
import me.duncte123.botcommons.messaging.EmbedUtils;
import me.duncte123.botcommons.web.WebUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.apexie.eliteclient.Config;
import org.apexie.eliteclient.command.CommandContext;
import org.apexie.eliteclient.command.ICommand;

public class FishingCommand implements ICommand {
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
                "https://discord-together-generator.glitch.me/app/fishing/channel/" + memberChannel.getId() + "/token/" + Config.get("token")
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
                    .setAuthor("Discord Together - Fishing")
                    .setColor(ctx.getSelfMember().getColor())
                    .setDescription("Have fun by playing Fishing with your friends while talking in the voice chat. Click the link down below to start the game.")
                    .addField("Invite link", invite, false);
            channel.sendMessage(builder.build()).queue();
        });
    }

    @Override
    public String getName() {
        return "fishing";
    }

    @Override
    public String getHelp() {
        return "Allows users to play Fishing together in a Discord voice chat";
    }

    @Override
    public String getUsage() {
        return "fishing";
    }
}
