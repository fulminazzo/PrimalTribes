package it.fulminazzo.primaltribes.Objects.Users;

import it.angrybear.Utils.ConfigUtils;
import it.angrybear.Utils.FileUtils;
import it.fulminazzo.primaltribes.Enums.ClanPermission;
import it.fulminazzo.primaltribes.Objects.Clan;
import it.fulminazzo.primaltribes.PrimalTribes;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class OfflineClanPlayer {
    private final File playerFile;
    private final FileConfiguration playerConfig;
    private final UUID uuid;
    private String name;
    private Clan clan;
    private boolean clanChatMuted;

    public OfflineClanPlayer(PrimalTribes plugin, File playerFile) throws IOException {
        this.playerFile = playerFile;
        if (!playerFile.exists()) {
            FileUtils.createNewFile(playerFile);
            this.playerConfig = YamlConfiguration.loadConfiguration(playerFile);
            this.uuid = UUID.fromString(playerFile.getName().substring(0, playerFile.getName().length() - ".yml".length()));
            this.clan = null;
            this.clanChatMuted = false;
            save();
        } else {
            this.playerConfig = YamlConfiguration.loadConfiguration(playerFile);
            this.uuid = UUID.fromString(playerFile.getName().substring(0, playerFile.getName().length() - ".yml".length()));
            this.name = playerConfig.getString("name");
            this.clan = plugin.getClansManager().getClans().stream()
                    .filter(c -> c.getMember(uuid) != null)
                    .findAny().orElse(null);
            this.clanChatMuted = playerConfig.getBoolean("clan-chat-muted");
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean hasPermission(ClanPermission clanPermission) {
        Member member = getMember();
        return member != null && (member.getRole().hasPermission(clanPermission) || member.getRole().hasPermission(ClanPermission.ALL));
    }

    public Member getMember() {
        if (clan == null) return null;
        return clan.getMembers().stream().filter(m -> m.getUuid().equals(getUuid())).findAny().orElse(null);
    }

    public void addPlayerToClan(Clan c) {
        this.clan = c;
    }

    public void removePlayerFromClan() {
        this.clan = null;
    }

    public Clan getClan() {
        return clan;
    }

    public boolean isInClan() {
        return clan != null;
    }

    public void muteClanChat() {
        this.clanChatMuted = true;
    }

    public void unMuteClanChat() {
        this.clanChatMuted = false;
    }

    public boolean isClanChatMuted() {
        return clanChatMuted;
    }

    public void save() throws IOException {
        playerConfig.set("uuid", uuid.toString());
        playerConfig.set("name", name);
        playerConfig.set("clan", clan == null ? "" : clan.getName());
        playerConfig.set("clan-chat-muted", clanChatMuted);
        ConfigUtils.saveConfig(playerConfig, playerFile);
    }
}