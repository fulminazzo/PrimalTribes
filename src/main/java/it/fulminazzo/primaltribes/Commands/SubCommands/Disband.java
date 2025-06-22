package it.fulminazzo.primaltribes.Commands.SubCommands;

import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.ConfirmClanSubCommand;
import it.fulminazzo.primaltribes.Enums.ClanPermission;
import it.fulminazzo.primaltribes.Enums.CommandType;
import it.fulminazzo.primaltribes.Enums.Message;
import it.fulminazzo.primaltribes.Enums.Permissions;
import it.fulminazzo.primaltribes.Utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Disband extends ConfirmClanSubCommand {

    public Disband(AbstractCommand command) {
        super(command, "disband", Permissions.DISBAND, ClanPermission.DISBAND,
                StringUtils.getCommandSyntax(String.format("&c%s &adisband", command.getName())),
                Message.HELP_DISBAND.getMessage(false, false),
                !command.isAdminCommand(), CommandType.CLAN_ONLY);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String[] args) {
        checkClan(sender);
        Runnable disbandAction = () -> {
            getClansManager().deleteClan(clan.getName());
            sender.sendMessage(Message.CLAN_DISBANDED.getMessage(!isAdminCommand(), isAdminCommand())
                    .replace("%clan%", clan.getDisplayName()));
        };
        Runnable cancelAction = () -> sender.sendMessage(Message.DISBAND_CANCELLED.getMessage(!isAdminCommand(), isAdminCommand()));
        if (sender instanceof Player) {
            getClanPlayersManager().getClanPlayer((Player) sender).addChatConfirmation(message -> {
                if (!message.equals(clan.getName())) cancelAction.run();
                else disbandAction.run();
            }, cancelAction);
            sender.sendMessage(Message.DISBAND_CONFIRM.getMessage(!isAdminCommand(), isAdminCommand()));
        } else disbandAction.run();
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