package it.fulminazzo.primaltribes.Commands.SubCommands;

import it.fulminazzo.primaltribes.Utils.StringUtils;
import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.ConfirmClanSubCommand;
import it.fulminazzo.primaltribes.Enums.ClanPermission;
import it.fulminazzo.primaltribes.Enums.CommandType;
import it.fulminazzo.primaltribes.Enums.Message;
import it.fulminazzo.primaltribes.Enums.Permissions;
import it.fulminazzo.primaltribes.Objects.Users.ClanPlayer;
import it.fulminazzo.primaltribes.Objects.Users.User;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UnWarn extends ConfirmClanSubCommand {

    public UnWarn(AbstractCommand command) {
        super(command, "unwarn", Permissions.UNWARN, ClanPermission.UNWARN,
                StringUtils.getCommandSyntax(String.format("&c%s &aunwarn", command.getName()), "player"),
                Message.HELP_UNWARN.getMessage(false, false),
                !command.isAdminCommand(), CommandType.CLAN_ONLY);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String[] args) {
        checkClan(sender);
        execute(sender, args, Message.NO_MEMBERS_TO_UNWARN, Message.NO_MEMBER_SPECIFIED,
                getMembers(sender), memberName -> {
            ClanPlayer clanPlayer = sender instanceof Player ?
                    getClanPlayersManager().getClanPlayer((Player) sender) : null;
            if (sender.getName().equalsIgnoreCase(memberName) && (clanPlayer != null && !clanPlayer.hasPermission(ClanPermission.UNWARN_SELF)))
                sender.sendMessage(Message.CANNOT_UNWARN_SELF.getMessage(!isAdminCommand(), isAdminCommand()));
            else if (clan.getMember(memberName) == null)
                sender.sendMessage(Message.MEMBER_NOT_FOUND.getMessage(!isAdminCommand(), isAdminCommand())
                        .replace("%member%", memberName));
            else execute(sender,
                        () ->
                            clan.unWarnPlayer(sender, memberName, isAdminCommand()));
        });
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) list.addAll(getMembers(sender));
        return list;
    }

    @Override
    public int getMinArguments() {
        return 0;
    }
    
    private List<String> getMembers(CommandSender sender) {
        checkClan(sender);
        ClanPlayer clanPlayer = sender instanceof Player ?
                getClanPlayersManager().getClanPlayer((Player) sender) : null;
        return clan.getMembers().stream()
                .map(User::getName)
                .filter(n -> !n.equalsIgnoreCase(sender.getName()) ||
                        clanPlayer == null || clanPlayer.hasPermission(ClanPermission.UNWARN_SELF))
                .collect(Collectors.toList());
    }
}