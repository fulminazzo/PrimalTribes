package it.fulminazzo.primaltribes.Commands.SubCommands;

import it.fulminazzo.primaltribes.Utils.StringUtils;
import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.ClanSubCommand;
import it.fulminazzo.primaltribes.Enums.ClanPermission;
import it.fulminazzo.primaltribes.Enums.CommandType;
import it.fulminazzo.primaltribes.Enums.Message;
import it.fulminazzo.primaltribes.Enums.Permissions;
import it.fulminazzo.primaltribes.Objects.Users.ClanPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SilenceChat extends ClanSubCommand {

    public SilenceChat(AbstractCommand command) {
        super(command, "silencechat", Permissions.SILENCE_CHAT, ClanPermission.SILENCE_CHAT,
                StringUtils.getCommandSyntax(String.format("&c%s &asilencechat", command.getName())),
                Message.HELP_SILENCE_CHAT.getMessage(false, false),
                !command.isAdminCommand(), CommandType.CLAN_ONLY);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String[] args) {
        Player player = (Player) sender;
        ClanPlayer clanPlayer = getClanPlayersManager().getClanPlayer(player);

        Message status;
        if (clanPlayer.isClanChatMuted()) {
            clanPlayer.unMuteClanChat();
            status = Message.ENABLED_FEMALE_SINGULAR;
        } else {
            clanPlayer.muteClanChat();
            status = Message.DISABLED_FEMALE_SINGULAR;
        }
        sender.sendMessage(Message.CLAN_CHAT_SILENCED.getMessage(!isAdminCommand(), isAdminCommand())
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