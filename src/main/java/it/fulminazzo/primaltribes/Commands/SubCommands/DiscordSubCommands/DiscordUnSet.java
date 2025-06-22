package it.fulminazzo.primaltribes.Commands.SubCommands.DiscordSubCommands;

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

public class DiscordUnSet extends ClanSubCommand {

    public DiscordUnSet(AbstractCommand command, String commandName) {
        super(command, "unset", Permissions.DISCORD_UNSET, ClanPermission.DISCORD_UNSET,
                StringUtils.getCommandSyntax(String.format("&c%s &aunset", commandName)),
                Message.HELP_DISCORD_UNSET.getMessage(false, false),
                !command.isAdminCommand(), CommandType.CLAN_ONLY);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String[] args) {
        checkClan(sender);
        clan.unsetDiscord(sender);
        sender.sendMessage(Message.DISCORD_UNSET.getMessage(!isAdminCommand(), isAdminCommand()));
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