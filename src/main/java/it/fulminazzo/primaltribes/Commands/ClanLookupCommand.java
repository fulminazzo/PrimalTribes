package it.fulminazzo.primaltribes.Commands;

import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.Enums.Message;
import it.fulminazzo.primaltribes.Enums.Permissions;
import it.fulminazzo.primaltribes.Managers.ClanPlayersManager;
import it.fulminazzo.primaltribes.Objects.Users.OfflineClanPlayer;
import it.fulminazzo.primaltribes.PrimalTribes;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClanLookupCommand extends AbstractCommand {
    private final PrimalTribes plugin;

    public ClanLookupCommand(PrimalTribes plugin, String name) {
        super(plugin, name, new Permissions("clan-lookup"), Message.CLANLOOKUP_COMMAND_DESCRIPTION.getMessage(false, false),
                ChatColor.translateAlternateColorCodes('&', String.format("&c/%s", name)));
        this.plugin = plugin;
        setPermissionMessage(Message.NO_PERMISSION.getMessage(true, false));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        String permission = getPermission();
        String permissionMessage = getPermissionMessage();
        if (permission != null && !sender.hasPermission(permission)) {
            if (permissionMessage != null) sender.sendMessage(permissionMessage);
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(Message.NO_PLAYER_SPECIFIED.getMessage(true, false));
            return true;
        }
        String argument = args[0];
        OfflineClanPlayer clanPlayer = plugin.getClanPlayersManager().getClanPlayer(argument);
        if (clanPlayer == null)
            clanPlayer = plugin.getClanPlayersManager().getOfflineClanPlayer(argument);
        if (clanPlayer == null)
            sender.sendMessage(Message.PLAYER_NOT_FOUND.getMessage(true, false)
                    .replace("%player%", argument));
        else if (!clanPlayer.isInClan())
            sender.sendMessage(Message.PLAYER_NOT_IN_CLAN.getMessage(true, false)
                    .replace("%player%", clanPlayer.getName()));
        else
            sender.sendMessage(Message.PLAYER_IN_CLAN.getMessage(true, false)
                    .replace("%player%", clanPlayer.getName())
                    .replace("%clan%", clanPlayer.getClan().getDisplayName()));
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args) throws IllegalArgumentException {
        List<String> list = new ArrayList<>();
        ClanPlayersManager clanPlayersManager = plugin.getClanPlayersManager();
        if (args.length == 1)
            list.addAll(clanPlayersManager.getAllPlayers().stream()
                            .filter(OfflineClanPlayer::isInClan)
                            .map(OfflineClanPlayer::getName)
                            .collect(Collectors.toList()));
        return StringUtil.copyPartialMatches(args[args.length - 1], list, new ArrayList<>());
    }
}
