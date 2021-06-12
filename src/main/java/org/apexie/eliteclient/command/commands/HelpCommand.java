package org.apexie.eliteclient.command.commands;

import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apexie.eliteclient.CommandManager;
import org.apexie.eliteclient.Config;
import org.apexie.eliteclient.MapsList;
import org.apexie.eliteclient.command.CommandCategory;
import org.apexie.eliteclient.command.CommandContext;
import org.apexie.eliteclient.command.ICommand;

import java.util.List;
import java.util.stream.Collectors;

public class HelpCommand implements ICommand {

    private final CommandManager manager;

    public HelpCommand(CommandManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(CommandContext ctx) {
        List<String> args = ctx.getArgs();
        TextChannel channel = ctx.getChannel();
        String prefix = MapsList.PREFIXES.get(ctx.getGuild().getIdLong());

        EmbedBuilder builder = EmbedUtils.embedMessage("")
                .setAuthor(ctx.getGuild().getName() + " Help Menu", ctx.getGuild().getIconUrl())
                .setColor(ctx.getSelfMember().getColor())
                .setThumbnail(ctx.getSelfUser().getEffectiveAvatarUrl())
                .setFooter("Requested by " + ctx.getAuthor().getName(), ctx.getAuthor().getEffectiveAvatarUrl());

        if (args.isEmpty()) {

            /* StringBuilder help_commands = new StringBuilder();

            manager.getCommands().stream().map(ICommand::getName).forEach(
                    (it) -> help_commands.append('`').append(prefix).append(it).append("` ")
            ); */

            builder.setDescription("These are the available commands for " + ctx.getGuild().getName() + "\n" +
                                "The bot prefix is: " + prefix + "\n" +
                                "Command Parameters: '<>' is for strict & '[]' is optional");

            // builder.addField("Commands", help_commands.toString(), false);

            for (CommandCategory category : CommandCategory.values()) {
                StringBuilder commands = new StringBuilder();

                List<ICommand> categoryCommands = manager.getCommands().stream()
                        .filter(cmd -> cmd.getCategory() == category)
                        .collect(Collectors.toList());

                categoryCommands.forEach(
                        (it) ->
                        commands.append('`').append(it.getName()).append("` ")
                );

                builder.addField(category.toString(), commands.toString(), false);
            }

            channel.sendMessage(builder.build()).queue();
            return;
        }

        String search = args.get(0);
        ICommand command = manager.getCommand(search);

        if (command == null) {
            channel.sendMessage("Unknown command: " + search).queue();
            return;
        }

        StringBuilder cmd_aliases = new StringBuilder();

        command.getAliases().forEach(
                (it) -> cmd_aliases.append('`').append(it).append("` ")
        );

        builder.setAuthor("'" + command.getName() + "' Command Help", ctx.getSelfUser().getEffectiveAvatarUrl());
        builder.setDescription("**» Aliases: **" + cmd_aliases + "\n" +
                            "**» Description: **" + command.getHelp() + "\n" +
                            "**» Usage: **`" + Config.get("prefix") + command.getUsage() + "`");

        channel.sendMessage(builder.build()).queue();
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getHelp() {
        return "Shows the list with commands in the bot";
    }

    @Override
    public String getUsage() {
        return "help [command]";
    }

    @Override
    public List<String> getAliases() {
        return List.of("commands", "cmds", "commandlist");
    }
}
