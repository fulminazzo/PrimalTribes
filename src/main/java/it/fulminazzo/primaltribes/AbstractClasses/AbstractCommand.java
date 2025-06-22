package it.fulminazzo.primaltribes.AbstractClasses;

import it.angrybear.Commands.BearCommand;
import it.angrybear.Enums.BearPermission;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.ClanSubCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.SubCommand;
import it.fulminazzo.primaltribes.Objects.Users.ClanPlayer;
import it.fulminazzo.primaltribes.PrimalTribes;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractCommand extends BearCommand<PrimalTribes> {

    public AbstractCommand(PrimalTribes plugin, String name, BearPermission permission, String description, String usageMessage, String... aliases) {
        super(plugin, name, permission, description, usageMessage, aliases);
    }

    public boolean validateSubCommand(SubCommand subCommand, CommandSender sender) {
        if (subCommand == null) return false;
        else if (!(sender instanceof Player)) {
            return !subCommand.isPlayerOnly();
        } else if (!PrimalTribes.isAdminCommand(this)) {
            ClanPlayer clanPlayer = getPlugin().getClanPlayersManager().getClanPlayer((Player) sender);
            if (subCommand.isClanOnly()) {
                if (!clanPlayer.isInClan()) return false;
                else if (subCommand instanceof ClanSubCommand) {
                    ClanSubCommand clanSubCommand = (ClanSubCommand) subCommand;
                    if (!clanPlayer.hasPermission(clanSubCommand.getClanPermission())) return false;
                }
            }
            else if (subCommand.isNoClanOnly() && clanPlayer.isInClan()) return false;
        }
        return sender.hasPermission(subCommand.getPermission());
    }

    public void replaceSubCommand(SubCommand subCommand) {
        if (subCommand == null) return;
        replaceSubCommand(subCommand.getName(), subCommand);
    }

    public void replaceSubCommand(String name, SubCommand subCommand) {
        removeSubCommands(name);
        if (subCommand != null) addSubCommands(subCommand);
    }

    public List<SubCommand> getSubCommands() {
        return getInternalSubCommands();
    }

    public SubCommand getSubCommand(String name) {
        List<SubCommand> subCommands = getSubCommands();
        return new ArrayList<>(subCommands).stream()
                .filter(s -> s.getName().equalsIgnoreCase(name) ||
                        Arrays.stream(s.getAliases()).anyMatch(a -> a.equalsIgnoreCase(name)))
                .findAny().orElse(null);
    }

    public boolean isAdminCommand() {
        return PrimalTribes.isAdminCommand(this);
    }
}
