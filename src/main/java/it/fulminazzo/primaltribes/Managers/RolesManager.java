package it.fulminazzo.primaltribes.Managers;

import it.angrybear.Utils.ConfigUtils;
import it.angrybear.Utils.FileUtils;
import it.fulminazzo.primaltribes.Enums.ClanPermission;
import it.fulminazzo.primaltribes.Objects.Role;
import it.fulminazzo.primaltribes.PrimalTribes;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class RolesManager {
    private final HashMap<Integer, Role> roles;
    private final Role defaultRole;
    private final Role leaderRole;

    public RolesManager(PrimalTribes plugin) throws IOException {
        File rolesFile = new File(plugin.getDataFolder(), "roles.yml");
        if (!rolesFile.exists()) plugin.saveResource("roles.yml", true);
        FileConfiguration rolesConfiguration = YamlConfiguration.loadConfiguration(rolesFile);
        ConfigurationSection rolesSection = rolesConfiguration.getConfigurationSection("roles");
        List<Role> tempRoles;
        if (rolesSection == null) tempRoles = new ArrayList<>();
        else tempRoles = rolesSection.getKeys(false)
                .stream()
                .map(key -> new Role(key, rolesSection.getConfigurationSection(key)))
                .collect(Collectors.toList());
        Role defaultRole = tempRoles.stream().filter(Role::isDefaultRole).findFirst().orElse(null);
        Role leaderRole = tempRoles.stream().filter(Role::isLeaderRole).findFirst().orElse(null);

        HashMap<Integer, Role> roles = new HashMap<>();
        tempRoles.forEach(r -> {
            Role role = roles.getOrDefault(r.getPriority(), null);
            if (role != null) {
                if (role.isLeaderRole() && !r.isLeaderRole()) return;
                if (role.isDefaultRole() && !r.isDefaultRole()) return;
            }
            roles.put(r.getPriority(), r);
        });
        tempRoles = roles.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
        if (defaultRole == null && !tempRoles.isEmpty())
            defaultRole = tempRoles.get(0);
        if (leaderRole == null && !tempRoles.isEmpty())
            leaderRole = tempRoles.get(tempRoles.size() - 1);
        this.defaultRole = defaultRole;
        this.leaderRole = leaderRole;
        if (defaultRole != null) tempRoles = tempRoles.subList(Math.max(tempRoles.indexOf(defaultRole), 0), tempRoles.size());
        if (leaderRole != null) tempRoles = tempRoles.subList(0, Math.min(Math.max(tempRoles.indexOf(leaderRole), 0) + 1, tempRoles.size()));
        tempRoles.forEach(r -> {
            if (!r.equals(this.defaultRole)) r.setDefaultRole(false);
            if (!r.equals(this.leaderRole)) r.setLeaderRole(false);
        });
        this.roles = new HashMap<>();
        tempRoles.forEach(r -> this.roles.put(r.getPriority(), r));

        File backupFile = new File(plugin.getDataFolder(), "roles.yml.bk");
        if (backupFile.exists())
            FileUtils.deleteFolder(backupFile);
        FileUtils.renameFile(rolesFile, backupFile);

        if (rolesSection != null) {
            rolesSection.getKeys(false).forEach(k -> rolesSection.set(k, null));
            for (Role r : this.roles.values()) {
                ConfigurationSection roleSection = rolesSection.createSection(r.getName());
                roleSection.set("display-name", r.getDisplayName());
                roleSection.set("prefix", r.getPrefix());
                roleSection.set("priority", r.getPriority());
                roleSection.set("leader", r.isLeaderRole());
                roleSection.set("default", r.isDefaultRole());
                roleSection.set("permissions", r.getPermissions().stream()
                        .map(ClanPermission::name)
                        .collect(Collectors.toList()));
            }
            ConfigUtils.saveConfig(rolesConfiguration, rolesFile);
        }

        if (FileUtils.compareTwoFiles(rolesFile, backupFile)) FileUtils.deleteFile(backupFile);
    }

    public Role getNextRole(Role role) {
        return roles.getOrDefault(role.getPriority() + 1, null);
    }

    public Role getPreviousRole(Role role) {
        return roles.getOrDefault(role.getPriority() - 1, null);
    }

    public Role getRole(String name) {
        if (name == null) return defaultRole;
        return roles.values().stream().filter(r -> r.getName().equalsIgnoreCase(name)).findAny().orElse(null);
    }

    public Role getLeaderRole() {
        return leaderRole;
    }

    public Role getDefaultRole() {
        return defaultRole;
    }

    public List<Role> getRoles() {
        return new ArrayList<>(roles.values());
    }
}