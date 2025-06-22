package it.fulminazzo.primaltribes.Commands;

import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.Enums.Message;
import it.fulminazzo.primaltribes.Enums.Permissions;
import it.fulminazzo.primaltribes.Objects.Clan;
import it.fulminazzo.primaltribes.PrimalTribes;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ClansCommand extends AbstractCommand {
    private final PrimalTribes plugin;

    public ClansCommand(PrimalTribes plugin, String name) {
        super(plugin, name, new Permissions("clans"), Message.CLANS_COMMAND_DESCRIPTION.getMessage(false, false),
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
        execute(sender, Message.NO_CLANS, Message.CLANS_MESSAGE,
                plugin.getClansManager().getClans());
        return true;
    }
    
    public void execute(CommandSender sender, Message emptyClansMessage,
                        Message guiTitle, List<Clan> clans) {
        if (clans.isEmpty()) {
            sender.sendMessage(emptyClansMessage.getMessage(true, false));
            return;
        }

        sender.sendMessage(guiTitle.getMessage(false, false));
        clans.forEach(clan ->
                sender.sendMessage(Message.CLAN_FORMAT.getMessage(false, false)
                        .replace("%clan%", clan.getDisplayName())));
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args) throws IllegalArgumentException {
        return new ArrayList<>();
    }
}
