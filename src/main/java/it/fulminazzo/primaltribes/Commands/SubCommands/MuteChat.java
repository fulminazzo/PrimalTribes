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

public class MuteChat extends ClanSubCommand {

    public MuteChat(AbstractCommand command) {
        super(command, "mutechat", Permissions.MUTE_CHAT, ClanPermission.MUTE_CHAT,
                StringUtils.getCommandSyntax(String.format("&c%s &amutechat", command.getName())),
                Message.HELP_MUTE_CHAT.getMessage(false, false),
                !command.isAdminCommand(), CommandType.CLAN_ONLY);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String[] args) {
        checkClan(sender);
        Message status;
        if (clan.isClanChatMuted()) {
            status = Message.ENABLED_FEMALE_SINGULAR;
            clan.unMuteClanChat(status, sender);
        } else {
            status = Message.DISABLED_FEMALE_SINGULAR;
            clan.muteClanChat(status, sender);
        }
        sender.sendMessage(Message.CLAN_CHAT_STATUS.getMessage(!isAdminCommand(), isAdminCommand())
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