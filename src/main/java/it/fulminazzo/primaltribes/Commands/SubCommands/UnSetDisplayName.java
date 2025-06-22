package it.fulminazzo.primaltribes.Commands.SubCommands;

import it.fulminazzo.primaltribes.Utils.StringUtils;
import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.ClanSubCommand;
import it.fulminazzo.primaltribes.Enums.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class UnSetDisplayName extends ClanSubCommand {

    public UnSetDisplayName(AbstractCommand command) {
        super(command, "unsetdisplayname", Permissions.UNSET_DISPLAYNAME, ClanPermission.UNSET_DISPLAYNAME,
                StringUtils.getCommandSyntax(String.format("&c%s &aunsetdisplayname", command.getName())),
                Message.HELP_UNSET_DISPLAYNAME.getMessage(false, false),
                !command.isAdminCommand(), CommandType.CLAN_ONLY);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String[] args) {
        checkClan(sender);
        clan.unsetDisplayName(sender);
        sender.sendMessage(Message.DISPLAYNAME_UNSET.getMessage(!isAdminCommand(), isAdminCommand())
                .replace("%display-name%", clan.getDisplayName()));
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