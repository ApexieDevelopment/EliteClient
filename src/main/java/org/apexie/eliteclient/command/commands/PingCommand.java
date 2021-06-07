package org.apexie.eliteclient.command.commands;

import net.dv8tion.jda.api.JDA;
import org.apexie.eliteclient.command.CommandContext;
import org.apexie.eliteclient.command.ICommand;

public class PingCommand implements ICommand {
    @Override
    public void handle(CommandContext ctx) {
        JDA jda = ctx.getJDA();

        jda.getRestPing().queue(
                (ping) -> ctx.getChannel()
                .sendMessageFormat("Reset ping %sms\nWS ping: %sms", ping, jda.getGatewayPing()).queue()
        );
    }

    @Override
    public String getName() { return "ping"; }

    @Override
    public String getHelp() {
        return "Shows the current ping from the bot to the Discord servers.";
    }

    @Override
    public String getUsage() {
        return "ping";
    }
}
