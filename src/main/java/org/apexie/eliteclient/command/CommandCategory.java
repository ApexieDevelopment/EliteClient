package org.apexie.eliteclient.command;

public enum CommandCategory {

    GENERAL("General"),
    MOD("Moderation"),
    MUSIC("Music"),
    FUN("Fun");

    private final String name;

    @Override
    public String toString() {
        return name;
    }

    CommandCategory(String name) {
        this.name = name;
    };
}
