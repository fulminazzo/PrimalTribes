package it.fulminazzo.primaltribes.AbstractClasses.SubCommands;

import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.Enums.CommandType;
import it.fulminazzo.primaltribes.Enums.Permissions;

import java.util.List;

public abstract class SuperSubCommand extends SubCommand {
    private List<SubCommand> subCommands;

    public SuperSubCommand(AbstractCommand command, String name, Permissions permissions, String help, String description, CommandType commandType, String... aliases) {
        super(command, name, permissions, help, description, commandType, aliases);
    }

    public SuperSubCommand(AbstractCommand command, String name, Permissions permissions, String help, String description, boolean playerOnly,
                           CommandType commandType, String... aliases) {
        super(command, name, permissions, help, description, playerOnly, commandType, aliases);
    }

    public void addSubCommand(SubCommand subCommand) {
        this.subCommands.add(subCommand);
    }

    public void removeSubCommand(String subCommandName) {
        this.subCommands.removeIf(s -> s.getName().equalsIgnoreCase(subCommandName));
    }

    public void removeSubCommand(SubCommand subCommand) {
        this.subCommands.remove(subCommand);
    }

    public void setSubCommands(List<SubCommand> subCommands) {
        this.subCommands = subCommands;
    }

    public List<SubCommand> getSubCommands() {
        return subCommands;
    }
}