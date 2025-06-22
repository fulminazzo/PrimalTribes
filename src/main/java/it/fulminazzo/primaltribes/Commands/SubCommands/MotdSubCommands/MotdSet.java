package it.fulminazzo.primaltribes.Commands.SubCommands.MotdSubCommands;

import it.fulminazzo.primaltribes.Utils.StringUtils;
import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.ClanSubCommand;
import it.fulminazzo.primaltribes.Enums.ClanPermission;
import it.fulminazzo.primaltribes.Enums.CommandType;
import it.fulminazzo.primaltribes.Enums.Message;
import it.fulminazzo.primaltribes.Enums.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class MotdSet extends ClanSubCommand {

    public MotdSet(AbstractCommand command, String commandName) {
        super(command, "set", Permissions.MOTD_SET, ClanPermission.MOTD_SET,
                StringUtils.getCommandSyntax(String.format("&c%s &aset", commandName), "&7message"),
                Message.HELP_MOTD_SET.getMessage(false, false),
                !command.isAdminCommand(), CommandType.CLAN_ONLY);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String[] args) {
        checkClan(sender);
        String motd = String.join(" ", args);
        if (clan.getMOTD().equals(motd))
            sender.sendMessage(Message.MOTD_ALREADY_SET.getMessage(!isAdminCommand(), isAdminCommand()));
        else {
            clan.setMOTD(sender, motd);
            sender.sendMessage(Message.NEW_MOTD_SET.getMessage(!isAdminCommand(), isAdminCommand()).replace("%motd%", clan.getMOTD()));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) list.add("<message>");
        return list;
    }

    @Override
    public int getMinArguments() {
        return 1;
    }
}