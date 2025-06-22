package it.fulminazzo.primaltribes.Commands.SubCommands;

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

public class UnSetTag extends ClanSubCommand {

    public UnSetTag(AbstractCommand command) {
        super(command, "unsettag", Permissions.UNSET_TAG, ClanPermission.UNSET_TAG,
                StringUtils.getCommandSyntax(String.format("&c%s &aunsettag", command.getName())),
                Message.HELP_UNSET_TAG.getMessage(false, false),
                !command.isAdminCommand(), CommandType.CLAN_ONLY);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String[] args) {
        checkClan(sender);
        clan.unsetTag(sender);
        sender.sendMessage(Message.TAG_UNSET.getMessage(!isAdminCommand(), isAdminCommand())
                .replace("%tag%", clan.getTag()));
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