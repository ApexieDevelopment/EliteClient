package org.apexie.eliteclient.command.commands.mod;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apexie.eliteclient.command.CommandCategory;
import org.apexie.eliteclient.command.CommandContext;
import org.apexie.eliteclient.command.ICommand;
import org.apexie.eliteclient.command.UsageMessage;

import java.util.List;

public class ClearCommand implements ICommand {
    @Override
    public void handle(CommandContext ctx) {
        final List<String> args = ctx.getArgs();
        final TextChannel channel = ctx.getChannel();

        if (args.isEmpty()) {
            new UsageMessage(this, ctx);
            return;
        }

        try {
            List<Message> messages = channel.getHistory().retrievePast(Integer.parseInt(args.get(0))).complete();
            channel.deleteMessages(messages).queue();
        } catch (IllegalArgumentException e) {
            if (e.toString().startsWith("java.lang.IllegalArgumentException: Message retrieval")) {
                channel.sendMessage("You can select messages only between 1 and 100!").queue();
            } else {
                channel.sendMessage("The selected messages are older than 14 days.").queue();
            }
        }
    }

    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.MOD;
    }

    @Override
    public String getHelp() {
        return "Deletes a specified amount of messages. Due to the Discord API limitations, the bot can't delete messages older than 14 days and more than 100 messages.";
    }

    @Override
    public String getUsage() {
        return "clear <number>";
    }

    @Override
    public List<String> getAliases() {
        return List.of("purge", "delete", "clean");
    }
}
