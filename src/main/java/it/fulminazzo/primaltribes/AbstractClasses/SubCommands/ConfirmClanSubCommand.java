package it.fulminazzo.primaltribes.AbstractClasses.SubCommands;

import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.Enums.ClanPermission;
import it.fulminazzo.primaltribes.Enums.CommandType;
import it.fulminazzo.primaltribes.Enums.Message;
import it.fulminazzo.primaltribes.Enums.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class ConfirmClanSubCommand extends NumberClanSubCommand {
    public ConfirmClanSubCommand(AbstractCommand command, String name, Permissions permissions, ClanPermission clanPermission, String help, String description,
                                 CommandType commandType, String... aliases) {
        super(command, name, permissions, clanPermission, help, description, commandType, aliases);
    }

    public ConfirmClanSubCommand(AbstractCommand command, String name, Permissions permissions, ClanPermission clanPermission, String help, String description,
                                 boolean playerOnly, CommandType commandType, String... aliases) {
        super(command, name, permissions, clanPermission, help, description, playerOnly, commandType, aliases);
    }

    public void execute(CommandSender sender,
                        Runnable action) {
        if (!(sender instanceof Player))
            action.run();
        else {
            //TODO: in chat confirmation?
        }
    }
}