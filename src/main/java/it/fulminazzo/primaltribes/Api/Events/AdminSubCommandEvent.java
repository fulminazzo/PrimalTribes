package it.fulminazzo.primaltribes.Api.Events;

import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.SubCommand;
import org.bukkit.command.CommandSender;

public class AdminSubCommandEvent extends SubCommandEvent {
    public AdminSubCommandEvent(SubCommand subCommand, CommandSender sender, AbstractCommand cmd, String[] args) {
        super(subCommand, sender, cmd, args);
    }
}