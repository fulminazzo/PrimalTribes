package it.fulminazzo.primaltribes.Objects.Users;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class Invite extends User {
    private final BukkitTask mainTask;

    public Invite(Player player, BukkitTask mainTask) {
        super(player);
        this.mainTask = mainTask;
    }

    public void cancelTask() {
        if (mainTask != null) mainTask.cancel();
    }
}