package it.fulminazzo.primaltribes.Enums;

public enum CommandName {
    CLAN("clan"),
    GANG("gang"),
    GUILD("guild"),
    TEAM("team");

    private final String commandName;

    CommandName(String commandName) {
        this.commandName = commandName;
    }

    public String getName() {
        return commandName;
    }
}