package it.fulminazzo.primaltribes.Commands.SubCommands;

import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.ClanSubCommand;
import it.fulminazzo.primaltribes.Enums.ClanPermission;
import it.fulminazzo.primaltribes.Enums.CommandType;
import it.fulminazzo.primaltribes.Enums.Message;
import it.fulminazzo.primaltribes.Enums.Permissions;
import it.fulminazzo.primaltribes.Listeners.ClanPlayersListener;
import it.fulminazzo.primaltribes.Objects.Users.ClanPlayer;
import it.fulminazzo.primaltribes.PrimalTribes;
import it.fulminazzo.primaltribes.Utils.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AllyChat extends ClanSubCommand {

    public AllyChat(AbstractCommand command) {
        super(command, "allychat", Permissions.ALLY_CHAT, ClanPermission.ALLY_CHAT,
                StringUtils.getCommandSyntax(String.format("&c%s &aallychat", command.getName()), "message"),
                Message.HELP_ALLY_CHAT.getMessage(false, false),
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
                if (!clanPlayersListener.isAllyChatPlayer(player)) {
                    clanPlayersListener.addAllyChatPlayer(player, clan);
                    status = Message.ENABLED_FEMALE_SINGULAR;
                } else {
                    clanPlayersListener.removeAllyChatPlayer(player);
                    status = Message.DISABLED_FEMALE_SINGULAR;
                }
            } else {
                if (clanPlayer.isAllyChatActivated()) {
                    clanPlayer.disableAllyChat();
                    status = Message.DISABLED_FEMALE_SINGULAR;
                } else {
                    clanPlayer.enableAllyChat();
                    status = Message.ENABLED_FEMALE_SINGULAR;
                }
            }
            sender.sendMessage(Message.CLAN_ALLY_CHAT.getMessage(!isAdminCommand(), isAdminCommand())
                    .replace("%status%", status.getMessage(false, false)));
        } else {
            boolean activatedBefore = clanPlayer.isAllyChatActivated();
            if (!activatedBefore)
                if (isAdminCommand() && !clanPlayer.getClan().equals(clan))
                    clanPlayersListener.addAllyChatPlayer(player, clan);
                else clanPlayer.enableAllyChat();
            player.chat(message);
            if (!activatedBefore)
                if (isAdminCommand() && !clanPlayer.getClan().equals(clan))
                    clanPlayersListener.removeAllyChatPlayer(player);
                else clanPlayer.disableAllyChat();
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