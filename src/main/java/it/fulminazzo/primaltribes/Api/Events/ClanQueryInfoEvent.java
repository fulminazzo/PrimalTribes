package it.fulminazzo.primaltribes.Api.Events;

import it.fulminazzo.primaltribes.Objects.Clan;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ClanQueryInfoEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Clan clan;
    private String info;

    public ClanQueryInfoEvent(Clan clan, String info) {
        this.clan = clan;
        this.info = info;
    }

    public Clan getClan() {
        return clan;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}