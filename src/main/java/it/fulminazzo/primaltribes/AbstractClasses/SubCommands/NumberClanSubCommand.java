package it.fulminazzo.primaltribes.AbstractClasses.SubCommands;

import it.angrybear.Utils.NumberUtils;
import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.Enums.ClanPermission;
import it.fulminazzo.primaltribes.Enums.CommandType;
import it.fulminazzo.primaltribes.Enums.Message;
import it.fulminazzo.primaltribes.Enums.Permissions;
import org.bukkit.command.CommandSender;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToLongFunction;

public abstract class NumberClanSubCommand extends ListClanSubCommand {
    public NumberClanSubCommand(AbstractCommand command, String name, Permissions permissions, ClanPermission clanPermission, String help, String description,
                                CommandType commandType, String... aliases) {
        super(command, name, permissions, clanPermission, help, description, commandType, aliases);
    }

    public NumberClanSubCommand(AbstractCommand command, String name, Permissions permissions, ClanPermission clanPermission, String help, String description,
                                boolean playerOnly, CommandType commandType, String... aliases) {
        super(command, name, permissions, clanPermission, help, description, playerOnly, commandType, aliases);
    }

    public void execute(CommandSender sender, String[] args, Message cancelled, Message guiTitle,
                        Message itemTitle, Message guiLore, int current, int minimum, long maximum,
                        Consumer<Long> action) {
        execute(sender, args, cancelled, guiTitle, itemTitle, guiLore, minimum, i -> maximum, maximum, action);
    }

    public <T> void execute(CommandSender sender, String[] args, Message cancelled, Message guiTitle,
                            Message itemTitle, Message guiLore, int minimum, ToLongFunction<T> maximum, T maximumInt,
                            Consumer<Long> action) {
        execute(sender, args, minimum, maximum,
                maximumInt, String::valueOf, action);
    }

    public void execute(CommandSender sender, String[] args, Message cancelled, Message guiTitle,
                        Message itemTitle, Message guiLore, int minimum, long maximum,
                        Function<Long, String> toString, Consumer<Long> action) {
        execute(sender, args, minimum, i -> maximum,
                maximum, toString, action);
    }

    public <T> void execute(CommandSender sender, String[] args,
                            int minimum, ToLongFunction<T> maximum, T maximumInt,
                            Function<Long, String> toString, Consumer<Long> action) {
        if (args.length == 0) {
            sender.sendMessage(Message.NOT_VALID_NUMBER.getMessage(!isAdminCommand(), isAdminCommand())
                    .replace("%minimum%", toString.apply((long) minimum))
                    .replace("%maximum%", String.valueOf(maximum.applyAsLong(maximumInt))));
        } else {
            String argument = args[0];
            if (!NumberUtils.isLong(argument)) {
                sender.sendMessage(Message.NOT_VALID_NUMBER.getMessage(!isAdminCommand(), isAdminCommand())
                        .replace("%minimum%", toString.apply((long) minimum))
                        .replace("%maximum%", String.valueOf(maximum.applyAsLong(maximumInt))));
            } else {
                long num = Long.parseLong(argument);
                if (num < minimum || num > maximum.applyAsLong(maximumInt))
                    sender.sendMessage(Message.NOT_VALID_NUMBER.getMessage(!isAdminCommand(), isAdminCommand())
                            .replace("%minimum%", toString.apply((long) minimum))
                            .replace("%maximum%", String.valueOf(maximum.applyAsLong(maximumInt))));
                else action.accept(num);
            }
        }
    }
}