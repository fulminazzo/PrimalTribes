package it.fulminazzo.primaltribes.Commands.SubCommands.AllySubCommands;

import it.fulminazzo.primaltribes.Utils.StringUtils;
import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.ListClanSubCommand;
import it.fulminazzo.primaltribes.Enums.ClanPermission;
import it.fulminazzo.primaltribes.Enums.CommandType;
import it.fulminazzo.primaltribes.Enums.Message;
import it.fulminazzo.primaltribes.Enums.Permissions;
import it.fulminazzo.primaltribes.Objects.Clan;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AllyUnInvite extends ListClanSubCommand {

    public AllyUnInvite(AbstractCommand command, String commandName) {
        super(command, "uninvite", Permissions.ALLY_UNINVITE, ClanPermission.ALLY_UNINVITE,
                StringUtils.getCommandSyntax(String.format("&c%s &auninvite", commandName), "clan"),
                Message.HELP_ALLY_UNINVITE.getMessage(false, false),
                !command.isAdminCommand(), CommandType.CLAN_ONLY);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String[] args) {
        checkClan(sender);
        execute(sender, args, Message.NO_CLAN_TO_UNINVITE_ALLY,
                Message.NO_CLAN_SPECIFIED,
                getInvitedClans(clan),
                clanName -> {
                    Clan c = getClansManager().getClan(clanName);
                    if (c == null)
                        sender.sendMessage(Message.CLAN_NOT_FOUND.getMessage(!isAdminCommand(), isAdminCommand())
                                .replace("%clan%", clanName));
                    else if (clan.equals(c))
                        sender.sendMessage(Message.CANNOT_UNINVITE_ALLY_SELF.getMessage(!isAdminCommand(), isAdminCommand()));
                    else if (!c.hasBeenAskedForAllyFrom(clan))
                        sender.sendMessage(Message.NOT_ASKED_ALLY.getMessage(!isAdminCommand(), isAdminCommand())
                                .replace("%clan%", c.getDisplayName()));
                    else {
                        clan.unAskAlly(sender, c);
                        sender.sendMessage(Message.CLAN_UN_ASKED_ALLY.getMessage(!isAdminCommand(), isAdminCommand())
                                .replace("%clan%", c.getDisplayName()));
                    }
                });
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String[] args) {
        List<String> list = new ArrayList<>();
        checkClan(sender);
        if (args.length == 1) list.addAll(getInvitedClans(clan));
        return list;
    }

    @Override
    public int getMinArguments() {
        return 0;
    }

    private List<String> getInvitedClans(Clan clan) {
        return getClansManager().getClans().stream()
                .filter(c -> !c.equals(clan))
                .filter(c -> c.hasBeenAskedForAllyFrom(clan))
                .map(Clan::getName)
                .collect(Collectors.toList());
    }
}