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

public class ToggleInvites extends ClanSubCommand {

    public ToggleInvites(AbstractCommand command) {
        super(command, "toggleinvites", Permissions.TOGGLE_INVITES, ClanPermission.TOGGLE_INVITES,
                StringUtils.getCommandSyntax(String.format("&c%s &atoggleinvites", command.getName())),
                Message.HELP_TOGGLE_INVITES.getMessage(false, false),
                !command.isAdminCommand(), CommandType.CLAN_ONLY, "tinvites");
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String[] args) {
        checkClan(sender);

        Message status;
        if (clan.areInvitesEnabled()) {
            clan.disableInvites(sender);
            status = Message.DISABLED_MALE_PLURAL;
        } else {
            clan.enableInvites(sender);
            status = Message.ENABLED_MALE_PLURAL;
        }

        sender.sendMessage(Message.CLAN_INVITES.getMessage(!isAdminCommand(), isAdminCommand())
                .replace("%status%", status.getMessage(false, false)));
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