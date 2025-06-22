package it.fulminazzo.primaltribes.Commands.SubCommands;

import it.fulminazzo.primaltribes.Utils.StringUtils;
import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.ListClanSubCommand;
import it.fulminazzo.primaltribes.Enums.*;
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

public class Promote extends ListClanSubCommand {

    public Promote(AbstractCommand command) {
        super(command, "promote", Permissions.PROMOTE, ClanPermission.PROMOTE,
                StringUtils.getCommandSyntax(String.format("&c%s &apromote", command.getName()), "player"),
                Message.HELP_PROMOTE.getMessage(false, false),
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
                    Role nextRole = getRolesManager().getNextRole(member.getRole());
                    if (nextRole == null)
                        sender.sendMessage(Message.CANNOT_PROMOTE_LEADER.getMessage(!isAdminCommand(), isAdminCommand())
                                .replace("%player%", member.getName()));
                    else if (nextRole.isLeaderRole())
                        sender.sendMessage(Message.CANNOT_PROMOTE_NEXT_LEADER.getMessage(!isAdminCommand(), isAdminCommand())
                                .replace("%player%", member.getName()));
                    else {
                        Role[] roles = clan.promotePlayer(sender, pName);
                        sender.sendMessage(Message.PLAYER_PROMOTED.getMessage(!isAdminCommand(), isAdminCommand())
                                .replace("%player%", member.getName())
                                .replace("%rank%", member.getRole().getDisplayName()));
                        Player player = Bukkit.getPlayer(pName);
                        if (player != null && roles != null)
                            player.sendMessage(Message.PROMOTED.getMessage(!isAdminCommand(), isAdminCommand())
                                    .replace("%previous-rank%", roles[0].getDisplayName())
                                    .replace("%next-rank%", roles[1].getDisplayName()));
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
                .filter(m -> {
                    Role nextRole = getRolesManager().getNextRole(m.getRole());
                    return nextRole != null && !nextRole.isLeaderRole();
                })
                .map(Member::getName)
                .collect(Collectors.toList());
    }
}
