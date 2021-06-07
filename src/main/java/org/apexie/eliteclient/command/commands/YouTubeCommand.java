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

        /** WebUtils.ins.getJSONObject("https://apis.duncte123.me/meme").async((json) -> {
            if (!json.get("success").asBoolean()) {
                channel.sendMessage("Something went wrong, try again later").queue();
                System.out.println(json);
                return;
            }

            final JsonNode data = json.get("data");
            final String title = data.get("title").asText();
            final String url = data.get("url").asText();
            final String image = data.get("image").asText();
            final EmbedBuilder embed = EmbedUtils.embedImageWithTitle(title, url, image);

            channel.sendMessage(embed.build()).queue();
        });
         **/

        WebUtils.ins.getJSONObject(
                "https://discord-together-generator.glitch.me/channel/" + memberChannel.getId() + "/token/" + Config.get("token")
        ).async((json) -> {
            if (!json.get("success").asBoolean()) {
                channel.sendMessage("Something went wrong, try again later").queue();
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
