package it.fulminazzo.primaltribes.AbstractClasses.SubCommands;

import it.angrybear.Commands.BearSubCommand;
import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.Enums.CommandType;
import it.fulminazzo.primaltribes.Enums.Permissions;
import it.fulminazzo.primaltribes.Managers.ClanPlayersManager;
import it.fulminazzo.primaltribes.Managers.ClansManager;
import it.fulminazzo.primaltribes.Managers.RolesManager;
import it.fulminazzo.primaltribes.PrimalTribes;

public abstract class SubCommand extends BearSubCommand<PrimalTribes> {
    private final CommandType commandType;

    public SubCommand(AbstractCommand command, String name, Permissions permissions, String usage, String description,
                      CommandType commandType, String... aliases) {
        super(PrimalTribes.getPlugin(), command, name, permissions, usage, description, aliases);
        this.commandType = commandType;
    }

    public SubCommand(AbstractCommand command, String name, Permissions permissions, String usage, String description,
                      boolean playerOnly, CommandType commandType, String... aliases) {
        super(PrimalTribes.getPlugin(), command, name, permissions, usage, description, playerOnly, aliases);
        this.commandType = commandType;
    }

    public boolean isClanOnly() {
        return commandType.equals(CommandType.CLAN_ONLY);
    }

    public boolean isNoClanOnly() {
        return commandType.equals(CommandType.NO_CLAN_ONLY);
    }

    public ClansManager getClansManager() {
        return PrimalTribes.getPlugin().getClansManager();
    }

    public RolesManager getRolesManager() {
        return PrimalTribes.getPlugin().getRolesManager();
    }

    public ClanPlayersManager getClanPlayersManager() {
        return PrimalTribes.getPlugin().getClanPlayersManager();
    }

    public boolean isAdminCommand() {
        return PrimalTribes.isAdminCommand(getCommand());
    }

    public boolean isNormalCommand() {
        return !PrimalTribes.isAdminCommand(getCommand());
    }
}