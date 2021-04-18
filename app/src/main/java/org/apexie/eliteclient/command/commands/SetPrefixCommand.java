package org.apexie.eliteclient.command.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apexie.eliteclient.MapsList;
import org.apexie.eliteclient.command.CommandContext;
import org.apexie.eliteclient.command.ICommand;
import org.apexie.eliteclient.command.MissingPermissionMessage;
import org.apexie.eliteclient.command.UsageMessage;
import org.apexie.eliteclient.database.DatabaseManager;
import java.util.List;

public class SetPrefixCommand implements ICommand {
    @Override
    public void handle(CommandContext ctx) {
        final TextChannel channel = ctx.getChannel();
        final List<String> args = ctx.getArgs();
        final Member member = ctx.getMember();

        if (!member.hasPermission(Permission.MANAGE_SERVER)) {
            new MissingPermissionMessage(this, ctx, "Manage Server");
            return;
        }

        if (args.isEmpty()) {
            new UsageMessage(this, ctx);
            return;
        }

        final String newPrefix = String.join("", args);
        updatePrefix(ctx.getGuild().getIdLong(), newPrefix);

        channel.sendMessageFormat("New prefix has been set to `%s`", newPrefix).queue();
    }

    @Override
    public String getName() {
        return "setprefix";
    }

    @Override
    public String getHelp() {
        return "Sets the prefix for this server";
    }

    private void updatePrefix(long guildId, String newPrefix) {
        MapsList.PREFIXES.put(guildId, newPrefix);
        DatabaseManager.INSTANCE.setPrefix(guildId, newPrefix);
    }

    @Override
    public String getUsage() {
        return "setprefix <newprefix>";
    }
}
