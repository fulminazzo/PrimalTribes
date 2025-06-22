package it.fulminazzo.primaltribes.Commands.SubCommands.AdminSubCommands;

import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.ListClanSubCommand;
import it.fulminazzo.primaltribes.Enums.ClanPermission;
import it.fulminazzo.primaltribes.Enums.CommandType;
import it.fulminazzo.primaltribes.Enums.Message;
import it.fulminazzo.primaltribes.Enums.Permissions;
import it.fulminazzo.primaltribes.Objects.Users.Member;
import it.fulminazzo.primaltribes.Objects.Users.OfflineClanPlayer;
import it.fulminazzo.primaltribes.Utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SilenceChatAdmin extends ListClanSubCommand {

    public SilenceChatAdmin(AbstractCommand command) {
        super(command, "silencechat", Permissions.SILENCE_CHAT, ClanPermission.SILENCE_CHAT,
                StringUtils.getCommandSyntax(String.format("&c%s &asilencechat", command.getName())),
                Message.HELP_SILENCE_CHAT.getMessage(false, false),
                false, CommandType.CLAN_ONLY);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String[] args) {
        checkClan(sender);
        execute(sender, args, Message.NO_MEMBERS, Message.NO_PLAYER_SPECIFIED,
                clan.getMembers().stream().map(Member::getName).collect(Collectors.toList()), pName -> {
            if (sender instanceof Player) ((Player) sender).closeInventory();
            Member member = clan.getMember(pName);
            if (member == null) {
                sender.sendMessage(Message.MEMBER_NOT_FOUND.getMessage(!isAdminCommand(), isAdminCommand())
                        .replace("%member%", pName));
                return;
            }
            OfflineClanPlayer clanPlayer = getClanPlayersManager().getOfflineClanPlayer(pName);
            if (clanPlayer == null) clanPlayer = getClanPlayersManager().getClanPlayer(pName);

            Message status;
            if (clanPlayer.isClanChatMuted()) {
                clanPlayer.unMuteClanChat();
                status = Message.ENABLED_FEMALE_SINGULAR;
            } else {
                clanPlayer.muteClanChat();
                status = Message.DISABLED_FEMALE_SINGULAR;
            }

            sender.sendMessage(Message.PLAYER_CLAN_CHAT_SILENCED.getMessage(!isAdminCommand(), isAdminCommand())
                    .replace("%player%", clanPlayer.getName())
                    .replace("%status%", status.getMessage(false, false)));
        });
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String[] args) {
        List<String> list = new ArrayList<>();
        checkClan(sender);
        if (args.length == 1)
            list.addAll(clan.getMembers().stream().map(Member::getName).collect(Collectors.toList()));
        return list;
    }

    @Override
    public int getMinArguments() {
        return 0;
    }
}