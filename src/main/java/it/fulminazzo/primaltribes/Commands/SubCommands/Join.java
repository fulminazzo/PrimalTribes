package it.fulminazzo.primaltribes.Commands.SubCommands;

import it.fulminazzo.primaltribes.Utils.StringUtils;
import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.ClanSubCommand;
import it.fulminazzo.primaltribes.Enums.*;
import it.fulminazzo.primaltribes.Objects.Clan;
import it.fulminazzo.primaltribes.Objects.Users.ClanPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Join extends ClanSubCommand {

    public Join(AbstractCommand command) {
        super(command, "join", Permissions.JOIN, ClanPermission.JOIN,
                StringUtils.getCommandSyntax(String.format("&c%s &ajoin", command.getName()), "clan"),
                Message.HELP_JOIN.getMessage(false, false),
                !command.isAdminCommand(), CommandType.NO_CLAN_ONLY);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String[] args) {
        Player player = (Player) sender;
        ClanPlayer clanPlayer = getClanPlayersManager().getClanPlayer(player);
        List<Clan> clanInvites = getClanInvites(player);
        Consumer<Clan> joinClanAction = (clan -> {
            if (clan.isPlayerBanned((Player) sender))
                sender.sendMessage(Message.ERRORS_BANNED.getMessage(!isAdminCommand(), isAdminCommand())
                        .replace("%clan%", clan.getDisplayName()));
            else if (clan.getMembers().size() >= ConfigOption.MEMBERS_PER_CLAN.getInt()) {
                sender.sendMessage(Message.MAXIMUM_MEMBERS_REACHED.getMessage(!isAdminCommand(), isAdminCommand())
                        .replace("%clan%", clan.getDisplayName()));
                clan.unInvitePlayer(null, player.getName());
            } else {
                player.closeInventory();
                sender.sendMessage(Message.JOINED_CLAN.getMessage(!isAdminCommand(), isAdminCommand())
                        .replace("%clan%", clan.getDisplayName()));
                clanPlayer.acceptInvite(clan);
            }
        });
        if (args.length == 0) {
            if (clanInvites.size() == 0)
                sender.sendMessage(Message.NO_INVITES.getMessage(!isAdminCommand(), isAdminCommand()));
            else if (clanInvites.size() == 1 && !clanPlayer.getClansByInvite().isEmpty())
                joinClanAction.accept(clanInvites.get(0));
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
            else joinClanAction.accept(clan);
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