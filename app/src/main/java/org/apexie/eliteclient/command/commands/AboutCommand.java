package org.apexie.eliteclient.command.commands;

import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apexie.eliteclient.Bot;
import org.apexie.eliteclient.command.CommandContext;
import org.apexie.eliteclient.command.ICommand;

import java.util.List;

public class AboutCommand implements ICommand {
    @Override
    public void handle(CommandContext ctx) {
        final TextChannel channel = ctx.getChannel();
        final StringBuilder links = new StringBuilder();
        final StringBuilder authors = new StringBuilder();
        links.append("**» GitHub: ** https://github.com/ApexieDevelopment/EliteClient\n");
        links.append("**» Discord: ** https://discord.gg/a75eNEAtrt\n");
        links.append("**» Patreon: ** https://patreon.com/itzlightyhd\n");
        authors.append("**» ItzLightyHD: ** https://youtube.com/ItzLightyHD\n");
        String description = "This bot runs on [**EliteClient**](https://github.com/ApexieDevelopment/EliteClient) version " +
                Bot.ELITECLIENT_VERSION + " made by the Apexie Development " +
                "Team";
        if (Bot.IS_DEVELOPMENT_BUILD == true) description = "This bot runs on a development build of [**EliteClient**](https://github.com/ApexieDevelopment/EliteClient) version " +
                Bot.ELITECLIENT_VERSION + " made by the Apexie Development " +
                "Team";
        EmbedBuilder builder = EmbedUtils.embedMessage("")
                .setAuthor(ctx.getSelfMember().getEffectiveName())
                .setColor(ctx.getGuild().getSelfMember().getColor())
                .setThumbnail(ctx.getSelfUser().getEffectiveAvatarUrl())
                .setDescription(description)
                .addField("Links", links.toString(), false)
                .addField("Contributors", authors.toString(), false)
                .setFooter("Requested by " + ctx.getAuthor().getName(), ctx.getAuthor().getEffectiveAvatarUrl());
        channel.sendMessage(builder.build()).queue();
    }

    @Override
    public String getName() {
        return "about";
    }

    @Override
    public String getHelp() {
        return "Shows more about the Discord client that this bot runs on.";
    }

    @Override
    public String getUsage() {
        return "about";
    }

    @Override
    public List<String> getAliases() {
        return List.of("version", "ver", "info", "aboutme", "client");
    }
}
