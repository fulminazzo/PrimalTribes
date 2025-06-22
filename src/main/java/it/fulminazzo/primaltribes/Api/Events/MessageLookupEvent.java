package it.fulminazzo.primaltribes.Api.Events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MessageLookupEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final String path;
    private boolean normalPrefix;
    private boolean adminPrefix;
    private String message;

    public MessageLookupEvent(String path, boolean normalPrefix,
                              boolean adminPrefix, String message) {
        this.path = path;
        this.normalPrefix = normalPrefix;
        this.adminPrefix = adminPrefix;
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public boolean isNormalPrefix() {
        return normalPrefix;
    }

    public void setNormalPrefix(boolean normalPrefix) {
        this.normalPrefix = normalPrefix;
    }

    public boolean isAdminPrefix() {
        return adminPrefix;
    }

    public void setAdminPrefix(boolean adminPrefix) {
        this.adminPrefix = adminPrefix;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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