package it.fulminazzo.primaltribes.Commands.SubCommands.AdminSubCommands;

import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.ListClanSubCommand;
import it.fulminazzo.primaltribes.Enums.*;
import it.fulminazzo.primaltribes.Objects.Clan;
import it.fulminazzo.primaltribes.Utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CreateAdmin extends ListClanSubCommand {

    public CreateAdmin(AbstractCommand command) {
        super(command, "create", Permissions.CREATE_ADMIN, ClanPermission.ALL,
                StringUtils.getCommandSyntax(String.format("&c%s &acreate", command.getName()), "&9name", "player"),
                Message.HELP_CREATE_ADMIN.getMessage(false, false),
                false, CommandType.GENERAL);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String[] args) {
        String clanName = args[0];
        Clan clan = getClansManager().getClan(clanName);
        if (clan != null)
            sender.sendMessage(Message.CLAN_ALREADY_EXISTS.getMessage(!isAdminCommand(), isAdminCommand())
                    .replace("%clan%", clan.getName()));
        else if (ConfigOption.BANNED_WORDS.getStringList().stream().anyMatch(w -> w.toLowerCase()
                .contains(clanName.toLowerCase())))
            sender.sendMessage(Message.BANNED_WORD.getMessage(!isAdminCommand(), isAdminCommand())
                    .replace("%word%", clanName));
        else if (!clanName.matches("[a-zA-Z0-9]+"))
            sender.sendMessage(Message.CHARACTERS_NOT_ALLOWED.getMessage(!isAdminCommand(), isAdminCommand()));
        else {
            execute(sender, cmd, Arrays.copyOfRange(args, 1, args.length), Message.NO_PLAYERS,
                    Message.NO_PLAYER_SPECIFIED,
                    getPlayers(), player -> {
                if (getClanPlayersManager().getClanPlayer(player).isInClan())
                    sender.sendMessage(Message.PLAYER_ALREADY_IN_CLAN.getMessage(!isAdminCommand(), isAdminCommand())
                            .replace("%player%", player.getName()));
                else if (getClansManager().createClan(clanName, player, isAdminCommand())) {
                    sender.sendMessage(Message.CLAN_CREATED.getMessage(!isAdminCommand(), isAdminCommand())
                            .replace("%clan%", clanName));
                    player.sendMessage(Message.CLAN_CREATED.getMessage(!isAdminCommand(), isAdminCommand())
                            .replace("%clan%", clanName));
                    String broadcastMessage = Message.NOTIFY_CLAN_CREATED.getMessage(false, false)
                            .replace("%player%", player.getDisplayName())
                            .replace("%clan-name%", clanName);
                    if (!broadcastMessage.equalsIgnoreCase(""))
                        Bukkit.broadcastMessage(broadcastMessage);
                }
            });
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) list.add("<name>");
        if (args.length == 2 && getClansManager().getClan(args[0]) == null)
            list.addAll(getPlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList()));
        return list;
    }

    @Override
    public int getMinArguments() {
        return 1;
    }

    private List<Player> getPlayers() {
        return Bukkit.getOnlinePlayers().stream()
                .filter(p -> !getClanPlayersManager().getClanPlayer(p).isInClan())
                .collect(Collectors.toList());
    }
}