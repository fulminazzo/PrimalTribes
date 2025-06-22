package it.fulminazzo.primaltribes;

import it.angrybear.Commands.BearCommand;
import it.angrybear.Enums.BearLoggingMessage;
import it.angrybear.SimpleBearPlugin;
import it.angrybear.Utils.PluginsUtil;
import it.fulminazzo.primaltribes.AbstractClasses.AbstractCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.SubCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.SuperClanSubCommand;
import it.fulminazzo.primaltribes.AbstractClasses.SubCommands.SuperSubCommand;
import it.fulminazzo.primaltribes.Api.Events.LoadEvent;
import it.fulminazzo.primaltribes.Api.Events.UnLoadEvent;
import it.fulminazzo.primaltribes.Commands.ClanAdminCommand;
import it.fulminazzo.primaltribes.Commands.ClanCommand;
import it.fulminazzo.primaltribes.Commands.ClanLookupCommand;
import it.fulminazzo.primaltribes.Commands.ClansCommand;
import it.fulminazzo.primaltribes.Enums.CommandName;
import it.fulminazzo.primaltribes.Enums.ConfigOption;
import it.fulminazzo.primaltribes.Enums.Message;
import it.fulminazzo.primaltribes.Enums.Permissions;
import it.fulminazzo.primaltribes.Exceptions.CommandNameException;
import it.fulminazzo.primaltribes.Listeners.ClanPlayersListener;
import it.fulminazzo.primaltribes.Listeners.PlayerListener;
import it.fulminazzo.primaltribes.Managers.ClanPlayersManager;
import it.fulminazzo.primaltribes.Managers.ClansManager;
import it.fulminazzo.primaltribes.Managers.RolesManager;
import it.fulminazzo.primaltribes.Objects.Clan;
import it.fulminazzo.primaltribes.Objects.Role;
import it.fulminazzo.primaltribes.Objects.Users.ClanPlayer;
import it.fulminazzo.reflectionutils.Objects.ReflObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.stream.Collectors;

public class PrimalTribes extends SimpleBearPlugin {
    private static PrimalTribes plugin;
    private static String commandName;
    private RolesManager rolesManager;
    private ClansManager clansManager;
    private ClanPlayersManager clanPlayersManager;
    private ClanPlayersListener clanPlayersListener;
    private List<Permission> permissions;
    private final List<AbstractCommand> commands = new ArrayList<>();
    private static final List<String> toReload = new ArrayList<>();
    private BukkitTask reloadTask;

    @Override
    public void onEnable() {
        plugin = this;
        super.onEnable();
        if (!isEnabled()) return;

        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        clanPlayersListener = new ClanPlayersListener(this);
        Bukkit.getPluginManager().registerEvents(clanPlayersListener, this);
        if (reloadTask != null) reloadTask.cancel();

        reloadTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, () ->
                new ArrayList<>(toReload).forEach(p -> {
                    reloadPlugin(p);
                    toReload.remove(p);
                }), 20, 20);
    }

    @Override
    public void onDisable() {
        if (reloadTask != null) reloadTask.cancel();
        super.onDisable();
    }

    public RolesManager getRolesManager() {
        return rolesManager;
    }

    public ClansManager getClansManager() {
        return clansManager;
    }

    public ClanPlayersManager getClanPlayersManager() {
        return clanPlayersManager;
    }

    public ClanPlayersListener getClanPlayersListener() {
        return clanPlayersListener;
    }

    public void reloadAll() throws Exception {
        unloadAll();
        loadAll();
    }

    @Override
    public void loadAll() throws Exception {
        super.loadAll();

        rolesManager = new RolesManager(this);
        clansManager = new ClansManager(this);
        clanPlayersManager = new ClanPlayersManager(this);

        loadCommands();
        //TODO: Rework
        //loadPermissions();
        loadPrimalTribesPlaceholders();

        Bukkit.getPluginManager().callEvent(new LoadEvent());
        Arrays.stream(Bukkit.getPluginManager().getPlugins())
                .filter(p -> p.getDescription().getSoftDepend().contains(getName()) ||
                        p.getDescription().getDepend().contains(getName()))
                .filter(Plugin::isEnabled)
                .forEach(p -> toReload.add(p.getName()));
    }

    @Override
    public void unloadAll() throws Exception {
        if (clanPlayersManager != null) clanPlayersManager.reload();
        if (clanPlayersListener != null) clanPlayersListener.quit();
        if (clansManager != null) clansManager.reload();

        //TODO:REWORK
        //unloadPermissions();
        unloadCommands();
        super.unloadAll();

        Bukkit.getPluginManager().callEvent(new UnLoadEvent());
    }

    private void loadCommands() throws Exception {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(getName());
        if (plugin == null || !plugin.isEnabled()) return;
        unloadCommands();

        CommandName commandName = ConfigOption.COMMAND_NAME.getCommandName();
        if (commandName == null) throw new CommandNameException();

        String name = commandName.getName();
        PrimalTribes.commandName = name;

        this.commands.addAll(Arrays.asList(
                new ClanCommand(this, name),
                new ClanAdminCommand(this, name + "a"),
                new ClansCommand(this, name + "s"),
                new ClanLookupCommand(this, name + "lookup")
        ));
        this.commands.forEach(BearCommand::loadCommand);
    }

    public void loadPrimalTribesPlaceholders() {
        addPlaceholder("clan", this::handleClanPlaceholder);
        addPlaceholder(PrimalTribes.getCommandName().toLowerCase(), this::handleClanPlaceholder);
        addPlaceholder("role", (p, identifier) -> {
            ClanPlayer clanPlayer = plugin.getClanPlayersManager().getClanPlayer(p);
            if (clanPlayer == null || !clanPlayer.isInClan())
                return Message.NOT_IN_CLAN.getMessage(false, false);
            Role role = clanPlayer.getMember().getRole();
            if (identifier.equalsIgnoreCase("name")) return role.getName();
            if (identifier.equalsIgnoreCase("displayname")) return role.getDisplayName();
            if (identifier.equalsIgnoreCase("prefix")) return role.getPrefix();
            if (identifier.equalsIgnoreCase("priority")) return String.valueOf(role.getPriority());
            return null;
        });
    }

    private void unloadCommands() {
        commands.forEach(BearCommand::unloadCommand);
    }

    public String handleClanPlaceholder(Player player, String identifier) {
        ClanPlayer clanPlayer = plugin.getClanPlayersManager().getClanPlayer(player);
        if (clanPlayer == null || !clanPlayer.isInClan())
            return Message.NOT_IN_CLAN.getMessage(false, false);
        Clan clan = clanPlayer.getClan();
        if (identifier.equalsIgnoreCase("name")) return clan.getName();
        if (identifier.equalsIgnoreCase("motd")) return clan.getMOTD();
        if (identifier.equalsIgnoreCase("displayname")) return clan.getDisplayName();
        if (identifier.equalsIgnoreCase("description")) return clan.getDescription();
        if (identifier.equalsIgnoreCase("tag")) return clan.getTag();
        if (identifier.equalsIgnoreCase("telegram")) return clan.getTelegram();
        if (identifier.equalsIgnoreCase("discord")) return clan.getDiscord();
        if (identifier.equalsIgnoreCase("chat_enabled"))
            return (clan.isClanChatMuted() ? Message.PLACEHOLDER_DISABLED : Message.PLACEHOLDER_ENABLED).getMessage(false, false);
        if (identifier.equalsIgnoreCase("friendly_fire"))
            return (clan.isFriendlyFire() ? Message.PLACEHOLDER_ENABLED : Message.PLACEHOLDER_DISABLED).getMessage(false, false);
        if (identifier.equalsIgnoreCase("open") || identifier.equalsIgnoreCase("opened"))
            return (clan.isOpened() ? Message.PLACEHOLDER_YES : Message.PLACEHOLDER_NO).getMessage(false, false);
        if (identifier.equalsIgnoreCase("close") || identifier.equalsIgnoreCase("closed"))
            return (!clan.isOpened() ? Message.PLACEHOLDER_YES : Message.PLACEHOLDER_NO).getMessage(false, false);
        if (identifier.equalsIgnoreCase("status")) return clan.getClanStatus();
        if (identifier.equalsIgnoreCase("members")) return String.valueOf(clan.getMembers().size());
        if (identifier.equalsIgnoreCase("bans")) return String.valueOf(clan.getBans().size());
        if (identifier.equalsIgnoreCase("warns")) return String.valueOf(clan.getWarns().size());
        //TODO: Placeholder expansion
        //if (identifier.equalsIgnoreCase("homes")) return String.valueOf(clan.getHomes().size());
        if (identifier.equalsIgnoreCase("invites")) return String.valueOf(clan.getInvitesUUID().size());
        //TODO: Placeholder expansion
        //if (identifier.equalsIgnoreCase("money")) return String.valueOf(clan.getMoney());
        if (identifier.equalsIgnoreCase("ally")) return String.valueOf(clan.getAllies().size());
        if (identifier.equalsIgnoreCase("ally_requests")) return String.valueOf(clan.getAllyRequests().size());
        if (identifier.equalsIgnoreCase("warns_limit")) return String.valueOf(clan.getWarnsLimit());
        return null;
    }

    public AbstractCommand getClanCommand() {
        return commands.stream().filter(c -> c instanceof ClanCommand).findAny().orElse(null);
    }

    public void addClanCommandSubCommand(SubCommand... subCommands) {
        AbstractCommand clanCommand = getClanCommand();
        if (clanCommand != null) addSubCommands(clanCommand, subCommands);
    }

    public AbstractCommand getClanAdminCommand() {
        return commands.stream().filter(c -> c instanceof ClanAdminCommand).findAny().orElse(null);
    }

    public void addClanAdminCommandSubCommand(SubCommand... subCommands) {
        AbstractCommand clanAdminCommand = getClanAdminCommand();
        if (clanAdminCommand != null) addSubCommands(clanAdminCommand, subCommands);
    }

    public AbstractCommand getAbstractCommand(String name) {
        return commands.stream().filter(c -> c.getName().equalsIgnoreCase(name)).findAny().orElse(null);
    }

    public void addSubCommands(String commandName, SubCommand... subCommands) {
        addSubCommands(getAbstractCommand(commandName), subCommands);
    }

    public void addSubCommands(AbstractCommand command, SubCommand... subCommands) {
        if (command == null) return;
        Arrays.stream(subCommands).forEach(command::replaceSubCommand);
    }

    public void loadPermissions() {
        loadPermissions(Permissions.class);
    }

    public void loadPermissions(Class<? extends Permissions> permissionClass) {
        Plugin pl = Bukkit.getPluginManager().getPlugin(getName());
        if (pl == null || !pl.isEnabled()) return;
        unloadPermissions(permissionClass);
        List<String> subCommandsPermissions = commands.stream()
                .flatMap(c -> c.getSubCommands().stream().flatMap(s -> getSubCommandPermissions(s).stream()))
                .collect(Collectors.toList());
        permissions = subCommandsPermissions.stream()
                .map(p -> {
                    String description = String.format(Message.SUBCOMMAND_PERMISSION.getMessage(false, false),
                            p.substring(getFirstDotString(p).length() + 1)
                                    .replace(".", " ")
                                    .replace("clan", commandName));
                    if (description.endsWith(" ")) description = description.substring(0, description.length() - 1);
                    return new Permission(p, description);
                })
                .collect(Collectors.toList());
        Permissions[] values = new ReflObject<>(permissionClass.getCanonicalName(), false).getMethodObject("values");
        Arrays.stream(values)
                .filter(p -> !(subCommandsPermissions.contains(p.getPermission(true)) ||
                        subCommandsPermissions.contains(p.getPermission(false))))
                .map(p -> {
                    List<String> perms = new ArrayList<>();
                    boolean isPartialAdmin = subCommandsPermissions.stream().anyMatch(s -> p.getPermission(true).startsWith(s));
                    boolean isNotPartialAdmin = subCommandsPermissions.stream().anyMatch(s -> p.getPermission(false).startsWith(s));
                    if (isPartialAdmin)
                        perms.add(p.getPermission(true));
                    if (isNotPartialAdmin)
                        perms.add(p.getPermission(false));
                    if (!isNotPartialAdmin && !isPartialAdmin)
                        perms.addAll(Arrays.asList(p.getPermission(true), p.getPermission(false)));
                    return perms;
                })
                .flatMap(Collection::stream)
                .forEach(p -> {
                    String description = String.format(Message.GENERAL_PERMISSION.getMessage(false, false),
                            p.substring(getFirstDotString(p).length() + 1)
                                    .replace(".", " ")
                                    .replace("clan", commandName));
                    if (description.endsWith(" ")) description = description.substring(0, description.length() - 1);
                    permissions.add(new Permission(p, description));
                });

        permissions.stream().sorted(Comparator.comparing(Permission::getName)).forEach(p -> {
            String commandPermission = getFirstDotString(p.getName());
            Permission mainPermission = Bukkit.getPluginManager().getPermission("primaltribes." + commandPermission);
            String strippedPermission = p.getName().substring(commandPermission.length());
            Optional<Permission> parentPermission = permissions.stream().filter(x ->
                            x.getName().equalsIgnoreCase(commandPermission + "." +
                                    strippedPermission.substring(strippedPermission.indexOf("."))))
                    .findAny();
            if (strippedPermission.contains(".") && parentPermission.isPresent())
                p.addParent(parentPermission.get(), false);
            else if (mainPermission != null)
                p.addParent(mainPermission, false);
            if (Bukkit.getPluginManager().getPermission(p.getName()) == null)
                Bukkit.getPluginManager().addPermission(p);
        });
    }

    private List<String> getSubCommandPermissions(SubCommand subCommand) {
        List<String> permissions = new ArrayList<>();
        permissions.add(subCommand.getPermission());
        if (subCommand instanceof SuperSubCommand)
            permissions.addAll(((SuperSubCommand) subCommand).getSubCommands()
                    .stream()
                    .flatMap(s -> getSubCommandPermissions(s).stream())
                    .collect(Collectors.toList()));
        if (subCommand instanceof SuperClanSubCommand)
            permissions.addAll(((SuperClanSubCommand) subCommand).getSubCommands()
                    .stream()
                    .flatMap(s -> getSubCommandPermissions(s).stream())
                    .collect(Collectors.toList()));
        return permissions;
    }

    private String getFirstDotString(String string) {
        if (string.contains(".")) return string.substring(0, string.indexOf("."));
        else return "";
    }

    public void unloadPermissions() {
        if (permissions == null) return;
        permissions.forEach(p -> Bukkit.getPluginManager().removePermission(p));
    }

    public void unloadPermissions(Class<? extends Permissions> permissionClass) {
        if (permissions == null) return;
        Permissions[] values = new ReflObject<>(permissionClass.getCanonicalName(), false).getMethodObject("values");
        Arrays.stream(values)
                .map(p -> new String[]{p.getPermission(false), p.getPermission(true)})
                .flatMap(Arrays::stream)
                .forEach(p -> {
                    Permission permission = Bukkit.getPluginManager().getPermission(p);
                    if (permission != null) {
                        Bukkit.getPluginManager().removePermission(permission);
                        permissions.remove(permission);
                    }
                });
    }

    public static String getCommandName() {
        return commandName;
    }

    public static void requestPluginReload(Plugin plugin) {
        if (plugin != null) {
            if (toReload.contains(plugin.getName())) return;
            toReload.add(plugin.getName());
        }
    }

    private void reloadPlugin(String pluginName) {
        if (pluginName != null) {
            try {
                Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
                if (plugin != null) PluginsUtil.unloadPlugin(plugin);
                PluginsUtil.loadPlugin(pluginName);
            } catch (Exception e) {
                PrimalTribes.logError(BearLoggingMessage.GENERAL_ERROR_OCCURRED, "%task%",
                        String.format("enabling plugin %s", pluginName), "%error%", e.getMessage());
                e.printStackTrace();
                Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
                if (plugin != null) Bukkit.getPluginManager().disablePlugin(plugin);
            }
        }
    }

    public static boolean isAdminCommand(AbstractCommand command) {
        return command instanceof ClanAdminCommand;
    }

    public static PrimalTribes getPlugin() {
        return plugin;
    }
}
