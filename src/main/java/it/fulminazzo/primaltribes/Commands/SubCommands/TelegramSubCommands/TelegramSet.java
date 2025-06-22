package it.fulminazzo.primaltribes.Commands.SubCommands.TelegramSubCommands;

import it.fulminazzo.primaltribes.Utils.StringUtils;
import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.ClanSubCommand;
import it.fulminazzo.primaltribes.Enums.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class TelegramSet extends ClanSubCommand {

    public TelegramSet(AbstractCommand command, String commandName) {
        super(command, "set", Permissions.TELEGRAM_SET, ClanPermission.TELEGRAM_SET,
                StringUtils.getCommandSyntax(String.format("&c%s &aset", commandName), "&flink"),
                Message.HELP_TELEGRAM_SET.getMessage(false, false),
                !command.isAdminCommand(), CommandType.CLAN_ONLY);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String[] args) {
        checkClan(sender);
        String link = String.join(" ", args);
        if (!StringUtils.validateString(link, ConfigOption.TELEGRAM_FORMAT.getString()))
            sender.sendMessage(Message.NOT_VALID_LINK.getMessage(!isAdminCommand(), isAdminCommand()));
        else if (ChatColor.translateAlternateColorCodes('&', clan.getTelegram()).equalsIgnoreCase(link)) {
            sender.sendMessage(Message.LINK_ALREADY_SET.getMessage(!isAdminCommand(), isAdminCommand()));
        } else {
            clan.setTelegram(sender, link);
            String telegram = clan.getTelegram();
            if (telegram == null)
                sender.sendMessage(Message.ENCRYPT.getMessage(!isAdminCommand(), isAdminCommand()));
            else sender.sendMessage(Message.NEW_TELEGRAM_SET.getMessage(!isAdminCommand(), isAdminCommand())
                    .replace("%telegram%", telegram));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) list.add("<link>");
        return list;
    }

    @Override
    public int getMinArguments() {
        return 1;
    }
}