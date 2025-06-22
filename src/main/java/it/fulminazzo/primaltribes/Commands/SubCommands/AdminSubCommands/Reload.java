package it.fulminazzo.primaltribes.Commands.SubCommands.AdminSubCommands;

import it.angrybear.Enums.BearLoggingMessage;
import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.SubCommand;
import it.fulminazzo.primaltribes.Enums.CommandType;
import it.fulminazzo.primaltribes.Enums.Message;
import it.fulminazzo.primaltribes.Enums.Permissions;
import it.fulminazzo.primaltribes.PrimalTribes;
import it.fulminazzo.primaltribes.Utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class Reload extends SubCommand {
    private final PrimalTribes plugin;

    public Reload(PrimalTribes plugin, AbstractCommand command) {
        super(command, "reload", Permissions.RELOAD,
                StringUtils.getCommandSyntax(String.format("&c%s &areload", command.getName())),
                Message.HELP_RELOAD.getMessage(false, false), CommandType.GENERAL);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String[] args) {
        try {
            plugin.reloadAll();
            sender.sendMessage(Message.PLUGIN_RELOADED.getMessage(!isAdminCommand(), isAdminCommand()));
        } catch (Exception e) {
            PrimalTribes.logWarning(BearLoggingMessage.GENERAL_ERROR_OCCURRED.getMessage(
                    "%task%", "enabling plugin", "%error%", e.getMessage()));
            sender.sendMessage(Message.PLUGIN_UNLOADED.getMessage(!isAdminCommand(), isAdminCommand()));
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public int getMinArguments() {
        return 0;
    }
}