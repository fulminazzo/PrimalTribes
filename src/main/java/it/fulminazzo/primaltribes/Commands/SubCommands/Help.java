package it.fulminazzo.primaltribes.Commands.SubCommands;

import it.angrybear.Utils.NumberUtils;
import it.angrybear.Utils.TextComponentUtils;
import it.fulminazzo.primaltribes.Utils.StringUtils;
import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.SubCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.SuperClanSubCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.SuperSubCommand;
import it.fulminazzo.primaltribes.Enums.CommandType;
import it.fulminazzo.primaltribes.Enums.Message;
import it.fulminazzo.primaltribes.Enums.Permissions;
import it.fulminazzo.primaltribes.PrimalTribes;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Help extends SubCommand {
    private final String commandName;
    private final AbstractCommand command;

    public Help(AbstractCommand command) {
        super(command, "help", Permissions.HELP,
                StringUtils.getCommandSyntax(String.format("&c%s &ahelp", command.getName()), "&9command"),
                Message.HELP_HELP.getMessage(false, false),
                CommandType.GENERAL, "?");
        this.commandName = command.getName();
        this.command = command;
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String[] args) {
        List<SubCommand> subCommands = getSubCommands(sender);
        int pageIndex = -1;
        int chatSize = 48;

        if (subCommands.isEmpty()) {
            sender.sendMessage(Message.NO_PERMISSIONS.getMessage(!isAdminCommand(), isAdminCommand()));
            return;
        }

        if (args.length > 0) {
            String argument = args[0];
            if (NumberUtils.isNatural(argument)) pageIndex = 0;
            else {
                subCommands = getSubCommands(sender).stream().filter(s ->
                        s.getName().toLowerCase().contains(argument.toLowerCase()) ||
                                s.getUsage().toLowerCase().contains(argument.toLowerCase()) ||
                                Arrays.stream(s.getAliases()).anyMatch(a -> a.toLowerCase().contains(argument.toLowerCase())))
                        .collect(Collectors.toList());
                if (args.length > 1 && NumberUtils.isNatural(argument)) pageIndex = 1;
            }

            if (subCommands.isEmpty()) {
                sender.sendMessage(Message.SUBCOMMAND_NOT_FOUND.getMessage(!isAdminCommand(), isAdminCommand())
                        .replace("%subcommand%", argument));
                return;
            }
        }

        int helpPerPage = 7;
        int page = 0;
        if (pageIndex != -1) page = Math.max(Integer.parseInt(args[pageIndex]) - 1, 0);
        int maxPages = subCommands.size() / helpPerPage + 1;
        page = Math.min(maxPages - 1, page);

        String helpPageMessage = Message.HELP_PAGE.getMessage(false, false)
                .replace("%plugin-name%", PrimalTribes.getPlugin().getName());
        String separator = StringUtils.repeatChar(Message.HELP_PAGE_SEPARATOR.getMessage(false, false),
                (chatSize - ChatColor.stripColor(helpPageMessage).length()) / 2);
        sender.sendMessage(separator + helpPageMessage + separator);

        String[] formats = Message.HELP_FORMAT.getMessage(false, false).split("%command%");
        subCommands.stream()
                .sorted(Comparator.comparing(SubCommand::getUsage))
                .collect(Collectors.toList())
                .subList(page * helpPerPage, Math.min((page + 1) * helpPerPage, subCommands.size()))
                .forEach(s -> {
                    String usage = s.getUsage();
                    if (isAdminCommand())
                        usage = usage.replace(commandName, StringUtils.getCommandSyntax(String.format("/&c%s", command.getName()), "clan"));
                    if (sender instanceof Player) {
                        TextComponent main = new TextComponent(formats[0]
                                .replace("%command%", usage)
                                .replace("%help%", s.getDescription()));
                        TextComponent command = new TextComponent(s.getUsage());
                        command.setHoverEvent(TextComponentUtils.getTextHoverEvent(Message.CLICK_TO_TRY.getMessage(false, false)));
                        command.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                                ChatColor.stripColor(usage).split("<")[0]));
                        main.addExtra(command);
                        TextComponent second = new TextComponent(String.join(" ", Arrays.copyOfRange(formats, 1, formats.length))
                                .replace("%command%", usage)
                                .replace("%help%", s.getDescription()));
                        main.addExtra(second);
                        ((Player) sender).spigot().sendMessage(main);
                    } else
                        sender.sendMessage(
                                Message.HELP_FORMAT.getMessage(false, false)
                                        .replace("%command%", usage)
                                        .replace("%help%", s.getDescription())
                        );
                });
        int leftSize = 0;
        int rightSize = 0;
        String singleSeparator = Message.HELP_PAGE_SEPARATOR.getMessage(false, false);
        String helpPageFormat = Message.HELP_PAGE_FORMAT.getMessage(false, false)
                .replace("%page%", String.valueOf(page + 1))
                .replace("%max-page%", String.valueOf(maxPages))
                .replace("%page-separator%", singleSeparator);
        String previous = Message.HELP_PAGE_PREVIOUS.getMessage(false, false)
                .replace("%page-separator%", singleSeparator);
        String next = Message.HELP_PAGE_NEXT.getMessage(false, false)
                .replace("%page-separator%", singleSeparator);
        String tempHelpPageFormat = helpPageFormat;
        if (page > 0) {
            helpPageFormat = previous.concat(helpPageFormat);
            leftSize = ChatColor.stripColor(previous).length();
        }
        if (page < maxPages - 1) {
            helpPageFormat = helpPageFormat.concat(next);
            rightSize = ChatColor.stripColor(next).length();
        }
        chatSize = (chatSize - ChatColor.stripColor(helpPageFormat).length()) / 2;
        String leftSeparator = StringUtils.repeatChar(Message.HELP_PAGE_SEPARATOR.getMessage(false, false),
                chatSize - (leftSize / 2) + (rightSize / 2));
        String rightSeparator = StringUtils.repeatChar(Message.HELP_PAGE_SEPARATOR.getMessage(false, false),
                chatSize - (rightSize / 2) + (leftSize / 2));
        TextComponent component = new TextComponent(leftSeparator);
        if (page > 0) {
            TextComponent previousComponent = new TextComponent(previous);
            ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                    String.format("/%s %s %d", commandName, getName(), page));
            previousComponent.setClickEvent(clickEvent);
            previousComponent.setHoverEvent(TextComponentUtils.getTextHoverEvent(Message.GO_TO_PREVIOUS_PAGE.getMessage(false, false)));
            component.addExtra(previousComponent);
        }
        component.addExtra(tempHelpPageFormat);
        if (page < maxPages - 1) {
            TextComponent afterComponent = new TextComponent(next);
            ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                    String.format("/%s %s %d", commandName, getName(), page + 2));
            afterComponent.setClickEvent(clickEvent);
            afterComponent.setHoverEvent(TextComponentUtils.getTextHoverEvent(Message.GO_TO_NEXT_PAGE.getMessage(false, false)));
            component.addExtra(afterComponent);
        }
        component.addExtra(rightSeparator);
        if (sender instanceof Player) ((Player) sender).spigot().sendMessage(component);
        else sender.sendMessage(component.toLegacyText());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1)
            list = getSubCommands(sender).stream()
                    .flatMap(s -> Stream.concat(Stream.of(s.getName()), Arrays.stream(s.getAliases())))
                    .collect(Collectors.toList());
        return list;
    }

    private List<SubCommand> getSubCommands(CommandSender sender) {
        return command.getSubCommands().stream()
                .map(this::getSubCommandSubCommands)
                .flatMap(Collection::stream)
                .filter(s -> command.validateSubCommand(s, sender))
                .collect(Collectors.toList());
    }

    private List<SubCommand> getSubCommandSubCommands(SubCommand subCommand) {
        List<SubCommand> subCommands = new ArrayList<>(Collections.singletonList(subCommand));
        if (subCommand instanceof SuperSubCommand)
            ((SuperSubCommand) subCommand).getSubCommands().forEach(s ->
                    subCommands.addAll(getSubCommandSubCommands(s)));
        if (subCommand instanceof SuperClanSubCommand)
            ((SuperClanSubCommand) subCommand).getSubCommands().forEach(s ->
                    subCommands.addAll(getSubCommandSubCommands(s)));
        return subCommands;
    }

    @Override
    public int getMinArguments() {
        return 0;
    }
}