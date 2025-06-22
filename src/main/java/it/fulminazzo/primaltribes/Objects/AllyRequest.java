package it.fulminazzo.primaltribes.Objects;

import it.fulminazzo.primaltribes.PrimalTribes;
import org.bukkit.scheduler.BukkitTask;

public class AllyRequest {
    private final String clanName;
    private final BukkitTask mainTask;

    public AllyRequest(String clanName, BukkitTask mainTask) {
        this.clanName = clanName;
        this.mainTask = mainTask;
    }

    public Clan getClan() {
        return PrimalTribes.getPlugin().getClansManager().getClan(clanName);
    }

    public void cancelTask() {
        if (mainTask != null) mainTask.cancel();
    }
}