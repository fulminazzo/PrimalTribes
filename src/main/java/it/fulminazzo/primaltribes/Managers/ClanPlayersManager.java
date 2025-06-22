package it.fulminazzo.primaltribes.Managers;

import it.angrybear.Enums.BearLoggingMessage;
import it.angrybear.Utils.FileUtils;
import it.fulminazzo.primaltribes.Objects.Users.ClanPlayer;
import it.fulminazzo.primaltribes.Objects.Users.OfflineClanPlayer;
import it.fulminazzo.primaltribes.PrimalTribes;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClanPlayersManager {
    private final PrimalTribes plugin;
    private final File playersFolder;
    private final List<ClanPlayer> clanPlayers;
    private final List<OfflineClanPlayer> offlineClanPlayers;

    public ClanPlayersManager(PrimalTribes plugin) throws IOException {
        this.plugin = plugin;
        File playersFolder = new File(plugin.getDataFolder(), "Players");
        if (!playersFolder.isDirectory()) FileUtils.createFolder(playersFolder);
        this.playersFolder = playersFolder;
        this.clanPlayers = new ArrayList<>();
        this.offlineClanPlayers = new ArrayList<>();
        Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getUniqueId).forEach(this::addOfflineClanPlayer);
        Bukkit.getOnlinePlayers().forEach(this::addClanPlayer);
    }

    public void addClanPlayer(Player player) {
        ClanPlayer clanPlayer = getClanPlayer(player);
        if (clanPlayer != null) return;
        removeOfflineClanPlayer(player.getUniqueId());
        try {
            clanPlayer = new ClanPlayer(plugin, player, playersFolder);
            clanPlayers.add(clanPlayer);
        } catch (IOException | IllegalArgumentException e) {
            PrimalTribes.logWarning(BearLoggingMessage.GENERAL_ERROR_OCCURRED.getMessage(
                    "%task%", String.format("creating player %s", player.getName()),
                    "%error%", e.getMessage()));
        }
    }

    private void addOfflineClanPlayer(UUID uuid) {
        OfflineClanPlayer offlineClanPlayer = getOfflineClanPlayer(uuid);
        removeClanPlayer(getClanPlayer(uuid));
        if (offlineClanPlayer != null) return;
        offlineClanPlayer = addOfflineClanPlayer(new File(playersFolder, uuid.toString() + ".yml"));
        OfflineClanPlayer finalOfflineClanPlayer = offlineClanPlayer;
        if (finalOfflineClanPlayer != null)
            Arrays.stream(Bukkit.getOfflinePlayers()).filter(p -> p.getUniqueId().equals(uuid))
                .findAny().ifPresent(p -> finalOfflineClanPlayer.updateName(p.getName()));
    }

    private OfflineClanPlayer addOfflineClanPlayer(File file) {
        try {
            OfflineClanPlayer offlineClanPlayer = new OfflineClanPlayer(plugin, file);
            offlineClanPlayers.add(offlineClanPlayer);
            return offlineClanPlayer;
        } catch (IOException | IllegalArgumentException e) {
            PrimalTribes.logWarning(BearLoggingMessage.GENERAL_ERROR_OCCURRED.getMessage(
                    "%task%", "loading offline player", "%error%", e.getMessage()));
            return null;
        }
    }

    public void removeClanPlayer(Player player) {
        ClanPlayer clanPlayer = getClanPlayer(player);
        removeClanPlayer(clanPlayer);
        addOfflineClanPlayer(player.getUniqueId());
    }

    public void removeClanPlayer(ClanPlayer clanPlayer) {
        if (clanPlayer == null) return;
        try {
            clanPlayer.quitPlayer();
            clanPlayers.remove(clanPlayer);
        } catch (IOException e) {
            PrimalTribes.logWarning(BearLoggingMessage.GENERAL_ERROR_OCCURRED.getMessage(
                    "%task%", String.format("saving player %s", clanPlayer.getUuid()), "%error%", e.getMessage()));
        }
    }

    public void removeOfflineClanPlayer(UUID uuid) {
        OfflineClanPlayer offlineClanPlayer = getOfflineClanPlayer(uuid);
        removeOfflineClanPlayer(offlineClanPlayer);
    }

    public void removeOfflineClanPlayer(OfflineClanPlayer offlineClanPlayer) {
        if (offlineClanPlayer == null) return;
        try {
            offlineClanPlayer.save();
        } catch (IOException e) {
            PrimalTribes.logWarning(BearLoggingMessage.GENERAL_ERROR_OCCURRED.getMessage(
                    "%task%", "removing player", "%error%", e.getMessage()));
        }
        offlineClanPlayers.remove(offlineClanPlayer);
    }

    public ClanPlayer getClanPlayer(Player player) {
        return getClanPlayer(player.getUniqueId());
    }

    public ClanPlayer getClanPlayer(String name) {
        return clanPlayers.stream().filter(c -> c.getName().equalsIgnoreCase(name)).findAny().orElse(null);
    }

    public ClanPlayer getClanPlayer(UUID uuid) {
        return clanPlayers.stream().filter(c -> c.getUuid().equals(uuid)).findAny().orElse(null);
    }

    public OfflineClanPlayer getOfflineClanPlayer(String name) {
        return offlineClanPlayers.stream().filter(c -> c.getName().equalsIgnoreCase(name)).findAny().orElse(null);
    }

    public OfflineClanPlayer getOfflineClanPlayer(UUID uuid) {
        return offlineClanPlayers.stream().filter(c -> c.getUuid().equals(uuid)).findAny().orElse(null);
    }

    public List<ClanPlayer> getClanPlayers() {
        return clanPlayers;
    }

    public List<OfflineClanPlayer> getOfflineClanPlayers() {
        return offlineClanPlayers;
    }

    public List<OfflineClanPlayer> getAllPlayers() {
        return Stream.concat(clanPlayers.stream(), offlineClanPlayers.stream()).collect(Collectors.toList());
    }

    public void reload() {
        if (offlineClanPlayers != null) new ArrayList<>(offlineClanPlayers).forEach(this::removeOfflineClanPlayer);
        if (clanPlayers != null) new ArrayList<>(clanPlayers).forEach(this::removeClanPlayer);
    }
}