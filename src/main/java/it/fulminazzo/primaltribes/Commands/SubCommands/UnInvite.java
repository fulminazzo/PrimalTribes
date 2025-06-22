package it.fulminazzo.primaltribes.Commands.SubCommands;

import it.fulminazzo.primaltribes.Utils.StringUtils;
import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.ListClanSubCommand;
import it.fulminazzo.primaltribes.Enums.*;
import it.fulminazzo.primaltribes.Objects.Clan;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class UnInvite extends ListClanSubCommand {

    public UnInvite(AbstractCommand command) {
        super(command, "uninvite", Permissions.UNINVITE, ClanPermission.UNINVITE,
                StringUtils.getCommandSyntax(String.format("&c%s &auninvite", command.getName()), "player"),
                Message.HELP_UNINVITE.getMessage(false, false),
                !command.isAdminCommand(), CommandType.CLAN_ONLY);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String[] args) {
        checkClan(sender);
        execute(sender, args, Message.NO_PLAYERS_TO_UNINVITE, Message.NO_PLAYER_SPECIFIED,
                getInvitedPlayers(clan),
                playerName -> {
                    if (sender instanceof Player) ((Player) sender).closeInventory();
                    if (!clan.isPlayerInvited(playerName)) {
                        sender.sendMessage(Message.PLAYER_NOT_INVITED.getMessage(!isAdminCommand(), isAdminCommand())
                                .replace("%player%", playerName));
                    } else {
                        clan.unInvitePlayer(sender, playerName);
                        sender.sendMessage(Message.PLAYER_UNINVITED.getMessage(!isAdminCommand(), isAdminCommand())
                                .replace("%player%", playerName));
                    }
                });
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String[] args) {
        List<String> list = new ArrayList<>();
        checkClan(sender);
        if (args.length == 1) list.addAll(getInvitedPlayers(clan));
        return list;
    }

    @Override
    public int getMinArguments() {
        return 0;
    }

    private List<String> getInvitedPlayers(Clan clan) {
        return clan.getInvitesName().stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
    }
}
