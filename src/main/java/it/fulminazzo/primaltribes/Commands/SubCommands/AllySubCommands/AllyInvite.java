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

public class AllyInvite extends ListClanSubCommand {

    public AllyInvite(AbstractCommand command, String commandName) {
        super(command, "invite", Permissions.ALLY_INVITE, ClanPermission.ALLY_INVITE,
                StringUtils.getCommandSyntax(String.format("&c%s &ainvite", commandName), "clan"),
                Message.HELP_ALLY_INVITE.getMessage(false, false),
                !command.isAdminCommand(), CommandType.CLAN_ONLY);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String[] args) {
        checkClan(sender);
        execute(sender, args, Message.NO_CLAN_TO_ALLY,
                Message.NO_CLAN_SPECIFIED,
                getClans(clan),
                clanName -> {
                    Clan c = getClansManager().getClan(clanName);
                    if (c == null)
                        sender.sendMessage(Message.CLAN_NOT_FOUND.getMessage(!isAdminCommand(), isAdminCommand())
                                .replace("%clan%", clanName));
                    else if (clan.equals(c))
                        sender.sendMessage(Message.CANNOT_ALLY_SELF.getMessage(!isAdminCommand(), isAdminCommand()));
                    else if (clan.isAlly(c))
                        sender.sendMessage(Message.CLAN_ALREADY_ALLIED.getMessage(!isAdminCommand(), isAdminCommand())
                                .replace("%clan%", c.getDisplayName()));
                    else if (c.hasBeenAskedForAllyFrom(clan))
                        sender.sendMessage(Message.CLAN_ALREADY_ASKED_ALLY.getMessage(!isAdminCommand(), isAdminCommand())
                                .replace("%clan%", c.getDisplayName()));
                    else if (clan.wasRecentlyInvited(c))
                        sender.sendMessage(Message.CLAN_RECENTLY_INVITED.getMessage(!isAdminCommand(), isAdminCommand())
                                .replace("%clan%", c.getDisplayName()));
                    else if (clan.hasBeenAskedForAllyFrom(c))
                        sender.sendMessage(Message.CLAN_ASKED_ALLY_FROM.getMessage(!isAdminCommand(), isAdminCommand())
                                .replace("%clan%", c.getDisplayName()));
                    else {
                        clan.askAlly(sender, c);
                        sender.sendMessage(Message.CLAN_ASKED_ALLY.getMessage(!isAdminCommand(), isAdminCommand())
                                .replace("%clan%", c.getDisplayName()));
                    }
                });
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String[] args) {
        List<String> list = new ArrayList<>();
        checkClan(sender);
        if (args.length == 1) list.addAll(getClans(clan));
        return list;
    }

    @Override
    public int getMinArguments() {
        return 0;
    }

    private List<String> getClans(Clan clan) {
        return getClansManager().getClans().stream()
                .filter(c -> !c.equals(clan))
                .filter(c -> !clan.isAlly(c))
                .filter(c -> !clan.hasBeenAskedForAllyFrom(c))
                .map(Clan::getName)
                .collect(Collectors.toList());
    }
}