package it.fulminazzo.primaltribes.Listeners;

import it.fulminazzo.primaltribes.Objects.Users.ClanPlayer;
import it.fulminazzo.primaltribes.PrimalTribes;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    private final PrimalTribes plugin;

    public PlayerListener(PrimalTribes plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        ClanPlayer clanPlayer = plugin.getClanPlayersManager().getClanPlayer(event.getPlayer());
        String message = event.getMessage();
        if (clanPlayer == null) return;
        if (!clanPlayer.isChatConfirmationOn()) return;
        if (message.equalsIgnoreCase("cancel")) clanPlayer.cancelChatConfirmation();
        else clanPlayer.executeChatConfirmation(message);
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getClanPlayersManager().addClanPlayer(player);
        plugin.getClansManager().updatePlayer(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getClanPlayersManager().removeClanPlayer(event.getPlayer());
    }
}