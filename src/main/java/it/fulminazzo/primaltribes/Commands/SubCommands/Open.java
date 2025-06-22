package it.fulminazzo.primaltribes.Commands.SubCommands;

import it.fulminazzo.primaltribes.Utils.StringUtils;
import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.ConfirmClanSubCommand;
import it.fulminazzo.primaltribes.Enums.ClanPermission;
import it.fulminazzo.primaltribes.Enums.CommandType;
import it.fulminazzo.primaltribes.Enums.Message;
import it.fulminazzo.primaltribes.Enums.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class Open extends ConfirmClanSubCommand {

    public Open(AbstractCommand command) {
        super(command, "open", Permissions.OPEN, ClanPermission.OPEN,
                StringUtils.getCommandSyntax(String.format("&c%s &aopen", command.getName())),
                Message.HELP_OPEN.getMessage(false, false),
                !command.isAdminCommand(), CommandType.CLAN_ONLY);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String[] args) {
        checkClan(sender);
        if (clan.isOpened())
            sender.sendMessage(Message.CLAN_ALREADY_OPENED.getMessage(!isAdminCommand(), isAdminCommand()));
        else execute(sender,
                () -> {
            clan.open(sender);
            sender.sendMessage(Message.CLAN_OPENED.getMessage(!isAdminCommand(), isAdminCommand()));
        });
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public int getMinArguments() {
        return 0;
    }
}