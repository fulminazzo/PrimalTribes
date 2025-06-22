package it.fulminazzo.primaltribes.Api.Events;

import it.fulminazzo.primaltribes.Objects.Clan;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RemoveClanEvent extends ClanEvent {
    private boolean cancelled;

    public RemoveClanEvent(Clan clan) {
        super(clan);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
