package it.fulminazzo.primaltribes.AbstractClasses.SubCommands;

import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.Enums.ClanPermission;
import it.fulminazzo.primaltribes.Enums.CommandType;
import it.fulminazzo.primaltribes.Enums.Permissions;
import it.fulminazzo.primaltribes.Objects.Users.ClanPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class SuperClanSubCommand extends ClanSubCommand {
    private List<ClanSubCommand> subCommands;

    public SuperClanSubCommand(AbstractCommand command, String name, Permissions permissions, ClanPermission clanPermission, String help, String description, CommandType commandType, String... aliases) {
        super(command, name, permissions, clanPermission, help, description, commandType, aliases);
    }

    public SuperClanSubCommand(AbstractCommand command, String name, Permissions permissions, ClanPermission clanPermission, String help, String description, boolean playerOnly, CommandType commandType, String... aliases) {
        super(command, name, permissions, clanPermission, help, description, playerOnly, commandType, aliases);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String[] args) {
        List<String> list = new ArrayList<>();
        checkClan(sender);
        if (args.length == 1)
            list = getSubCommands().stream().filter(s -> validateSubCommand(s, sender))
                    .flatMap(s -> Stream.concat(
                            Stream.of(s.getName()),
                            Arrays.stream(s.getAliases())
                    )).collect(Collectors.toList());

        if (args.length >= 2) {
            ClanSubCommand subCommand = getSubCommand(args[0]);
            if (subCommand != null) {
                subCommand.setClan(clan);
                if (validateSubCommand(subCommand, sender))
                    list = subCommand.onTabComplete(sender, cmd, Arrays.copyOfRange(args, 1, args.length));
            }
        }
        return list;
    }

    public void addSubCommand(ClanSubCommand subCommand) {
        this.subCommands.add(subCommand);
    }

    public void removeSubCommand(String subCommandName) {
        this.subCommands.removeIf(s -> s.getName().equalsIgnoreCase(subCommandName));
    }

    public void removeSubCommand(ClanSubCommand subCommand) {
        this.subCommands.remove(subCommand);
    }

    public void setSubCommands(List<ClanSubCommand> subCommands) {
        this.subCommands = subCommands;
    }

    public List<ClanSubCommand> getSubCommands() {
        return subCommands;
    }

    protected boolean validateSubCommand(ClanSubCommand subCommand, CommandSender sender) {
        if (subCommand == null) return false;
        if (!sender.hasPermission(subCommand.getPermission())) return false;
        if (isAdminCommand()) return true;
        if (!(sender instanceof Player)) return false;
        ClanPlayer clanPlayer = getClanPlayersManager().getClanPlayer((Player) sender);
        return clanPlayer.hasPermission(subCommand.getClanPermission());
    }

    protected ClanSubCommand getSubCommand(String name) {
        return getSubCommands().stream()
                .filter(s -> s.getName().equalsIgnoreCase(name) ||
                        Arrays.stream(s.getAliases()).anyMatch(a -> a.equalsIgnoreCase(name)))
                .findAny().orElse(null);
    }
}
