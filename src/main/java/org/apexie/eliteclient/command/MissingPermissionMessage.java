package org.apexie.eliteclient.command;

import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apexie.eliteclient.MapsList;

public class MissingPermissionMessage {
    public MissingPermissionMessage(ICommand command, CommandContext ctx, String permission, boolean self) {
        TextChannel channel = ctx.getChannel();

        if (self == false) {
            EmbedBuilder builder = EmbedUtils.embedMessage("")
                    .setAuthor("You are missing some permissions")
                    .setColor(ctx.getSelfMember().getColor())
                    .setDescription("It looks like you need the `" + permission + "` permission to use the `" + command.getName() + "` command")
                    .setFooter("Triggered by " + ctx.getAuthor().getName(), ctx.getAuthor().getEffectiveAvatarUrl());

            channel.sendMessage(builder.build()).queue();
        } else if (self) {
            EmbedBuilder builder = EmbedUtils.embedMessage("")
                    .setAuthor("I'm missing some permissions")
                    .setColor(ctx.getSelfMember().getColor())
                    .setDescription("It looks like I need the `" + permission + "` permission to make the `" + command.getName() + "` command working.\n" +
                            "Add the permission to my role and you're done.")
                    .setFooter("Triggered by " + ctx.getAuthor().getName(), ctx.getAuthor().getEffectiveAvatarUrl());

            channel.sendMessage(builder.build()).queue();
        }
    }

    public MissingPermissionMessage(ICommand command, CommandContext ctx, String permission) {
        TextChannel channel = ctx.getChannel();
        String prefix = MapsList.PREFIXES.get(ctx.getGuild().getIdLong());

        EmbedBuilder builder = EmbedUtils.embedMessage("")
                .setAuthor("You are missing some permissions")
                .setColor(ctx.getSelfMember().getColor())
                .setDescription("It looks like you need the `" + permission + "` permission to use the `" + command.getName() + "` command")
                .setFooter("Triggered by " + ctx.getAuthor().getName(), ctx.getAuthor().getEffectiveAvatarUrl());

        channel.sendMessage(builder.build()).queue();
    }
}
