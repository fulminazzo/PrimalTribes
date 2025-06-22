package it.fulminazzo.primaltribes.Commands.SubCommands;

import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.ListClanSubCommand;
import it.fulminazzo.primaltribes.Enums.ClanPermission;
import it.fulminazzo.primaltribes.Enums.CommandType;
import it.fulminazzo.primaltribes.Enums.Message;
import it.fulminazzo.primaltribes.Enums.Permissions;
import it.fulminazzo.primaltribes.Objects.Clan;
import it.fulminazzo.primaltribes.Objects.Role;
import it.fulminazzo.primaltribes.Objects.Users.Member;
import it.fulminazzo.primaltribes.Utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SetRank extends ListClanSubCommand {

    public SetRank(AbstractCommand command) {
        super(command, "setrank", Permissions.SET_RANK, ClanPermission.SET_RANK,
                StringUtils.getCommandSyntax(String.format("&c%s &asetrank", command.getName()), "player"),
                Message.HELP_SET_RANK.getMessage(false, false),
                !command.isAdminCommand(), CommandType.CLAN_ONLY);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String[] args) {
        checkClan(sender);
        execute(sender, args, Message.NO_MEMBERS, Message.NO_MEMBER_SPECIFIED,
                getNonLeaderMembers(clan),
                pName -> {
                    Member member = clan.getMember(pName);
                    if (member == null)
                        sender.sendMessage(Message.MEMBER_NOT_FOUND.getMessage(!isAdminCommand(), isAdminCommand())
                                .replace("%member%", pName));
                    else if (member.getRole().isLeaderRole())
                        sender.sendMessage(Message.CANNOT_DEMOTE_LEADER.getMessage(!isAdminCommand(), isAdminCommand())
                                .replace("%player%", member.getName()));
                    else execute(sender, Arrays.copyOfRange(args, Math.min(1, args.length), args.length), Message.NO_ROLES,
                            Message.NO_RANK_SPECIFIED,
                                getRoles(pName), roleName -> {
                                Role role = getRolesManager().getRole(roleName);
                                if (role == null) {
                                    if (roleName == null)
                                        sender.sendMessage(Message.NO_RANK_SPECIFIED.getMessage(!isAdminCommand(), isAdminCommand()));
                                    else
                                        sender.sendMessage(Message.RANK_NOT_FOUND.getMessage(!isAdminCommand(), isAdminCommand())
                                                .replace("%rank%", roleName));
                                    return;
                                }
                                if (member.getRole().equals(role))
                                    sender.sendMessage(Message.ALREADY_REACHED_RANK.getMessage(!isAdminCommand(), isAdminCommand())
                                            .replace("%player%", member.getName())
                                            .replace("%rank%", role.getDisplayName()));
                                else if (role.isLeaderRole())
                                    sender.sendMessage(Message.CANNOT_SET_RANK_LEADER.getMessage(!isAdminCommand(), isAdminCommand()));
                                else {
                                    Role[] roles = clan.setRank(sender, pName, role);
                                    Player player = Bukkit.getPlayer(pName);
                                    Message playerPromoted = roles[1].getPriority() >= roles[0].getPriority() ?
                                            Message.PLAYER_PROMOTED : Message.PLAYER_DEMOTED;
                                    Message promoted = roles[1].getPriority() >= roles[0].getPriority() ?
                                            Message.PROMOTED : Message.DEMOTED;
                                    sender.sendMessage(playerPromoted.getMessage(!isAdminCommand(), isAdminCommand())
                                            .replace("%player%", member.getName())
                                            .replace("%rank%", member.getRole().getDisplayName()));
                                    if (player != null)
                                        player.sendMessage(promoted.getMessage(!isAdminCommand(), isAdminCommand())
                                                .replace("%previous-rank%", roles[0].getDisplayName())
                                                .replace("%rank%", roles[0].getDisplayName())
                                                .replace("%next-rank%", roles[1].getDisplayName())
                                                .replace("%new-rank%", roles[1].getDisplayName()));
                                }
                            });
                });
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String[] args) {
        List<String> list = new ArrayList<>();
        checkClan(sender);
        if (args.length == 1)
            list.addAll(getNonLeaderMembers(clan));

        if (args.length == 2) {
            String argument = args[0];
            Member member = clan.getMember(argument);
            if (member != null) {
                list.addAll(getRoles(argument).stream().map(Role::getName).collect(Collectors.toList()));
                list.remove(member.getRole().getName());
            }
        }
        return list;
    }

    @Override
    public int getMinArguments() {
        return 0;
    }

    private List<String> getNonLeaderMembers(Clan clan) {
        return clan.getMembers().stream()
                .filter(m -> !clan.getLeader().equals(m))
                .map(Member::getName)
                .collect(Collectors.toList());
    }

    private List<Role> getRoles(String playerName) {
        Member member = clan.getMember(playerName);
        return getRolesManager().getRoles().stream()
                .filter(r -> !r.isLeaderRole())
                .filter(r -> member != null && !r.equals(member.getRole()))
                .collect(Collectors.toList());
    }
}
