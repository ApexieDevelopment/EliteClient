package org.apexie.eliteclient.command.commands.mod;

import org.apexie.eliteclient.command.*;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class KickCommand implements ICommand {

    @Override
    public void handle(CommandContext ctx) {
        final TextChannel channel = ctx.getChannel();
        final Message message = ctx.getMessage();
        final Member member = ctx.getMember();
        final List<String> args = ctx.getArgs();

        if (args.size() < 2 || message.getMentionedMembers().isEmpty()) {
            new UsageMessage(this, ctx);
            return;
        }

        final Member target = message.getMentionedMembers().get(0);

        if (!member.canInteract(target) || !member.hasPermission(Permission.KICK_MEMBERS)) {
            new MissingPermissionMessage(this, ctx, "Kick Members");
            return;
        }

        final Member selfMember = ctx.getSelfMember();

        if (!selfMember.canInteract(target) || !selfMember.hasPermission(Permission.KICK_MEMBERS)) {
            new MissingPermissionMessage(this, ctx, "Kick Members", true);
            return;
        }

        final String reason = String.join(" ", args.subList(1, args.size()));

        ctx.getGuild()
                .kick(target, reason)
                .reason(reason)
                .queue(
                        (__) -> channel.sendMessage("Kick was successful").queue(),
                        (error) -> channel.sendMessageFormat("Could not kick %s", error.getMessage()).queue()
                );
    }

    @Override
    public String getName() {
        return "kick";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MOD;
    }

    @Override
    public String getHelp() {
        return "Kick a member off the server.";
    }

    @Override
    public String getUsage() {
        return "kick <@user> <reason>";
    }
}
