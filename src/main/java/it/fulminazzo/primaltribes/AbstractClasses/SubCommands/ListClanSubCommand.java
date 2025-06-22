package it.fulminazzo.primaltribes.AbstractClasses.SubCommands;

import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.Enums.ClanPermission;
import it.fulminazzo.primaltribes.Enums.CommandType;
import it.fulminazzo.primaltribes.Enums.Message;
import it.fulminazzo.primaltribes.Enums.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Consumer;

public abstract class ListClanSubCommand extends ClanSubCommand {

    public ListClanSubCommand(AbstractCommand command, String name, Permissions permissions, ClanPermission clanPermission, String help, String description,
                              CommandType commandType, String... aliases) {
        super(command, name, permissions, clanPermission, help, description, commandType, aliases);
    }

    public ListClanSubCommand(AbstractCommand command, String name, Permissions permissions, ClanPermission clanPermission, String help, String description,
                              boolean playerOnly, CommandType commandType, String... aliases) {
        super(command, name, permissions, clanPermission, help, description, playerOnly, commandType, aliases);
    }

    public void execute(CommandSender sender, Command cmd, String[] args, Message emptyPlayersMessage,
                        Message noPlayerSpecified,
                        List<Player> players, Consumer<Player> action) {
        if (args.length == 0) {
            if (players.isEmpty())
                sender.sendMessage(emptyPlayersMessage.getMessage(!isAdminCommand(), isAdminCommand()));
            else sender.sendMessage(noPlayerSpecified.getMessage(!isAdminCommand(), isAdminCommand()));
        } else {
            String argument = args[0];
            Player player = Bukkit.getPlayer(argument);
            if (player == null)
                sender.sendMessage(Message.PLAYER_NOT_FOUND.getMessage(!isAdminCommand(), isAdminCommand())
                        .replace("%player%", argument));
            else action.accept(player);
        }
    }

    public <T> void execute(CommandSender sender, String[] args, Message emptyObjectMessage,
                            Message noObjectSpecified,
                            List<T> objects, Consumer<String> action) {
        if (args.length == 0) {
            if (objects.isEmpty()) sender.sendMessage(emptyObjectMessage.getMessage(!isAdminCommand(), isAdminCommand()));
            else sender.sendMessage(noObjectSpecified.getMessage(!isAdminCommand(), isAdminCommand()));
        } else action.accept(args[0]);
    }
}