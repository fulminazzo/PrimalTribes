package it.fulminazzo.primaltribes.Commands.SubCommands;

import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.ClanSubCommand;
import it.fulminazzo.primaltribes.Enums.ClanPermission;
import it.fulminazzo.primaltribes.Enums.CommandType;
import it.fulminazzo.primaltribes.Enums.Message;
import it.fulminazzo.primaltribes.Enums.Permissions;
import it.fulminazzo.primaltribes.Objects.Clan;
import it.fulminazzo.primaltribes.Objects.Users.ClanPlayer;
import it.fulminazzo.primaltribes.Utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Decline extends ClanSubCommand {

    public Decline(AbstractCommand command) {
        super(command, "decline", Permissions.DECLINE, ClanPermission.DECLINE,
                StringUtils.getCommandSyntax(String.format("&c%s &adecline", command.getName()), "clan"),
                Message.HELP_DECLINE.getMessage(false, false),
                !command.isAdminCommand(), CommandType.NO_CLAN_ONLY);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String[] args) {
        Player player = (Player) sender;
        ClanPlayer clanPlayer = getClanPlayersManager().getClanPlayer(player);
        List<Clan> clanInvites = getClanInvites(player);
        Consumer<Clan> declineClanAction = (clan -> {
            player.closeInventory();
            sender.sendMessage(Message.DECLINED_INVITE.getMessage(!isAdminCommand(), isAdminCommand())
                    .replace("%clan%", clan.getDisplayName()));
            clan.unInvitePlayer(null, player.getName());
            clan.notifyUsers(Message.NOTIFY_DECLINE_INVITE, Message.NOTIFY_ACTIONBAR_DECLINE_INVITE,
                    "player", player.getName());
        });
        if (args.length == 0) {
            if (clanInvites.size() == 0)
                sender.sendMessage(Message.NO_INVITES.getMessage(!isAdminCommand(), isAdminCommand()));
            else if (clanInvites.size() == 1 && !clanPlayer.getClansByInvite().isEmpty())
                declineClanAction.accept(clanInvites.get(0));
            else {
                sender.sendMessage(Message.CHOOSE_CLAN.getMessage(!isAdminCommand(), isAdminCommand()));
            }
        } else {
            String argument = args[0];
            Clan clan = getClansManager().getClan(argument);
            if (clan == null)
                sender.sendMessage(Message.CLAN_NOT_FOUND.getMessage(!isAdminCommand(), isAdminCommand())
                        .replace("%clan%", argument));
            else if (clan.isPlayerBanned((Player) sender))
                sender.sendMessage(Message.ERRORS_BANNED.getMessage(!isAdminCommand(), isAdminCommand())
                        .replace("%clan%", clan.getDisplayName()));
            else if (!clanInvites.contains(clan))
                sender.sendMessage(Message.NOT_INVITED.getMessage(!isAdminCommand(), isAdminCommand())
                        .replace("%clan%", argument));
            else declineClanAction.accept(clan);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) list = getClanInvites(sender).stream().map(Clan::getName).collect(Collectors.toList());
        return list;
    }

    @Override
    public int getMinArguments() {
        return 0;
    }

    private List<Clan> getClanInvites(CommandSender sender) {
        Player player = (Player) sender;
        ClanPlayer clanPlayer = getClanPlayersManager().getClanPlayer(player);
        List<Clan> clanInvites = clanPlayer.getClansByInvite();
        return Stream.concat(
                        clanInvites.stream(),
                        getClansManager().getOpenClans().stream()
                )
                .filter(c -> !c.isPlayerBanned(player))
                .distinct()
                .collect(Collectors.toList());
    }
}