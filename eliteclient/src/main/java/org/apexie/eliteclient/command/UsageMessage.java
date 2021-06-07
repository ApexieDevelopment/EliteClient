package org.apexie.eliteclient.command;

import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apexie.eliteclient.MapsList;

public class UsageMessage {
    public UsageMessage(ICommand command, CommandContext ctx) {
        TextChannel channel = ctx.getChannel();
        String prefix = MapsList.PREFIXES.get(ctx.getGuild().getIdLong());

        EmbedBuilder builder = EmbedUtils.embedMessage("")
                .setAuthor("Some arguments are missing")
                .setColor(ctx.getSelfMember().getColor())
                .setDescription("It looks like need you to type more arguments for the `" + command.getName() + "` command.\n" +
                        "Look at the usage and you'll know how to use the command correctly.")
                .addField("Usage", "`" + prefix + command.getUsage() + "`", false)
                .setFooter("Triggered by " + ctx.getAuthor().getName(), ctx.getAuthor().getEffectiveAvatarUrl());

        channel.sendMessage(builder.build()).queue();
    }
}
