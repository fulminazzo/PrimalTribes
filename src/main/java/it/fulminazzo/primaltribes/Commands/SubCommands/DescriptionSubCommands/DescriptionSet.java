package it.fulminazzo.primaltribes.Commands.SubCommands.DescriptionSubCommands;

import it.fulminazzo.primaltribes.Utils.StringUtils;
import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.ClanSubCommand;
import it.fulminazzo.primaltribes.Enums.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class DescriptionSet extends ClanSubCommand {

    public DescriptionSet(AbstractCommand command, String commandName) {
        super(command, "set", Permissions.DESCRIPTION_SET, ClanPermission.DESCRIPTION_SET,
                StringUtils.getCommandSyntax(String.format("&c%s &aset", commandName), "&7description"),
                Message.HELP_DESCRIPTION_SET.getMessage(false, false),
                !command.isAdminCommand(), CommandType.CLAN_ONLY);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String[] args) {
        checkClan(sender);
        String description = String.join(" ", args);
        if (ChatColor.translateAlternateColorCodes('&', clan.getDescription()).equalsIgnoreCase(description)) {
            sender.sendMessage(Message.DESCRIPTION_ALREADY_SET.getMessage(!isAdminCommand(), isAdminCommand()));
        } else {
            clan.setDescription(sender, description);
            description = clan.getDescription();
            sender.sendMessage(Message.NEW_DESCRIPTION_SET.getMessage(!isAdminCommand(), isAdminCommand())
                    .replace("%description%", description));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) list.add("<description>");
        return list;
    }

    @Override
    public int getMinArguments() {
        return 1;
    }
}