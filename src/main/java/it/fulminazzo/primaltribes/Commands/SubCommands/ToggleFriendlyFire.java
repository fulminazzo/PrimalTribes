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

public class ToggleFriendlyFire extends ClanSubCommand {

    public ToggleFriendlyFire(AbstractCommand command) {
        super(command, "togglefriendlyfire", Permissions.TOGGLE_FRIENDLY_FIRE, ClanPermission.TOGGLE_FRIENDLY_FIRE,
                StringUtils.getCommandSyntax(String.format("&c%s &atogglefriendlyfire", command.getName())),
                Message.HELP_TOGGLE_FRIENDLY_FIRE.getMessage(false, false),
                !command.isAdminCommand(), CommandType.CLAN_ONLY, "friendlyfire", "toggleff", "tff");
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String[] args) {
        checkClan(sender);

        Message status;
        if (clan.isFriendlyFire()) status = Message.DISABLED_MALE_SINGULAR;
        else status = Message.ENABLED_MALE_SINGULAR;

        clan.swapFriendlyFire(sender, status);

        sender.sendMessage(Message.CLAN_FRIENDLY_FIRE.getMessage(!isAdminCommand(), isAdminCommand())
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