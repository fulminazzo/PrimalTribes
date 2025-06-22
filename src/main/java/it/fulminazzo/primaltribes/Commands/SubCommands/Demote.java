package it.fulminazzo.primaltribes.Commands.SubCommands;

import it.fulminazzo.primaltribes.Utils.StringUtils;
import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.ListClanSubCommand;
import it.fulminazzo.primaltribes.Enums.ClanPermission;
import it.fulminazzo.primaltribes.Enums.CommandType;
import it.fulminazzo.primaltribes.Enums.Message;
import it.fulminazzo.primaltribes.Enums.Permissions;
import it.fulminazzo.primaltribes.Objects.Clan;
import it.fulminazzo.primaltribes.Objects.Role;
import it.fulminazzo.primaltribes.Objects.Users.Member;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Demote extends ListClanSubCommand {

    public Demote(AbstractCommand command) {
        super(command, "demote", Permissions.DEMOTE, ClanPermission.DEMOTE,
                StringUtils.getCommandSyntax(String.format("&c%s &ademote", command.getName()), "player"),
                Message.HELP_DEMOTE.getMessage(false, false),
                !command.isAdminCommand(), CommandType.CLAN_ONLY);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String[] args) {
        checkClan(sender);
        execute(sender, args, Message.NO_MEMBERS, Message.NO_MEMBER_SPECIFIED,
                getNonLeaderMembers(clan),
                pName -> {
                    if (sender instanceof Player) ((Player) sender).closeInventory();
                    Member member = clan.getMember(pName);
                    if (member == null) {
                        sender.sendMessage(Message.MEMBER_NOT_FOUND.getMessage(!isAdminCommand(), isAdminCommand())
                                .replace("%member%", pName));
                        return;
                    }
                    Role role = member.getRole();
                    if (role.isLeaderRole())
                        sender.sendMessage(Message.CANNOT_DEMOTE_LEADER.getMessage(!isAdminCommand(), isAdminCommand())
                                .replace("%player%", member.getName()));
                    else if (member.getRole().isDefaultRole())
                        sender.sendMessage(Message.CANNOT_DEMOTE_DEFAULT_RANK.getMessage(!isAdminCommand(), isAdminCommand())
                                .replace("%player%", member.getName()));
                    else {
                        Role[] roles = clan.demotePlayer(sender, pName);
                        sender.sendMessage(Message.PLAYER_DEMOTED.getMessage(!isAdminCommand(), isAdminCommand())
                                .replace("%player%", member.getName())
                                .replace("%rank%", member.getRole().getDisplayName()));
                        Player player = Bukkit.getPlayer(pName);
                        if (player != null && roles != null)
                            player.sendMessage(Message.DEMOTED.getMessage(!isAdminCommand(), isAdminCommand())
                                    .replace("%rank%", roles[0].getDisplayName())
                                    .replace("%new-rank%", roles[1].getDisplayName()));
                    }
                });
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String[] args) {
        List<String> list = new ArrayList<>();
        checkClan(sender);
        if (args.length == 1) list.addAll(getNonLeaderMembers(clan));
        return list;
    }

    @Override
    public int getMinArguments() {
        return 0;
    }

    private List<String> getNonLeaderMembers(Clan clan) {
        return clan.getMembers().stream()
                .filter(m -> !m.getRole().isLeaderRole())
                .filter(m -> !m.getRole().isDefaultRole())
                .map(Member::getName)
                .collect(Collectors.toList());
    }
}
