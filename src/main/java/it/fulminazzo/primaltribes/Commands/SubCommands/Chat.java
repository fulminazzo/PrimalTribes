package it.fulminazzo.primaltribes.Commands.SubCommands;

import it.fulminazzo.primaltribes.Utils.StringUtils;
import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.ClanSubCommand;
import it.fulminazzo.primaltribes.Enums.ClanPermission;
import it.fulminazzo.primaltribes.Enums.CommandType;
import it.fulminazzo.primaltribes.Enums.Message;
import it.fulminazzo.primaltribes.Enums.Permissions;
import it.fulminazzo.primaltribes.Listeners.ClanPlayersListener;
import it.fulminazzo.primaltribes.Objects.Users.ClanPlayer;
import it.fulminazzo.primaltribes.PrimalTribes;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Chat extends ClanSubCommand {

    public Chat(AbstractCommand command) {
        super(command, "chat", Permissions.CHAT, ClanPermission.CHAT,
                StringUtils.getCommandSyntax(String.format("&c%s &achat", command.getName()), "message"),
                Message.HELP_CHAT.getMessage(false, false),
                true, CommandType.CLAN_ONLY);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String[] args) {
        Player player = (Player) sender;
        ClanPlayer clanPlayer = getClanPlayersManager().getClanPlayer(player);
        ClanPlayersListener clanPlayersListener = PrimalTribes.getPlugin().getClanPlayersListener();
        String message = String.join(" ", args);

        if (args.length == 0 || ChatColor.stripColor(message).replace(" ", "").equalsIgnoreCase("")) {
            Message status;
            if (isAdminCommand() && !clanPlayer.getClan().equals(clan)) {
                if (!clanPlayersListener.isChatPlayer(player)) {
                    clanPlayersListener.addChatPlayer(player, clan);
                    status = Message.ENABLED_FEMALE_SINGULAR;
                } else {
                    clanPlayersListener.removeChatPlayer(player);
                    status = Message.DISABLED_FEMALE_SINGULAR;
                }
            } else {
                if (clanPlayer.isClanChatActivated()) {
                    clanPlayer.disableClanChat();
                    status = Message.DISABLED_FEMALE_SINGULAR;
                } else {
                    clanPlayer.enableClanChat();
                    status = Message.ENABLED_FEMALE_SINGULAR;
                }
            }
            sender.sendMessage(Message.CLAN_CHAT.getMessage(!isAdminCommand(), isAdminCommand())
                    .replace("%status%", status.getMessage(false, false)));
        } else {
            boolean activatedBefore = clanPlayer.isClanChatActivated();
            if (!activatedBefore)
                if (isAdminCommand() && !clanPlayer.getClan().equals(clan))
                    clanPlayersListener.addChatPlayer(player, clan);
                else clanPlayer.enableClanChat();
            player.chat(message);
            if (!activatedBefore)
                if (isAdminCommand() && !clanPlayer.getClan().equals(clan))
                    clanPlayersListener.removeChatPlayer(player);
                else clanPlayer.disableClanChat();
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) list.add("<message>");
        return list;
    }

    @Override
    public int getMinArguments() {
        return 0;
    }
}