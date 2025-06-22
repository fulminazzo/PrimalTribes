package it.fulminazzo.primaltribes.Commands.SubCommands;

import it.fulminazzo.primaltribes.Utils.StringUtils;
import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.ClanSubCommand;
import it.fulminazzo.primaltribes.Enums.*;
import it.fulminazzo.primaltribes.Objects.Clan;
import it.fulminazzo.primaltribes.PrimalTribes;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class SetTag extends ClanSubCommand {
    private final PrimalTribes plugin;

    public SetTag(PrimalTribes plugin, AbstractCommand command) {
        super(command, "settag", Permissions.SET_TAG, ClanPermission.SET_TAG,
                StringUtils.getCommandSyntax(String.format("&c%s &asettag", command.getName()), "&7tag"),
                Message.HELP_SET_TAG.getMessage(false, false),
                !command.isAdminCommand(), CommandType.CLAN_ONLY);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String[] args) {
        checkClan(sender);
        String tag = args[0];
        String strippedTag = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', tag));
        Clan c1 = plugin.getClansManager().getClan(strippedTag);
        Clan c2 = plugin.getClansManager().getClanByTag(strippedTag);
        if (strippedTag.length() < ConfigOption.MIN_SIZE_TAG.getInt())
            sender.sendMessage(Message.NAME_TOO_SHORT.getMessage(!isAdminCommand(), isAdminCommand())
                    .replace("%min-size%", String.valueOf(ConfigOption.MIN_SIZE_TAG.getInt())));
        else if (strippedTag.length() > ConfigOption.MAX_SIZE_TAG.getInt())
            sender.sendMessage(Message.NAME_TOO_LONG.getMessage(!isAdminCommand(), isAdminCommand())
                    .replace("%max-size%", String.valueOf(ConfigOption.MAX_SIZE_TAG.getInt())));
        else if (ConfigOption.BANNED_WORDS.getStringList().stream().anyMatch(w -> w.toLowerCase()
                .contains(strippedTag.toLowerCase())))
            sender.sendMessage(Message.BANNED_WORD.getMessage(!isAdminCommand(), isAdminCommand())
                    .replace("%word%", strippedTag));
        else if (!strippedTag.matches("[a-zA-Z0-9]+"))
            sender.sendMessage(Message.CHARACTERS_NOT_ALLOWED.getMessage(!isAdminCommand(), isAdminCommand()));
        else if (c1 != null)
            sender.sendMessage(Message.CLAN_ALREADY_EXISTS.getMessage(!isAdminCommand(), isAdminCommand())
                    .replace("%clan%", c1.getName()));
        else if (c2 != null && !c2.equals(clan))
            sender.sendMessage(Message.CLAN_ALREADY_EXISTS.getMessage(!isAdminCommand(), isAdminCommand())
                    .replace("%clan%", c2.getTag()));
        else {
            clan.setTag(sender, tag.toUpperCase());
            sender.sendMessage(Message.NEW_TAG_SET.getMessage(!isAdminCommand(), isAdminCommand())
                    .replace("%tag%", clan.getTag()));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) list.add("<tag>");
        return list;
    }

    @Override
    public int getMinArguments() {
        return 1;
    }
}