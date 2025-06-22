package it.fulminazzo.primaltribes.Api.Events;

import it.fulminazzo.primaltribes.Objects.Clan;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class ClanEvent extends Event implements Cancellable {
    protected static final HandlerList handlers = new HandlerList();
    private final Clan clan;

    public ClanEvent(Clan clan) {
        this.clan = clan;
    }

    public Clan getClan() {
        return clan;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}