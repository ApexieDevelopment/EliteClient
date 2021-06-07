package org.apexie.eliteclient.command;

import java.util.List;

public interface ICommand {
    void handle(CommandContext ctx);

    String getName();

    String getHelp();

    String getUsage();

    default List<String> getAliases() {
        return List.of(); // Arrays.asList if using Java 8
    }

}