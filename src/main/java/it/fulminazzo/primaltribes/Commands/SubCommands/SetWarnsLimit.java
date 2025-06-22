package it.fulminazzo.primaltribes.Commands.SubCommands;

import it.fulminazzo.primaltribes.Utils.StringUtils;
import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.NumberClanSubCommand;
import it.fulminazzo.primaltribes.Enums.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class SetWarnsLimit extends NumberClanSubCommand {

    public SetWarnsLimit(AbstractCommand command) {
        super(command, "setwarnlimit", Permissions.SET_WARNS_LIMIT, ClanPermission.SET_WARNS_LIMIT,
                StringUtils.getCommandSyntax(String.format("&c%s &asetwarnlimit", command.getName()), "number"),
                Message.HELP_SET_WARNS_LIMIT.getMessage(false, false),
                !command.isAdminCommand(), CommandType.CLAN_ONLY);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String[] args) {
        checkClan(sender);
        execute(sender, args, Message.SET_WARN_CANCELLED, Message.GUI_SET_WARN_TITLE, Message.GUI_SET_WARN_TITLE,
                Message.GUI_SET_WARN_LORE, clan.getWarnsLimit(), 0, ConfigOption.MAXIMUM_WARNS.getInt(),
                warnsKick -> {
            clan.setWarnsKick(sender, Math.toIntExact(warnsKick));
            sender.sendMessage(Message.SET_WARNS_LIMIT.getMessage(!isAdminCommand(), isAdminCommand())
                    .replace("%warns%", String.valueOf(warnsKick)));
        });
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) list.add("<number>");
        return list;
    }

    @Override
    public int getMinArguments() {
        return 0;
    }
}