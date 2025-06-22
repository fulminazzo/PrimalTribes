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
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Kick extends ListClanSubCommand {

    public Kick(AbstractCommand command) {
        super(command, "kick", Permissions.KICK, ClanPermission.KICK,
                StringUtils.getCommandSyntax(String.format("&c%s &akick", command.getName()), "player"),
                Message.HELP_KICK.getMessage(false, false),
                !command.isAdminCommand(), CommandType.CLAN_ONLY);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String[] args) {
        checkClan(sender);
        execute(sender, args, Message.NO_MEMBERS_TO_KICK, Message.NO_MEMBER_SPECIFIED,
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
                    if (member == null) {
                        sender.sendMessage(Message.MEMBER_NOT_FOUND.getMessage(!isAdminCommand(), isAdminCommand())
                                .replace("%member%", playerName));
                        return;
                    }
                    clan.kickMember(sender, playerName);
                    sender.sendMessage(Message.PLAYER_KICKED.getMessage(!isAdminCommand(), isAdminCommand())
                            .replace("%player%", playerName));
                    Player target = Bukkit.getPlayer(playerName);
                    if (target != null)
                        target.sendMessage(Message.KICKED.getMessage(false, false)
                                .replace("%clan%", clan.getDisplayName()));
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
        return clan.getMembers().stream()
                .map(User::getName)
                .filter(s -> !s.equalsIgnoreCase(sender.getName()))
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
    }
}
