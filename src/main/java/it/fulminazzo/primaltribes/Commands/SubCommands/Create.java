package it.fulminazzo.primaltribes.Commands.SubCommands;

import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.SubCommand;
import it.fulminazzo.primaltribes.Enums.CommandType;
import it.fulminazzo.primaltribes.Enums.ConfigOption;
import it.fulminazzo.primaltribes.Enums.Message;
import it.fulminazzo.primaltribes.Enums.Permissions;
import it.fulminazzo.primaltribes.Objects.Clan;
import it.fulminazzo.primaltribes.PrimalTribes;
import it.fulminazzo.primaltribes.Utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Create extends SubCommand {
    private final PrimalTribes plugin;

    public Create(PrimalTribes plugin, AbstractCommand command) {
        super(command, "create", Permissions.CREATE,
                StringUtils.getCommandSyntax(String.format("&c%s &acreate", command.getName()), "&9name"),
                Message.HELP_CREATE.getMessage(false, false),
                !command.isAdminCommand(), CommandType.NO_CLAN_ONLY);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String[] args) {
        String clanName = args[0];
        Clan clan = plugin.getClansManager().getClan(clanName);
        Player player = (Player) sender;
        if (clan != null) {
            player.sendMessage(Message.CLAN_ALREADY_EXISTS.getMessage(!isAdminCommand(), isAdminCommand())
                    .replace("%clan%", clan.getName()));
            return;
        }
        Optional<String> bannedWord = ConfigOption.BANNED_WORDS.getStringList().stream()
                .map(String::toLowerCase)
                .filter(w -> w.contains(clanName.toLowerCase()) || clanName.toLowerCase().contains(w))
                .findAny();
        if (bannedWord.isPresent()) {
            player.sendMessage(Message.BANNED_WORD.getMessage(!isAdminCommand(), isAdminCommand())
                    .replace("%word%", bannedWord.get()));
            return;
        }
        if (!clanName.matches("[a-zA-Z0-9]+")) {
            player.sendMessage(Message.CHARACTERS_NOT_ALLOWED.getMessage(!isAdminCommand(), isAdminCommand()));
            return;
        }
        if (plugin.getClansManager().createClan(clanName, player, isAdminCommand())) {
            player.sendMessage(Message.CLAN_CREATED.getMessage(!isAdminCommand(), isAdminCommand())
                    .replace("%clan%", clanName));
            String broadcastMessage = Message.NOTIFY_CLAN_CREATED.getMessage(false, false)
                    .replace("%player%", player.getDisplayName())
                    .replace("%clan-name%", clanName);
            if (!broadcastMessage.equalsIgnoreCase(""))
                Bukkit.broadcastMessage(broadcastMessage);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) list.add("<name>");
        return list;
    }

    @Override
    public int getMinArguments() {
        return 1;
    }
}