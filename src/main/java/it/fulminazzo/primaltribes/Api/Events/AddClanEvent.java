package it.fulminazzo.primaltribes.Api.Events;

import it.fulminazzo.primaltribes.Objects.Clan;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AddClanEvent extends ClanEvent {
    private boolean cancelled;
    private final Player player;
    private final boolean isAdminCommand;

    public AddClanEvent(Clan clan, Player player, boolean isAdminCommand) {
        super(clan);
        this.player = player;
        this.isAdminCommand = isAdminCommand;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isAdminCommand() {
        return isAdminCommand;
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
