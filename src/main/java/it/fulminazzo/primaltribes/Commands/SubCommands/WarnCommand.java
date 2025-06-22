package it.fulminazzo.primaltribes.Commands.SubCommands;

import it.fulminazzo.primaltribes.Utils.StringUtils;
import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.NumberClanSubCommand;
import it.fulminazzo.primaltribes.Enums.*;
import it.fulminazzo.primaltribes.Objects.Users.User;
import it.fulminazzo.primaltribes.Utils.TimeUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WarnCommand extends NumberClanSubCommand {

    public WarnCommand(AbstractCommand command) {
        super(command, "warn", Permissions.WARN, ClanPermission.WARN,
                StringUtils.getCommandSyntax(String.format("&c%s &awarn", command.getName()), "player", "&ddelay"),
                Message.HELP_WARN.getMessage(false, false),
                !command.isAdminCommand(), CommandType.CLAN_ONLY);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String[] args) {
        checkClan(sender);
        execute(sender, args, Message.NO_MEMBER_TO_WARN, Message.NO_MEMBER_SPECIFIED,
                getMembers(sender), memberName -> {
            if (sender.getName().equalsIgnoreCase(memberName))
                sender.sendMessage(Message.CANNOT_WARN_SELF.getMessage(!isAdminCommand(), isAdminCommand()));
            else if (clan.getMember(memberName) == null)
                sender.sendMessage(Message.MEMBER_NOT_FOUND.getMessage(!isAdminCommand(), isAdminCommand())
                        .replace("%member%", memberName));
            else execute(sender, Arrays.copyOfRange(args, Math.min(1, args.length), args.length), Message.WARN_CANCELLED,
                    Message.GUI_WARN_DELAY_TITLE, Message.GUI_WARN_DELAY_TITLE, Message.GUI_WARN_DELAY_LORE,
                        1, ConfigOption.MAX_WARN_EXPIRE.getInt(), TimeUtil::getTime, delay ->
                            clan.warnPlayer(sender, memberName, Math.toIntExact(delay), isAdminCommand()));
        });
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) list.addAll(getMembers(sender));
        if (args.length == 2) list.add("<delay>");
        return list;
    }

    @Override
    public int getMinArguments() {
        return 0;
    }

    private List<String> getMembers(CommandSender sender) {
        checkClan(sender);
        return clan.getMembers().stream()
                .map(User::getName)
                .filter(n -> !n.equalsIgnoreCase(sender.getName()))
                .collect(Collectors.toList());
    }
}