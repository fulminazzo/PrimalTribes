package it.fulminazzo.primaltribes.Commands.SubCommands;

import it.angrybear.Utils.TextComponentUtils;
import it.fulminazzo.primaltribes.Utils.StringUtils;
import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.ListClanSubCommand;
import it.fulminazzo.primaltribes.Enums.*;
import it.fulminazzo.primaltribes.Objects.Clan;
import it.fulminazzo.primaltribes.Objects.Users.ClanPlayer;
import it.fulminazzo.primaltribes.PrimalTribes;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Invite extends ListClanSubCommand {

    public Invite(AbstractCommand command) {
        super(command, "invite", Permissions.INVITE, ClanPermission.INVITE,
                StringUtils.getCommandSyntax(String.format("&c%s &ainvite", command.getName()), "player"),
                Message.HELP_INVITE.getMessage(false, false),
                !command.isAdminCommand(), CommandType.CLAN_ONLY);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String[] args) {
        checkClan(sender);
        execute(sender, cmd, args, Message.NO_PLAYERS_TO_INVITE,
                Message.NO_PLAYER_SPECIFIED,
                getOnlineNotInClanPlayers(clan),
                p -> {
                    if (sender instanceof Player) ((Player) sender).closeInventory();
                    ClanPlayer cP = getClanPlayersManager().getClanPlayer(p);
                    if (!clan.areInvitesEnabled())
                        sender.sendMessage(Message.INVITES_DISABLED.getMessage(!isAdminCommand(), isAdminCommand()));
                    else if (clan.isPlayerInvited(p))
                        sender.sendMessage(Message.PLAYER_ALREADY_INVITED.getMessage(!isAdminCommand(), isAdminCommand())
                                .replace("%player%", p.getName()));
                    else if (cP.isInClan())
                        sender.sendMessage(Message.PLAYER_ALREADY_IN_CLAN.getMessage(!isAdminCommand(), isAdminCommand())
                                .replace("%player%", p.getName()));
                    else if (clan.isPlayerBanned(p))
                        sender.sendMessage(Message.ERROR_PLAYER_BANNED.getMessage(!isAdminCommand(), isAdminCommand())
                                .replace("%player%", p.getName()));
                    else if (clan.getMembers().size() >= ConfigOption.MEMBERS_PER_CLAN.getInt())
                        sender.sendMessage(Message.CLAN_MAXIMUM_MEMBERS_REACHED.getMessage(!isAdminCommand(), isAdminCommand()));
                    else if (clan.wasRecentlyInvited(p))
                        sender.sendMessage(Message.PLAYER_RECENTLY_INVITED.getMessage(!isAdminCommand(), isAdminCommand())
                                .replace("%player%", p.getName()));
                    else {
                        clan.invitePlayer(sender, p);
                        sender.sendMessage(Message.PLAYER_INVITED.getMessage(!isAdminCommand(), isAdminCommand())
                                .replace("%player%", p.getName()));
                        p.sendMessage(Message.INVITED_BY.getMessage(false, false)
                                .replace("%clan%", clan.getDisplayName())
                                .replace("%seconds%", String.valueOf(ConfigOption.INVITES_TIMEOUT.getInt()))
                                .replace("%player%", sender.getName()));
                        TextComponent textComponent = new TextComponent(Message.CLICK_TO_ACCEPT.getMessage(false, false));
                        ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                String.format("/%s join %s", PrimalTribes.getCommandName(), clan.getName()));
                        textComponent.setClickEvent(clickEvent);
                        textComponent.setHoverEvent(TextComponentUtils.getTextHoverEvent(
                                Message.CLICK_HERE_TO_ACCEPT.getMessage(false, false)));
                        p.spigot().sendMessage(textComponent);
                    }
                });
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String[] args) {
        List<String> list = new ArrayList<>();
        checkClan(sender);
        if (args.length == 1)
            list.addAll(getOnlineNotInClanPlayers(clan).stream()
                    .map(HumanEntity::getName)
                    .collect(Collectors.toList()));
        return list;
    }

    @Override
    public int getMinArguments() {
        return 0;
    }

    private List<Player> getOnlineNotInClanPlayers(Clan clan) {
        return Bukkit.getOnlinePlayers().stream()
                .filter(p -> !clan.equals(getClanPlayersManager().getClanPlayer(p).getClan()))
                .filter(p -> !clan.isPlayerBanned(p))
                .filter(p -> !getClanPlayersManager().getClanPlayer(p).isInvitedByClan(clan))
                .sorted(Comparator.comparing(HumanEntity::getName))
                .collect(Collectors.toList());
    }
}
