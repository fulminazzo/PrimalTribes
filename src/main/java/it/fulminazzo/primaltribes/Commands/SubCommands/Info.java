package it.fulminazzo.primaltribes.Commands.SubCommands;

import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.SubCommand;
import it.fulminazzo.primaltribes.Enums.CommandType;
import it.fulminazzo.primaltribes.Enums.Message;
import it.fulminazzo.primaltribes.Enums.Permissions;
import it.fulminazzo.primaltribes.Objects.Clan;
import it.fulminazzo.primaltribes.Objects.Users.ClanPlayer;
import it.fulminazzo.primaltribes.Utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Info extends SubCommand {

    public Info(AbstractCommand command) {
        super(command, "info", Permissions.INFO,
                StringUtils.getCommandSyntax(String.format("&c%s &ainfo", command.getName()), "clan"),
                Message.HELP_INFO.getMessage(false, false),
                CommandType.GENERAL);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String[] args) {
        Clan clan = null;
        if (args.length == 0) {
            if (sender instanceof Player) {
                ClanPlayer clanPlayer = getClanPlayersManager().getClanPlayer((Player) sender);
                clan = clanPlayer.getClan();
            }
            if (clan == null) {
                sender.sendMessage(Message.NO_CLAN_SPECIFIED.getMessage(!isAdminCommand(), isAdminCommand()));
                return;
            }
        } else {
            String argument = args[0];
            clan = getClansManager().getClan(argument);
            if (clan == null) {
                sender.sendMessage(Message.CLAN_NOT_FOUND.getMessage(!isAdminCommand(), isAdminCommand())
                        .replace("%clan%", argument));
                return;
            }
        }
        sender.sendMessage(clan.getInfo());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1)
            list.addAll(getClansManager().getClans().stream().map(Clan::getName).collect(Collectors.toList()));
        return list;
    }

    @Override
    public int getMinArguments() {
        return 0;
    }
}
