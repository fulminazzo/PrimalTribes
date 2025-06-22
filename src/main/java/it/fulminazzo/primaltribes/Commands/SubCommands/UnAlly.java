package it.fulminazzo.primaltribes.Commands.SubCommands;

import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.ConfirmClanSubCommand;
import it.fulminazzo.primaltribes.Enums.ClanPermission;
import it.fulminazzo.primaltribes.Enums.CommandType;
import it.fulminazzo.primaltribes.Enums.Message;
import it.fulminazzo.primaltribes.Enums.Permissions;
import it.fulminazzo.primaltribes.Objects.Clan;
import it.fulminazzo.primaltribes.Utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UnAlly extends ConfirmClanSubCommand {

    public UnAlly(AbstractCommand command) {
        super(command, "unally", Permissions.UNALLY, ClanPermission.UNALLY,
                StringUtils.getCommandSyntax(String.format("&c%s &aunally", command.getName()), "clan"),
                Message.HELP_UNALLY.getMessage(false, false),
                !command.isAdminCommand(), CommandType.CLAN_ONLY);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String[] args) {
        checkClan(sender);
        execute(sender, args, Message.NO_CLAN_TO_UNALLY,
                Message.NO_CLAN_SPECIFIED,
                getAllies(clan),
                clanName -> {
                    Clan c = getClansManager().getClan(clanName);
                    if (c == null)
                        sender.sendMessage(Message.CLAN_NOT_FOUND.getMessage(!isAdminCommand(), isAdminCommand())
                                .replace("%clan%", clanName));
                    else if (clan.equals(c))
                        sender.sendMessage(Message.CANNOT_UNALLY_SELF.getMessage(!isAdminCommand(), isAdminCommand()));
                    else if (!clan.isAlly(c))
                        sender.sendMessage(Message.CLAN_NOT_ALLIED.getMessage(!isAdminCommand(), isAdminCommand())
                                .replace("%clan%", c.getDisplayName()));
                    else if (clan.hasBeenAskedForAllyFrom(c))
                        sender.sendMessage(Message.CLAN_ASKED_ALLY_FROM.getMessage(!isAdminCommand(), isAdminCommand())
                                .replace("%clan%", c.getDisplayName()));
                    else execute(sender,
                                () -> {
                        clan.unAlly(sender, c);
                        sender.sendMessage(Message.CLAN_UNALLIED.getMessage(!isAdminCommand(), isAdminCommand())
                                .replace("%clan%", c.getDisplayName()));
                    });
                });
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String[] args) {
        List<String> list = new ArrayList<>();
        checkClan(sender);
        if (args.length == 1) list.addAll(getAllies(clan));
        return list;
    }

    @Override
    public int getMinArguments() {
        return 0;
    }

    private List<String> getAllies(Clan clan) {
        return clan.getAllies().stream().map(Clan::getName).collect(Collectors.toList());
    }
}