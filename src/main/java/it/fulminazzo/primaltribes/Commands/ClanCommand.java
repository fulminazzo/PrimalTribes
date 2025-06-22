package it.fulminazzo.primaltribes.Commands;

import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.ClanSubCommand;
import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.SubCommand;
import it.fulminazzo.primaltribes.Api.Events.SubCommandEvent;
import it.fulminazzo.primaltribes.Commands.SubCommands.*;
import it.fulminazzo.primaltribes.Enums.Message;
import it.fulminazzo.primaltribes.Enums.Permissions;
import it.fulminazzo.primaltribes.Objects.Users.ClanPlayer;
import it.fulminazzo.primaltribes.PrimalTribes;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClanCommand extends AbstractCommand {
    private final PrimalTribes plugin;

    public ClanCommand(PrimalTribes plugin, String name) {
        super(plugin, name, new Permissions("clan"), Message.CLAN_COMMAND_DESCRIPTION.getMessage(false, false),
                ChatColor.translateAlternateColorCodes('&', String.format("&c/%s &8<&6subcommand&8>", name)));
        this.plugin = plugin;
        addSubCommands(
                new Motd(this), new Create(plugin, this), new SetDisplayName(plugin, this), new UnSetDisplayName(this),
                new SetTag(plugin, this), new UnSetTag(this), new Chat(this), new MuteChat(this),
                new SilenceChat(this), new ToggleFriendlyFire(this), new Discord(this), new Telegram(this),
                new Invite(this), new UnInvite(this), new Kick(this), new Ban(this),
                new UnBan(this), new Join(this), new Open(this), new Close(this),
                new Disband(this), new Leave(this), new SetLeader(this), new Promote(this),
                new Demote(this), new SetWarnsLimit(this), new WarnCommand(this), new UnWarn(this),
                new ToggleInvites(this), new Ally(this), new UnAlly(this),
                new Decline(this), new Description(this),
                new Allies(this), new Members(this), new AllyChat(this),
                new Info(this), new SetRank(this),
                new Help(this)
        );
        setPermissionMessage(Message.NO_PERMISSION.getMessage(!isAdminCommand(), isAdminCommand()));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        String permission = getPermission();
        String permissionMessage = getPermissionMessage();
        if (permission != null && !sender.hasPermission(permission)) {
            if (permissionMessage != null) sender.sendMessage(permissionMessage);
            return true;
        }
        String argument;
        if (args.length == 0) {
            if (sender instanceof Player && plugin.getClanPlayersManager().getClanPlayer((Player) sender).isInClan())
                args = new String[]{"info"};
            else {
                sender.sendMessage(Message.NOT_ENOUGH_ARGUMENTS.getMessage(!isAdminCommand(), isAdminCommand()));
                sender.sendMessage(Message.USAGE.getMessage(!isAdminCommand(), isAdminCommand())
                        .replace("%usage%", getUsage()));
                return true;
            }
        }
        argument = args[0];
        SubCommand subCommand = getSubCommand(argument);
        if (subCommand == null) {
            sender.sendMessage(Message.SUBCOMMAND_NOT_FOUND.getMessage(!isAdminCommand(), isAdminCommand())
                    .replace("%subcommand%", argument));
            return true;
        }
        if (!(sender instanceof Player)) {
            if (subCommand.isPlayerOnly()) {
                sender.sendMessage(Message.CONSOLE_CANNOT_EXECUTE.getMessage(!isAdminCommand(), isAdminCommand())
                        .replace("%subcommand%", argument));
                return true;
            }
        } else {
            ClanPlayer clanPlayer = plugin.getClanPlayersManager().getClanPlayer((Player) sender);
            if (subCommand.isClanOnly()) {
                if (!clanPlayer.isInClan()) {
                    sender.sendMessage(Message.NOT_IN_CLAN.getMessage(!isAdminCommand(), isAdminCommand()));
                    return true;
                } else if (subCommand instanceof ClanSubCommand) {
                    ClanSubCommand clanSubCommand = (ClanSubCommand) subCommand;
                    if (!clanPlayer.hasPermission(clanSubCommand.getClanPermission())) {
                        sender.sendMessage(Message.NO_PERMISSION_SUBCOMMAND.getMessage(!isAdminCommand(), isAdminCommand())
                                .replace("%subcommand%", argument.toLowerCase()));
                        return true;
                    }
                }
            } else if (subCommand.isNoClanOnly() && clanPlayer.isInClan()) {
                sender.sendMessage(Message.ALREADY_IN_CLAN.getMessage(!isAdminCommand(), isAdminCommand()));
                return true;
            }
        }
        if (!sender.hasPermission(subCommand.getPermission())) {
            sender.sendMessage(Message.NO_PERMISSION_SUBCOMMAND.getMessage(!isAdminCommand(), isAdminCommand())
                    .replace("%subcommand%", argument.toLowerCase()));
            return true;
        }
        if (args.length - 1 < subCommand.getMinArguments()) {
            sender.sendMessage(Message.NOT_ENOUGH_ARGUMENTS.getMessage(!isAdminCommand(), isAdminCommand()));
            sender.sendMessage(Message.USAGE.getMessage(!isAdminCommand(), isAdminCommand())
                    .replace("%usage%", subCommand.getUsage()));
            return true;
        }
        SubCommandEvent subCommandEvent = new SubCommandEvent(subCommand, sender, this, args);
        Bukkit.getPluginManager().callEvent(subCommandEvent);
        if (subCommandEvent.isCancelled()) return true;
        subCommand = subCommandEvent.getSubCommand();
        args = subCommandEvent.getArgs();
        subCommand.execute(sender, this, Arrays.copyOfRange(args, 1, args.length));
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args) throws IllegalArgumentException {
        List<String> list = new ArrayList<>();
        if (args.length == 1)
            list = getSubCommands().stream().filter(s -> validateSubCommand(s, sender)).flatMap(s -> Stream.concat(
                    Stream.of(s.getName()),
                    Arrays.stream(s.getAliases())
            )).collect(Collectors.toList());

        if (args.length >= 2) {
            SubCommand subCommand = getSubCommand(args[0]);
            if (validateSubCommand(subCommand, sender))
                list = subCommand.onTabComplete(sender, this, Arrays.copyOfRange(args, 1, args.length));
        }
        return StringUtil.copyPartialMatches(args[args.length - 1], list, new ArrayList<>());
    }
}
