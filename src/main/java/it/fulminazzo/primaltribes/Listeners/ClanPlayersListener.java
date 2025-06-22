package it.fulminazzo.primaltribes.Listeners;

import it.angrybear.Utils.HexUtils;
import it.fulminazzo.primaltribes.Enums.ClanPermission;
import it.fulminazzo.primaltribes.Enums.Message;
import it.fulminazzo.primaltribes.Enums.Permissions;
import it.fulminazzo.primaltribes.Managers.ClanPlayersManager;
import it.fulminazzo.primaltribes.Objects.Clan;
import it.fulminazzo.primaltribes.Objects.Role;
import it.fulminazzo.primaltribes.Objects.Users.ClanPlayer;
import it.fulminazzo.primaltribes.Objects.Users.Member;
import it.fulminazzo.primaltribes.PrimalTribes;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;

public class ClanPlayersListener implements Listener {
    private final PrimalTribes plugin;
    private final HashMap<UUID, String> playersAdminClan;
    private final HashMap<UUID, String> playersAdminAllyClan;

    public ClanPlayersListener(PrimalTribes plugin) {
        this.plugin = plugin;
        playersAdminClan = new HashMap<>();
        playersAdminAllyClan = new HashMap<>();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ClanPlayer clanPlayer = getClanPlayersManager().getClanPlayer(player);
        if (clanPlayer.isInClan() && clanPlayer.hasPermission(ClanPermission.MOTD)) {
            Clan clan = clanPlayer.getClan();
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> player.sendMessage(clan.getMOTD()));
            clan.notifyUsers(Message.NOTIFY_LOGGED_IN, Message.NOTIFY_ACTIONBAR_LOGGED_IN,
                    "player", player.getName());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ClanPlayer clanPlayer = getClanPlayersManager().getClanPlayer(player);
        if (clanPlayer.isInClan() && clanPlayer.hasPermission(ClanPermission.MOTD)) {
            Clan clan = clanPlayer.getClan();
            clan.notifyUsers(Message.NOTIFY_LOGGED_OUT, Message.NOTIFY_ACTIONBAR_LOGGED_OUT,
                    "player", player.getName());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        ClanPlayer clanPlayer = getClanPlayer(player);
        if (clanPlayer == null) return;
        Clan clan;
        if (!clanPlayer.isInClan()) {
            clan = plugin.getClansManager().getClan(playersAdminClan.getOrDefault(player.getUniqueId(), ""));
            if (clan == null) {
                clanPlayer.disableClanChat();
                return;
            }
            clan = plugin.getClansManager().getClan(playersAdminAllyClan.getOrDefault(player.getUniqueId(), ""));
            if (clan == null) {
                clanPlayer.disableAllyChat();
                return;
            }
        } else clan = clanPlayer.getClan();

        boolean clanChatColored = !clanPlayer.isInClan() || clanPlayer.hasPermission(ClanPermission.CHAT_COLORED);
        if (clanPlayer.isClanChatActivated() || isChatPlayer(player)) {
            if (clan.isClanChatMuted() && !clanPlayer.hasPermission(ClanPermission.BYPASS_MUTE_CHAT)) {
                player.sendMessage(Message.CLAN_CHAT_MUTED.getMessage(false, false));
                event.setCancelled(true);
                return;
            }
            Clan finalClan = clan;
            event.getRecipients().removeIf(r -> {
                ClanPlayer c = getClanPlayersManager().getClanPlayer(r);
                if (c.isClanChatMuted()) return true;
                if (!c.isInClan() || !c.getClan().equals(finalClan)) return true;
                if (isChatPlayer(r)) return false;
                if (c.isSocialSpyOn() && !player.hasPermission(Permissions.SOCIAL_SPY.getPermission(true))) {
                    c.disableSocialSpy();
                    return true;
                }
                return false;
            });
            String format = Message.CHAT_FORMAT.getMessage(false, false);
            if (format.equalsIgnoreCase("")) return;
            Member member = clanPlayer.getMember();
            Role role;
            if (member == null || !clan.equals(clanPlayer.getClan())) role = null;
            else role = member.getRole();
            format = format
                    .replace("%player%", "%1$s")
                    .replace("%message%", "%2$s")
                    .replace("%clan-name%", clan.getName())
                    .replace("%clan-displayname%", clan.getDisplayName())
                    .replace("%clan-tag%", clan.getTag())
                    .replace("%rank%", role == null ? "" : role.getDisplayName())
                    .replace("%role-prefix%", role == null ? "" : role.getPrefix());
            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
                format = PlaceholderAPI.setBracketPlaceholders(player, format);
            event.setFormat(format);
            String message = event.getMessage();
            if (player.hasPermission(Permissions.CHAT_COLORED.getPermission(isChatPlayer(player))) && clanChatColored)
                message = ChatColor.translateAlternateColorCodes('&', HexUtils.parseString(message));
            event.setMessage(message);
        }

        if (clanPlayer.isAllyChatActivated() || isAllyChatPlayer(player)) {
            Clan finalClan = clan;
            event.getRecipients().removeIf(r -> {
                ClanPlayer c = getClanPlayersManager().getClanPlayer(r);
                if (c.isClanChatMuted()) return true;
                if (!c.isInClan() || !finalClan.getAllies().contains(c.getClan())) return true;
                if (isAllyChatPlayer(r)) return false;
                if (c.isSocialSpyOn() && !player.hasPermission(Permissions.SOCIAL_SPY.getPermission(true))) {
                    c.disableSocialSpy();
                    return true;
                }
                return false;
            });
            String format = Message.ALLY_CHAT_FORMAT.getMessage(false, false);
            if (format.equalsIgnoreCase("")) return;
            Member member = clanPlayer.getMember();
            Role role;
            if (member == null) role = null;
            else role = member.getRole();
            format = format
                    .replace("%player%", "%1$s")
                    .replace("%message%", "%2$s")
                    .replace("%clan-name%", clan.getName())
                    .replace("%clan-displayname%", clan.getDisplayName())
                    .replace("%clan-tag%", clan.getTag())
                    .replace("%rank%", role == null ? "" : role.getDisplayName())
                    .replace("%role-prefix%", role == null ? "" : role.getPrefix());
            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
                format = PlaceholderAPI.setBracketPlaceholders(player, format);
            event.setFormat(format);
            String message = event.getMessage();
            if (player.hasPermission(Permissions.CHAT_COLORED.getPermission(isAllyChatPlayer(player))) && clanChatColored)
                message = ChatColor.translateAlternateColorCodes('&', HexUtils.parseString(message));
            event.setMessage(message);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Entity damagerEntity = event.getDamager();
        if (damagerEntity instanceof Projectile)
            damagerEntity = (Entity) ((Projectile) damagerEntity).getShooter();
        if (!(damagerEntity instanceof Player)) return;
        if (event.getEntity().equals(damagerEntity)) return;
        Player damaged = (Player) event.getEntity();
        Clan damagedClan = getClan(damaged);
        Player damager = (Player) damagerEntity;
        Clan damagerClan = getClan(damager);
        if (damagedClan == null || !damagedClan.equals(damagerClan)) return;
        if (damagedClan.isFriendlyFire()) return;
        event.setCancelled(true);
    }

    public ClanPlayer getClanPlayer(Player player) {
        if (player == null) return null;
        return getClanPlayersManager().getClanPlayer(player);
    }

    public Clan getClan(Player player) {
        ClanPlayer clanPlayer = getClanPlayer(player);
        if (clanPlayer == null) return null;
        return clanPlayer.getClan();
    }

    public boolean isChatPlayer(Player player) {
        return playersAdminClan.getOrDefault(player.getUniqueId(), null) != null;
    }

    public void addChatPlayer(Player player, Clan clan) {
        playersAdminClan.put(player.getUniqueId(), clan.getName());
    }

    public void removeChatPlayer(Player player) {
        playersAdminClan.remove(player.getUniqueId());
    }

    public boolean isAllyChatPlayer(Player player) {
        return playersAdminAllyClan.getOrDefault(player.getUniqueId(), null) != null;
    }

    public void addAllyChatPlayer(Player player, Clan clan) {
        playersAdminAllyClan.put(player.getUniqueId(), clan.getName());
    }

    public void removeAllyChatPlayer(Player player) {
        playersAdminAllyClan.remove(player.getUniqueId());
    }

    public ClanPlayersManager getClanPlayersManager() {
        return plugin.getClanPlayersManager();
    }

    public void quit() {
        playersAdminClan.clear();
        playersAdminAllyClan.clear();
    }
}