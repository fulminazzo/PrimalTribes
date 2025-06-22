package it.fulminazzo.primaltribes.Managers;

import it.angrybear.Utils.FileUtils;
import it.fulminazzo.primaltribes.Api.Events.AddClanEvent;
import it.fulminazzo.primaltribes.Api.Events.RemoveClanEvent;
import it.fulminazzo.primaltribes.Enums.Message;
import it.fulminazzo.primaltribes.Exceptions.ClanException;
import it.fulminazzo.primaltribes.Objects.Clan;
import it.fulminazzo.primaltribes.Objects.Users.ClanPlayer;
import it.fulminazzo.primaltribes.PrimalTribes;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ClansManager {
    private final PrimalTribes plugin;
    private final File clansFolder;
    private final List<Clan> clans;

    public ClansManager(PrimalTribes plugin) throws IOException {
        this.plugin = plugin;
        this.clansFolder = getAndCreateFolder(plugin.getDataFolder(), "Clans");
        File[] clansFiles = clansFolder.listFiles();
        this.clans = clansFiles == null ? new ArrayList<>() :
                Arrays.stream(clansFiles).map(f -> {
                    try {
                        return new Clan(plugin, f.getName(), f);
                    } catch (ClanException e) {
                        PrimalTribes.logError(String.format("There was an error while getting clan %s!", f.getName()));
                        PrimalTribes.logError(String.format("Error message: %s", e.getMessage()));
                        return null;
                    }
                }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public void updatePlayer(Player player) {
        clans.forEach(clan -> clan.update(player));
    }

    public Clan getClan(String name) {
        return clans.stream().filter(c -> c.getName().equalsIgnoreCase(name)).findAny().orElse(null);
    }

    public Clan getClanByDisplayName(String name) {
        return clans.stream().filter(c ->
                        ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', c.getDisplayName()))
                                .equalsIgnoreCase(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', name))))
                .findAny().orElse(null);
    }

    public Clan getClanByTag(String tag) {
        return clans.stream().filter(c ->
                        ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', c.getTag()))
                                .equalsIgnoreCase(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', tag))))
                .findAny().orElse(null);
    }

    public boolean createClan(String name, Player player, boolean isAdminCommand) {
        try {
            Clan clan = getClan(name);
            ClanPlayer clanPlayer = plugin.getClanPlayersManager().getClanPlayer(player);
            if (clan != null || clanPlayer == null) return false;
            try {
                clan = new Clan(plugin, name, getAndCreateFolder(clansFolder, name), player);
                AddClanEvent clanEvent = new AddClanEvent(clan, player, isAdminCommand);
                Bukkit.getPluginManager().callEvent(clanEvent);
                if (clanEvent.isCancelled()) {
                    clan.destroy(false);
                    return false;
                }
            } catch (IOException e) {
                PrimalTribes.logWarning(e.getMessage());
                throw new ClanException(e.getMessage());
            }
            clans.add(clan);
            clanPlayer.addPlayerToClan(clan);
            return true;
        } catch (ClanException e) {
            player.sendMessage(Message.ENCRYPT.getMessage(!isAdminCommand, isAdminCommand));
            return false;
        }
    }

    public void deleteClan(String name) {
        Clan clan = getClan(name);
        if (clan == null) return;
        RemoveClanEvent clanEvent = new RemoveClanEvent(clan);
        Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getPluginManager().callEvent(clanEvent));
        if (clanEvent.isCancelled()) return;
        clan.destroy();
        clans.remove(clan);
    }

    public List<Clan> getOpenClans() {
        return clans.stream().filter(Clan::isOpened).collect(Collectors.toList());
    }

    public List<Clan> getClans() {
        return clans;
    }

    private File getAndCreateFolder(File parentFile, String name) throws IOException {
        File folder = new File(parentFile, name);
        if (!folder.isDirectory()) FileUtils.createFolder(folder);
        return folder;
    }

    public void reload() {
        if (clans != null) clans.forEach(Clan::save);
    }
}