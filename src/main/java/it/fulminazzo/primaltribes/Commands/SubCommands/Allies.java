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
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Allies extends ConfirmClanSubCommand {

    public Allies(AbstractCommand command) {
        super(command, "allies", Permissions.ALLIES, ClanPermission.ALLIES,
                StringUtils.getCommandSyntax(String.format("&c%s &aallies", command.getName())),
                Message.HELP_ALLIES.getMessage(false, false),
                !command.isAdminCommand(), CommandType.CLAN_ONLY);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String[] args) {
        checkClan(sender);
        List<Clan> allies = clan.getAllies();
        if (!(sender instanceof Player)) {
            sender.sendMessage(Message.CONSOLE_CANNOT_EXECUTE.getMessage(!isAdminCommand(), isAdminCommand()));
            return;
        }
        if (allies.isEmpty()) {
            sender.sendMessage(Message.NO_ALLIES.getMessage(!isAdminCommand(), isAdminCommand()));
            return;
        }
        //TODO
        /*MultipleGUI multipleGUI = new MultipleGUI(
                allies.stream()
                        .map(clan -> {
                            String lore = Message.GUI_ALLIES_LORE.getMessage(false, false)
                                    .replace("%clan%", clan.getDisplayName());
                            return new Object[]{GUIUtils.getPlayerHead(clan.getDisplayName(), lore), clan};
                        })
                        .map(o -> (Item) o[0])
                        .toArray(Item[]::new),
                54,
                GUIUtils.getPreviousPageItem(),
                GUIUtils.getNextPageItem()
        );
        multipleGUI.setTitle(Message.GUI_ALLIES_TITLE.getMessage(false, false)
                .replace("%clan%", clan.getDisplayName()));
        multipleGUI.setAllCorners(GUIUtils.getCornerItem());
        multipleGUI.build();
        multipleGUI.openGUI((Player) sender, 1);*/
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