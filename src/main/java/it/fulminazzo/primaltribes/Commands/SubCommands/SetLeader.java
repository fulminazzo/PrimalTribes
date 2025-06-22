package it.fulminazzo.primaltribes.Commands.SubCommands;

import it.fulminazzo.primaltribes.Utils.StringUtils;
import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.ConfirmClanSubCommand;
import it.fulminazzo.primaltribes.Enums.ClanPermission;
import it.fulminazzo.primaltribes.Enums.CommandType;
import it.fulminazzo.primaltribes.Enums.Message;
import it.fulminazzo.primaltribes.Enums.Permissions;
import it.fulminazzo.primaltribes.Objects.Clan;
import it.fulminazzo.primaltribes.Objects.Role;
import it.fulminazzo.primaltribes.Objects.Users.Member;
import it.fulminazzo.primaltribes.Objects.Users.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SetLeader extends ConfirmClanSubCommand {

    public SetLeader(AbstractCommand command) {
        super(command, "setleader", Permissions.SET_LEADER, ClanPermission.SET_LEADER,
                StringUtils.getCommandSyntax(String.format("&c%s &asetleader", command.getName()), "player"),
                Message.HELP_SET_LEADER.getMessage(false, false),
                !command.isAdminCommand(), CommandType.CLAN_ONLY);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String[] args) {
        checkClan(sender);
        execute(sender, args, Message.NO_MEMBERS, Message.NO_MEMBER_SPECIFIED,
                getNonLeaderMembers(clan).stream().map(User::getName).collect(Collectors.toList()), pName ->
                        execute(sender,
                                () -> {
                            Member member = clan.getMember(pName);
                            if (member == null) {
                                sender.sendMessage(Message.MEMBER_NOT_FOUND.getMessage(!isAdminCommand(), isAdminCommand())
                                        .replace("%member%", pName));
                                return;
                            }
                            if (member.getRole().isLeaderRole()) {
                                sender.sendMessage(Message.PLAYER_ALREADY_LEADER.getMessage(!isAdminCommand(), isAdminCommand())
                                        .replace("%player%", member.getName()));
                                return;
                            }
                            Role[] roles = clan.setLeader(sender, pName);
                            sender.sendMessage(Message.NEW_LEADER_SET.getMessage(!isAdminCommand(), isAdminCommand())
                                    .replace("%player%", pName));
                            Player newLeader = Bukkit.getPlayer(pName);
                            if (newLeader != null && roles != null)
                                newLeader.sendMessage(Message.PROMOTED.getMessage(!isAdminCommand(), isAdminCommand())
                                        .replace("%previous-rank%", roles[0].getDisplayName())
                                        .replace("%next-rank%", roles[1].getDisplayName()));
                        }));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String[] args) {
        List<String> list = new ArrayList<>();
        checkClan(sender);
        if (args.length == 1)
            list = getNonLeaderMembers(clan).stream().map(User::getName).collect(Collectors.toList());
        return list;
    }

    @Override
    public int getMinArguments() {
        return 0;
    }

    private List<Member> getNonLeaderMembers(Clan clan) {
        return clan.getMembers().stream().filter(m -> !m.getRole().isLeaderRole()).collect(Collectors.toList());
    }
}