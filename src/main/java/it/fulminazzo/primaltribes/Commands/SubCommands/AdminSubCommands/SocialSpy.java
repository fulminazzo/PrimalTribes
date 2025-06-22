package it.fulminazzo.primaltribes.Commands.SubCommands.AdminSubCommands;

import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.SubCommand;
import it.fulminazzo.primaltribes.Enums.CommandType;
import it.fulminazzo.primaltribes.Enums.Message;
import it.fulminazzo.primaltribes.Enums.Permissions;
import it.fulminazzo.primaltribes.Objects.Users.ClanPlayer;
import it.fulminazzo.primaltribes.Utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SocialSpy extends SubCommand {

    public SocialSpy(AbstractCommand command) {
        super(command, "socialspy", Permissions.SOCIAL_SPY,
                StringUtils.getCommandSyntax(String.format("&c%s &asocialspy", command.getName())),
                Message.HELP_SOCIAL_SPY.getMessage(false, false), true, CommandType.GENERAL);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String[] args) {
        Player player = (Player) sender;
        ClanPlayer clanPlayer = getClanPlayersManager().getClanPlayer(player);

        Message status;
        if (clanPlayer.isSocialSpyOn()) {
            clanPlayer.disableSocialSpy();
            status = Message.DISABLED_FEMALE_SINGULAR;
        } else {
            clanPlayer.enableSocialSpy();
            status = Message.ENABLED_FEMALE_SINGULAR;
        }
        sender.sendMessage(Message.SOCIAL_SPY.getMessage(!isAdminCommand(), isAdminCommand())
                .replace("%status%", status.getMessage(false, false)));
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