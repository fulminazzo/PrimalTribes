package it.fulminazzo.primaltribes.Objects;

import it.fulminazzo.primaltribes.Enums.ClanPermission;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Role {
    private final String name;
    private final String displayName;
    private final String prefix;
    private final int priority;
    private boolean defaultRole;
    private boolean leaderRole;
    private final List<ClanPermission> clanPermissions;

    public Role(String name, ConfigurationSection roleSection) {
        this.name = name;
        this.displayName = roleSection.getString("display-name");
        this.prefix = roleSection.getString("prefix");
        this.priority = roleSection.getInt("priority");
        this.defaultRole = roleSection.getBoolean("default");
        this.leaderRole = roleSection.getBoolean("leader");
        this.clanPermissions = roleSection.getStringList("permissions")
                .stream()
                .filter(s -> Arrays.stream(ClanPermission.values()).anyMatch(p -> p.name().equalsIgnoreCase(s)))
                .map(s -> ClanPermission.valueOf(s.toUpperCase()))
                .collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return ChatColor.translateAlternateColorCodes('&', displayName);
    }

    public String getPrefix() {
        return ChatColor.translateAlternateColorCodes('&', prefix);
    }

    public int getPriority() {
        return priority;
    }

    public void setDefaultRole(boolean defaultRole) {
        this.defaultRole = defaultRole;
    }

    public boolean isDefaultRole() {
        return defaultRole;
    }

    public void setLeaderRole(boolean leaderRole) {
        this.leaderRole = leaderRole;
    }

    public boolean isLeaderRole() {
        return leaderRole;
    }

    public boolean hasPermission(ClanPermission clanPermission) {
        return clanPermissions.contains(clanPermission);
    }

    public List<ClanPermission> getPermissions() {
        return clanPermissions;
    }
}