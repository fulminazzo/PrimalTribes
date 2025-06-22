package it.fulminazzo.primaltribes.AbstractClasses.SubCommands;

import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.Enums.ClanPermission;
import it.fulminazzo.primaltribes.Enums.CommandType;
import it.fulminazzo.primaltribes.Enums.Permissions;
import it.fulminazzo.primaltribes.Objects.Clan;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class ClanSubCommand extends SubCommand {
    protected Clan clan;
    private final ClanPermission clanPermission;

    public ClanSubCommand(AbstractCommand command, String name, Permissions permissions, ClanPermission clanPermission, String help, String description,
                          CommandType commandType, String... aliases) {
        super(command, name, permissions, help, description, commandType, aliases);
        this.clanPermission = clanPermission;
    }

    public ClanSubCommand(AbstractCommand command, String name, Permissions permissions, ClanPermission clanPermission, String help, String description,
                          boolean playerOnly, CommandType commandType, String... aliases) {
        super(command, name, permissions, help, description, playerOnly, commandType, aliases);
        this.clanPermission = clanPermission;
    }

    protected void checkClan(CommandSender sender) {
        if (!isAdminCommand() && sender instanceof Player)
            clan = getClanPlayersManager().getClanPlayer((Player) sender).getClan();
    }

    public void setClan(Clan clan) {
        this.clan = clan;
    }

    public Clan getClan() {
        return clan;
    }

    public ClanPermission getClanPermission() {
        return clanPermission;
    }
}