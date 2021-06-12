package org.apexie.eliteclient.command;

import java.util.List;

public interface ICommand {
    void handle(CommandContext ctx);

    String getName();

    default CommandCategory getCategory() {
        return CommandCategory.GENERAL;
    };

    String getHelp();

    String getUsage();

    default List<String> getAliases() {
        return List.of();
    }

}