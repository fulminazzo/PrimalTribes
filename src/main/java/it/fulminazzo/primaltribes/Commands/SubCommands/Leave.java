package it.fulminazzo.primaltribes.Commands.SubCommands;

import it.fulminazzo.primaltribes.Utils.StringUtils;
import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.ConfirmClanSubCommand;
import it.fulminazzo.primaltribes.Enums.ClanPermission;
import it.fulminazzo.primaltribes.Enums.CommandType;
import it.fulminazzo.primaltribes.Enums.Message;
import it.fulminazzo.primaltribes.Enums.Permissions;
import it.fulminazzo.primaltribes.Objects.Users.ClanPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Leave extends ConfirmClanSubCommand {

    public Leave(AbstractCommand command) {
        super(command, "leave", Permissions.LEAVE, ClanPermission.LEAVE,
                StringUtils.getCommandSyntax(String.format("&c%s &aleave", command.getName())),
                Message.HELP_LEAVE.getMessage(false, false),
                !command.isAdminCommand(), CommandType.CLAN_ONLY);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String[] args) {
        checkClan(sender);
        Player player = (Player) sender;
        ClanPlayer clanPlayer = getClanPlayersManager().getClanPlayer(player);
        if (clanPlayer.getMember().getRole().isLeaderRole()) {
            sender.sendMessage(Message.CANNOT_LEAVE.getMessage(!isAdminCommand(), isAdminCommand()));
            return;
        }
        execute(sender,
                () -> {
                    clanPlayer.removePlayerFromClan();
                    clan.leaveMember(player);
                    sender.sendMessage(Message.CLAN_LEFT.getMessage(!isAdminCommand(), isAdminCommand())
                            .replace("%clan%", clan.getDisplayName()));
                });
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