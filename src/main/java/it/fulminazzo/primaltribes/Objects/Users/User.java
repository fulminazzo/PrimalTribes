package it.fulminazzo.primaltribes.Objects.Users;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.UUID;

public class User {
    private final UUID uuid;
    private String name;

    public User(ConfigurationSection userSection) {
        this.uuid = UUID.fromString(userSection.getName());
        this.name = userSection.getString("name");
    }

    public User(String uuid, String name) {
        this.uuid = UUID.fromString(uuid);
        this.name = name;
    }

    public User(Player player) {
        this.uuid = player.getUniqueId();
        this.name = player.getName();
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
}