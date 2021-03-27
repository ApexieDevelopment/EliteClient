package org.apexie.eliteclient.command.commands;

import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apexie.eliteclient.command.CommandContext;
import org.apexie.eliteclient.command.ICommand;

public class InviteCommand implements ICommand {

    @Override
    public void handle(CommandContext ctx) {
        ctx.getAuthor().openPrivateChannel().queue(
                (channel) -> channel.sendMessage("https://discord.com/api/oauth2/authorize?client_id=" + ctx.getSelfUser().getId() + "&permissions=8&scope=bot").queue()
        );
    }

    @Override
    public String getName() {
        return "invite";
    }

    @Override
    public String getHelp() {
        return "Sends an invite in the DMs";
    }

    @Override
    public String getUsage() {
        return "invite";
    }
}
