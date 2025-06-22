package it.fulminazzo.primaltribes.Commands.SubCommands.AdminSubCommands;

import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.ListClanSubCommand;
import it.fulminazzo.primaltribes.Enums.*;
import it.fulminazzo.primaltribes.Objects.Users.ClanPlayer;
import it.fulminazzo.primaltribes.Utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Add extends ListClanSubCommand {

    public Add(AbstractCommand command) {
        super(command, "add", Permissions.ADD, ClanPermission.ALL,
                StringUtils.getCommandSyntax(String.format("&c%s &aadd", command.getName()), "player"),
                Message.HELP_ADD.getMessage(false, false),
                false, CommandType.CLAN_ONLY);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String[] args) {
        checkClan(sender);
        execute(sender, cmd, args, Message.NO_PLAYERS_TO_INVITE, Message.NO_PLAYER_SPECIFIED,
                getNonInClanPlayers(),
                player -> {
                    if (sender instanceof Player) ((Player) sender).closeInventory();
                    ClanPlayer clanPlayer = getClanPlayersManager().getClanPlayer(player);
                    if (clanPlayer.isInClan())
                        sender.sendMessage(Message.PLAYER_ALREADY_IN_CLAN.getMessage(!isAdminCommand(), isAdminCommand())
                                .replace("%player%", player.getName()));
                    else if (clan.isPlayerBanned(player) &&
                            !sender.hasPermission(Permissions.ADD_BYPASS_BAN.getPermission(isAdminCommand())))
                        sender.sendMessage(Message.ERROR_PLAYER_BANNED.getMessage(!isAdminCommand(), isAdminCommand())
                                .replace("%player%", player.getName()));
                    else if (clan.getMembers().size() >= ConfigOption.MEMBERS_PER_CLAN.getInt() &&
                            !sender.hasPermission(Permissions.ADD_BYPASS_MEMBERS.getPermission(isAdminCommand())))
                        sender.sendMessage(Message.CLAN_MAXIMUM_MEMBERS_REACHED.getMessage(!isAdminCommand(), isAdminCommand()));
                    else {
                        clanPlayer.acceptInvite(clan);
                        sender.sendMessage(Message.ADDED_PLAYER.getMessage(!isAdminCommand(), isAdminCommand())
                                .replace("%clan%", clan.getDisplayName())
                                .replace("%player%", player.getName()));
                        player.sendMessage(Message.ADDED_TO_CLAN.getMessage(!isAdminCommand(), isAdminCommand())
                                .replace("%clan%", clan.getDisplayName())
                                .replace("%issuer%", sender.getName()));
                    }
                });
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String[] args) {
        List<String> list = new ArrayList<>();
        checkClan(sender);
        if (args.length == 1) list.addAll(getNonInClanPlayers().stream()
                .map(HumanEntity::getName)
                .collect(Collectors.toList()));
        return list;
    }

    @Override
    public int getMinArguments() {
        return 0;
    }

    private List<Player> getNonInClanPlayers() {
        return Bukkit.getOnlinePlayers().stream()
                .filter(p -> !getClanPlayersManager().getClanPlayer(p).isInClan())
                .collect(Collectors.toList());
    }
}
