package it.fulminazzo.primaltribes.Commands.SubCommands;

import it.fulminazzo.primaltribes.Utils.StringUtils;
import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.ListClanSubCommand;
import it.fulminazzo.primaltribes.Enums.ClanPermission;
import it.fulminazzo.primaltribes.Enums.CommandType;
import it.fulminazzo.primaltribes.Enums.Message;
import it.fulminazzo.primaltribes.Enums.Permissions;
import it.fulminazzo.primaltribes.Objects.Clan;
import it.fulminazzo.primaltribes.Objects.Users.Member;
import it.fulminazzo.primaltribes.Objects.Users.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Ban extends ListClanSubCommand {

    public Ban(AbstractCommand command) {
        super(command, "ban", Permissions.BAN, ClanPermission.BAN,
                StringUtils.getCommandSyntax(String.format("&c%s &aban", command.getName()), "player"),
                Message.HELP_BAN.getMessage(false, false),
                !command.isAdminCommand(), CommandType.CLAN_ONLY);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String[] args) {
        checkClan(sender);
        execute(sender, args, Message.NO_PLAYERS_TO_BAN, Message.NO_PLAYER_SPECIFIED,
                getMembers(clan, sender),
                playerName -> {
                    ((Player) sender).closeInventory();
                    if (sender.getName().equals(playerName)) {
                        sender.sendMessage(Message.CANNOT_KICK_SELF.getMessage(!isAdminCommand(), isAdminCommand()));
                        return;
                    }
                    Member member = clan.getMember(playerName);
                    if (member != null && member.getRole().isLeaderRole()) {
                        sender.sendMessage(Message.CANNOT_KICK_LEADER.getMessage(!isAdminCommand(), isAdminCommand()));
                        return;
                    }
                    if (clan.isPlayerBanned(playerName)) {
                        sender.sendMessage(Message.PLAYER_ALREADY_BANNED.getMessage(!isAdminCommand(), isAdminCommand())
                                .replace("%player%", clan.getBanned(playerName).getName()));
                        return;
                    }
                    clan.banPlayer(sender, playerName);
                    sender.sendMessage(Message.PLAYER_BANNED.getMessage(!isAdminCommand(), isAdminCommand())
                            .replace("%player%", playerName));
                    Player target = Bukkit.getPlayer(playerName);
                    String bannedMessage = Message.BANNED.getMessage(false, false)
                            .replace("%clan%", clan.getDisplayName());
                    if (target != null && !bannedMessage.equalsIgnoreCase(""))
                        target.sendMessage(bannedMessage);
                });
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String[] args) {
        List<String> list = new ArrayList<>();
        checkClan(sender);
        if (args.length == 1) list.addAll(getMembers(clan, sender));
        return list;
    }

    @Override
    public int getMinArguments() {
        return 0;
    }

    private List<String> getMembers(Clan clan, CommandSender sender) {
        return Stream.concat(
                        Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName),
                        clan.getMembers().stream().map(User::getName)
                ).distinct()
                .filter(n -> !n.equalsIgnoreCase(sender.getName()))
                .filter(n -> !clan.isPlayerBanned(n))
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
    }
}
