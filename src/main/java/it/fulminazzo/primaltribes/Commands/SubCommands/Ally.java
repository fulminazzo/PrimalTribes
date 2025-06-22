package it.fulminazzo.primaltribes.Commands.SubCommands;

import it.fulminazzo.primaltribes.Api.Events.AdminSubCommandEvent;
import it.fulminazzo.primaltribes.Api.Events.SubCommandEvent;
import it.fulminazzo.primaltribes.Utils.StringUtils;
import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.ClanSubCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.SuperClanSubCommand;
import it.fulminazzo.primaltribes.Commands.SubCommands.AllySubCommands.AllyAccept;
import it.fulminazzo.primaltribes.Commands.SubCommands.AllySubCommands.AllyDecline;
import it.fulminazzo.primaltribes.Commands.SubCommands.AllySubCommands.AllyInvite;
import it.fulminazzo.primaltribes.Commands.SubCommands.AllySubCommands.AllyUnInvite;
import it.fulminazzo.primaltribes.Enums.ClanPermission;
import it.fulminazzo.primaltribes.Enums.CommandType;
import it.fulminazzo.primaltribes.Enums.Message;
import it.fulminazzo.primaltribes.Enums.Permissions;
import it.fulminazzo.primaltribes.Objects.Users.ClanPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class Ally extends SuperClanSubCommand {

    public Ally(AbstractCommand command) {
        super(command, "ally", Permissions.ALLY, ClanPermission.ALLY,
                StringUtils.getCommandSyntax(String.format("&c%s &aally", command.getName()), "subcommand"),
                Message.HELP_ALLY.getMessage(false, false),
                !command.isAdminCommand(), CommandType.CLAN_ONLY);
        setSubCommands(Arrays.asList(
                new AllyInvite(command, command.getName() + String.format(" &a%s", getName())),
                new AllyUnInvite(command, command.getName() + String.format(" &a%s", getName())),
                new AllyAccept(command, command.getName() + String.format(" &a%s", getName())),
                new AllyDecline(command, command.getName() + String.format(" &a%s", getName()))
        ));
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String[] args) {
        checkClan(sender);
        ClanPlayer clanPlayer = null;
        if (sender instanceof Player) clanPlayer = getClanPlayersManager().getClanPlayer((Player) sender);
        String argument = args.length == 0 ? "invite" : args[0];
        ClanSubCommand subCommand = getSubCommand(argument);
        if (subCommand == null)
            sender.sendMessage(Message.SUBCOMMAND_NOT_FOUND.getMessage(!isAdminCommand(), isAdminCommand())
                    .replace("%subcommand%", argument));
        else if (!sender.hasPermission(subCommand.getPermission()) ||
                (!isAdminCommand() && clanPlayer != null && !clanPlayer.hasPermission(subCommand.getClanPermission())))
            sender.sendMessage(Message.NO_PERMISSION_SUBCOMMAND.getMessage(!isAdminCommand(), isAdminCommand())
                    .replace("%subcommand%", argument.toLowerCase()));
        else if (args.length - 1 < subCommand.getMinArguments()) {
            sender.sendMessage(Message.NOT_ENOUGH_ARGUMENTS.getMessage(!isAdminCommand(), isAdminCommand()));
            sender.sendMessage(Message.USAGE.getMessage(!isAdminCommand(), isAdminCommand())
                    .replace("%usage%", subCommand.getUsage()));
        } else {
            subCommand.setClan(clan);
            if (isAdminCommand()) {
                AdminSubCommandEvent subCommandEvent = new AdminSubCommandEvent(subCommand, sender, getCommand(), args);
                Bukkit.getPluginManager().callEvent(subCommandEvent);
                if (subCommandEvent.isCancelled()) return;
            }
            else {
                SubCommandEvent subCommandEvent = new SubCommandEvent(subCommand, sender, getCommand(), args);
                Bukkit.getPluginManager().callEvent(subCommandEvent);
                if (subCommandEvent.isCancelled()) return;
            }
            subCommand.execute(sender, cmd, Arrays.copyOfRange(args, 1, args.length));
        }
    }

    @Override
    public int getMinArguments() {
        return 0;
    }
}