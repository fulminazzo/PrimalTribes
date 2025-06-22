package it.fulminazzo.primaltribes.Api.Events;

import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class SubCommandEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final AbstractCommand cmd;
    private final CommandSender sender;
    private boolean cancelled;
    private SubCommand subCommand;
    private String[] args;

    public SubCommandEvent(SubCommand subCommand, CommandSender sender,
                           AbstractCommand cmd, String[] args) {
        this.cancelled = false;
        this.subCommand = subCommand;
        this.sender = sender;
        this.cmd = cmd;
        this.args = args;
    }

    public SubCommand getSubCommand() {
        return subCommand;
    }

    public void setSubCommand(SubCommand subCommand) {
        this.subCommand = subCommand;
    }

    public CommandSender getSender() {
        return sender;
    }

    public AbstractCommand getCmd() {
        return cmd;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
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