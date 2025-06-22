package it.fulminazzo.primaltribes.Objects.Users;

import it.fulminazzo.primaltribes.Managers.RolesManager;
import it.fulminazzo.primaltribes.Objects.Role;
import it.fulminazzo.primaltribes.PrimalTribes;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class Member extends User {
    private Role role;

    public Member(ConfigurationSection memberSection) {
        super(memberSection);
        RolesManager rolesManager = PrimalTribes.getPlugin().getRolesManager();
        Role role = PrimalTribes.getPlugin().getRolesManager().getRole(memberSection.getString("rank"));
        if (role == null) role = rolesManager.getDefaultRole();
        this.role = role;
    }

    public Member(String uuid, String name, String roleName) {
        super(uuid, name);
        RolesManager rolesManager = PrimalTribes.getPlugin().getRolesManager();
        Role role = rolesManager.getRole(roleName);
        if (role == null) role = rolesManager.getDefaultRole();
        this.role = role;
    }

    public Member(Player player, String roleName) {
        super(player);
        RolesManager rolesManager = PrimalTribes.getPlugin().getRolesManager();
        Role role = rolesManager.getRole(roleName);
        if (role == null) role = rolesManager.getDefaultRole();
        this.role = role;
    }

    public Member(Player player, Role role) {
        super(player);
        RolesManager rolesManager = PrimalTribes.getPlugin().getRolesManager();
        if (role == null) role = rolesManager.getDefaultRole();
        this.role = role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Role getRole() {
        return role;
    }
}
