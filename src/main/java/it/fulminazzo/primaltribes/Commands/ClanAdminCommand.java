package it.fulminazzo.primaltribes.Commands;

import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.ClanSubCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.SubCommand;
import it.fulminazzo.primaltribes.Api.Events.AdminSubCommandEvent;
import it.fulminazzo.primaltribes.Commands.SubCommands.*;
import it.fulminazzo.primaltribes.Commands.SubCommands.AdminSubCommands.*;
import it.fulminazzo.primaltribes.Enums.Message;
import it.fulminazzo.primaltribes.Enums.Permissions;
import it.fulminazzo.primaltribes.Objects.Clan;
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

public class ClanAdminCommand extends AbstractCommand {
    private final PrimalTribes plugin;
    private final List<SubCommand> singleSubCommands;

    public ClanAdminCommand(PrimalTribes plugin, String name) {
        super(plugin, name, new Permissions("clana"),
                Message.CLANA_COMMAND_DESCRIPTION.getMessage(false, false),
                ChatColor.translateAlternateColorCodes('&', String.format("&c/%s &8<&6%command-name%&8> &8<&6subcommand&8>"
                        .replace("%command-name%", PrimalTribes.getCommandName()), name)),
                name + "dmin");
        this.plugin = plugin;
        addSubCommands(new Motd(this), new Telegram(this), new Discord(this),
                new SetDisplayName(plugin, this), new UnSetDisplayName(this),
                new SetTag(plugin, this), new UnSetTag(this), new Invite(this),
                new UnInvite(this), new Promote(this), new Demote(this),
                new SetLeader(this), new Chat(this),
                new MuteChat(this), new ToggleFriendlyFire(this), new Kick(this),
                new Ban(this), new UnBan(this), new Open(this),
                new Close(this), new Disband(this), new SetWarnsLimit(this),
                new WarnCommand(this), new UnWarn(this),
                new ToggleInvites(this), new Description(this),
                new Ally(this), new UnAlly(this),
                new Allies(this), new Members(this),
                new AllyChat(this), new SetRank(this),
                new Add(this), new SilenceChatAdmin(this)
                );
        singleSubCommands = Arrays.asList(
                new Help(this), new CreateAdmin(this),
                new Reload(plugin, this), new SocialSpy(this));
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
        if (args.length == 0) {
            sender.sendMessage(Message.NOT_ENOUGH_ARGUMENTS.getMessage(!isAdminCommand(), isAdminCommand()));
            sender.sendMessage(Message.USAGE.getMessage(!isAdminCommand(), isAdminCommand())
                    .replace("%usage%", getUsage()));
            return true;
        }
        String argument;
        SubCommand subCommand;
        Clan clan = plugin.getClansManager().getClan(args[0]);
        int range = 2;
        if (clan == null) {
            argument = args[0];
            subCommand = singleSubCommands.stream()
                    .filter(s -> s.getName().equalsIgnoreCase(argument) ||
                            Arrays.stream(s.getAliases()).anyMatch(a -> a.equalsIgnoreCase(argument)))
                    .findAny().orElse(null);
            range = 1;
            if (subCommand == null) {
                sender.sendMessage(Message.CLAN_NOT_FOUND.getMessage(!isAdminCommand(), isAdminCommand())
                        .replace("%clan%", args[0]));
                return true;
            }
        } else if (args.length == 1) {
            if (sender instanceof Player && plugin.getClanPlayersManager().getClanPlayer((Player) sender).isInClan()) {
                argument = "info";
                subCommand = getSubCommand(argument);
            } else {
                sender.sendMessage(Message.NOT_ENOUGH_ARGUMENTS.getMessage(!isAdminCommand(), isAdminCommand()));
                sender.sendMessage(Message.USAGE.getMessage(!isAdminCommand(), isAdminCommand())
                        .replace("%usage%", getUsage()));
                return true;
            }
        } else {
            argument = args[1];
            subCommand = getSubCommand(argument);
        }
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
        }
        if (subCommand instanceof ClanSubCommand) {
            ClanSubCommand clanSubCommand = (ClanSubCommand) subCommand;
            clanSubCommand.setClan(clan);
        }
        if (!sender.hasPermission(subCommand.getPermission())) {
            sender.sendMessage(Message.NO_PERMISSION_SUBCOMMAND.getMessage(!isAdminCommand(), isAdminCommand())
                    .replace("%subcommand%", argument.toLowerCase()));
            return true;
        }
        if (args.length - range < subCommand.getMinArguments()) {
            sender.sendMessage(Message.NOT_ENOUGH_ARGUMENTS.getMessage(!isAdminCommand(), isAdminCommand()));
            sender.sendMessage(Message.USAGE.getMessage(!isAdminCommand(), isAdminCommand())
                    .replace("%usage%", subCommand.getUsage()));
            return true;
        }
        AdminSubCommandEvent adminSubCommandEvent = new AdminSubCommandEvent(subCommand, sender, this, args);
        Bukkit.getPluginManager().callEvent(adminSubCommandEvent);
        if (adminSubCommandEvent.isCancelled()) return true;
        subCommand = adminSubCommandEvent.getSubCommand();
        args = adminSubCommandEvent.getArgs();
        subCommand.execute(sender, this, Arrays.copyOfRange(args, range, args.length));
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args) throws IllegalArgumentException {
        List<String> list = new ArrayList<>();
        List<SubCommand> subCommands;
        int startingArgSubCommand;

        Clan clan = args.length > 1 ? plugin.getClansManager().getClan(args[0]) : null;

        if (clan != null) {
            subCommands = super.getSubCommands();
            startingArgSubCommand = 2;
        } else {
            subCommands = singleSubCommands;
            startingArgSubCommand = 1;
        }

        if (args.length == 1)
            list.addAll(plugin.getClansManager().getClans().stream().map(Clan::getName).collect(Collectors.toList()));

        if (args.length == startingArgSubCommand)
            list.addAll(subCommands.stream()
                    .filter(s -> sender.hasPermission(s.getPermission()))
                    .peek(s -> {
                        if (s instanceof ClanSubCommand) ((ClanSubCommand) s).setClan(clan);
                    })
                    .flatMap(s -> Stream.concat(
                            Stream.of(s.getName()),
                            Arrays.stream(s.getAliases())
                    )).collect(Collectors.toList()));

        if (args.length >= startingArgSubCommand + 1) {
            SubCommand subCommand = getSubCommand(subCommands, args[startingArgSubCommand - 1]);
            if (subCommand != null && sender.hasPermission(subCommand.getPermission())) {
                if (subCommand instanceof ClanSubCommand) ((ClanSubCommand) subCommand).setClan(clan);
                list = subCommand.onTabComplete(sender, this, Arrays.copyOfRange(args, startingArgSubCommand, args.length));
            }
        }

        return StringUtil.copyPartialMatches(args[args.length - 1], list, new ArrayList<>());
    }

    @Override
    public List<SubCommand> getSubCommands() {
        return Stream.concat(super.getSubCommands().stream(), singleSubCommands.stream()).collect(Collectors.toList());
    }

    private SubCommand getSubCommand(List<SubCommand> subCommands, String name) {
        return new ArrayList<>(subCommands).stream()
                .filter(s -> s.getName().equalsIgnoreCase(name) ||
                        Arrays.stream(s.getAliases()).anyMatch(a -> a.equalsIgnoreCase(name)))
                .findAny().orElse(null);
    }
}
