package it.fulminazzo.primaltribes.Commands.SubCommands;

import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.ClanSubCommand;
import it.fulminazzo.primaltribes.Enums.*;
import it.fulminazzo.primaltribes.Objects.Clan;
import it.fulminazzo.primaltribes.PrimalTribes;
import it.angrybear.Utils.HexUtils;
import it.fulminazzo.primaltribes.Utils.StringUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SetDisplayName extends ClanSubCommand {
    private final PrimalTribes plugin;

    public SetDisplayName(PrimalTribes plugin, AbstractCommand command) {
        super(command, "setdisplayname", Permissions.SET_DISPLAYNAME, ClanPermission.SET_DISPLAYNAME,
                StringUtils.getCommandSyntax(String.format("&c%s &asetdisplayname", command.getName()), "&7name"),
                Message.HELP_SET_DISPLAYNAME.getMessage(false, false),
                !command.isAdminCommand(), CommandType.CLAN_ONLY);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String[] args) {
        checkClan(sender);
        String displayName = args[0];
        String strippedDisplayName = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&',
                HexUtils.parseString(displayName)));
        Clan c1 = plugin.getClansManager().getClan(strippedDisplayName);
        Clan c2 = plugin.getClansManager().getClanByDisplayName(strippedDisplayName);
        if (strippedDisplayName.length() < ConfigOption.MIN_SIZE_NAME.getInt())
            sender.sendMessage(Message.NAME_TOO_SHORT.getMessage(!isAdminCommand(), isAdminCommand())
                    .replace("%min-size%", String.valueOf(ConfigOption.MIN_SIZE_NAME.getInt())));
        else if (strippedDisplayName.length() > ConfigOption.MAX_SIZE_NAME.getInt())
            sender.sendMessage(Message.NAME_TOO_LONG.getMessage(!isAdminCommand(), isAdminCommand())
                    .replace("%max-size%", String.valueOf(ConfigOption.MAX_SIZE_NAME.getInt())));
        else {
            Optional<String> bannedWord = ConfigOption.BANNED_WORDS.getStringList().stream()
                    .map(String::toLowerCase)
                    .filter(w -> w.contains(strippedDisplayName.toLowerCase()) || strippedDisplayName.toLowerCase().contains(w))
                    .findAny();
            if (bannedWord.isPresent())
                sender.sendMessage(Message.BANNED_WORD.getMessage(!isAdminCommand(), isAdminCommand())
                        .replace("%word%", bannedWord.get()));
            else if (!strippedDisplayName.matches("[a-zA-Z0-9]+"))
                sender.sendMessage(Message.CHARACTERS_NOT_ALLOWED.getMessage(!isAdminCommand(), isAdminCommand()));
            else if (c1 != null && !c1.equals(clan))
                sender.sendMessage(Message.CLAN_ALREADY_EXISTS.getMessage(!isAdminCommand(), isAdminCommand())
                        .replace("%clan%", c1.getName()));
            else if (c2 != null && !c2.equals(clan))
                sender.sendMessage(Message.CLAN_ALREADY_EXISTS.getMessage(!isAdminCommand(), isAdminCommand())
                        .replace("%clan%", c2.getDisplayName()));
            else {
                clan.setDisplayName(sender, displayName);
                sender.sendMessage(Message.NEW_DISPLAYNAME_SET.getMessage(!isAdminCommand(), isAdminCommand())
                        .replace("%display-name%", clan.getDisplayName()));
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) list.add("<display-name>");
        return list;
    }

    @Override
    public int getMinArguments() {
        return 1;
    }
}