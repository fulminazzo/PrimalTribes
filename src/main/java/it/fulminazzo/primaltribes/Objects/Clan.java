package it.fulminazzo.primaltribes.Objects;

import it.angrybear.Enums.BearLoggingMessage;
import it.angrybear.Utils.ConfigUtils;
import it.angrybear.Utils.FileUtils;
import it.angrybear.Utils.HexUtils;
import it.fulminazzo.primaltribes.Api.Events.ClanQueryInfoEvent;
import it.fulminazzo.primaltribes.Api.Events.SaveClanEvent;
import it.fulminazzo.primaltribes.Enums.ConfigOption;
import it.fulminazzo.primaltribes.Enums.Logging;
import it.fulminazzo.primaltribes.Enums.Message;
import it.fulminazzo.primaltribes.Exceptions.ClanException;
import it.fulminazzo.primaltribes.Managers.ClansManager;
import it.fulminazzo.primaltribes.Objects.Users.*;
import it.fulminazzo.primaltribes.PrimalTribes;
import it.fulminazzo.primaltribes.Utils.Base64Util;
import it.fulminazzo.primaltribes.Utils.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Clan {
    private final PrimalTribes plugin;
    private final File clanFolder;
    private final File infoFile;
    private final FileConfiguration infoConfiguration;
    private final File logsFile;
    private final FileConfiguration logsConfiguration;
    private final String name;
    private final List<Invite> invites;
    private final HashMap<String, BukkitTask> recentlyInvitedPlayers;
    private final List<Member> members;
    private final List<User> bans;
    private final List<Warned> warns;
    private final SimpleDateFormat loggingFormat;
    private final List<AllyRequest> allyRequests;
    private final HashMap<String, BukkitTask> recentlyInvitedAllies;
    private final List<String> allies;
    private PublicKey publicKey;
    private PrivateKey privateKey;

    private String displayName;
    private String description;
    private String tag;
    private String motd;
    private String telegram;
    private String discord;
    private boolean opened;
    private boolean invitesEnabled;
    private boolean clanChatMuted;
    private boolean friendlyFire;
    private int warnsLimit;

    private final HashMap<UUID, BukkitTask> playerSprays;

    // Add from ingame
    public Clan(PrimalTribes plugin, String clanName, File clanFolder, Player player) throws IOException {
        this.plugin = plugin;
        this.clanFolder = clanFolder;
        this.infoFile = new File(clanFolder, "info.yml");
        if (infoFile.exists()) FileUtils.deleteFile(infoFile);
        FileUtils.createNewFile(infoFile);
        this.infoConfiguration = YamlConfiguration.loadConfiguration(infoFile);
        this.logsFile = new File(clanFolder, "logs.yml");
        if (logsFile.exists()) FileUtils.deleteFile(logsFile);
        FileUtils.createNewFile(logsFile);
        this.logsConfiguration = YamlConfiguration.loadConfiguration(logsFile);

        this.name = clanName;
        this.displayName = clanName;
        this.description = "";
        this.tag = "";
        this.motd = Message.DEFAULT_MOTD.getMessage(false, false);
        this.telegram = "";
        this.discord = "";

        this.invites = new ArrayList<>();
        this.members = new ArrayList<>();
        this.members.add(new Member(player, plugin.getRolesManager().getLeaderRole()));
        this.bans = new ArrayList<>();
        this.warns = new ArrayList<>();

        this.opened = false;
        this.invitesEnabled = true;
        this.clanChatMuted = false;
        this.friendlyFire = true;
        this.warnsLimit = 3;

        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair pair = generator.generateKeyPair();
            this.publicKey = pair.getPublic();
            this.privateKey = pair.getPrivate();
        } catch (NoSuchAlgorithmException e) {
            PrimalTribes.logError("There was an error while creating some keys: " + e.getMessage());
        }

        this.allyRequests = new ArrayList<>();
        this.allies = new ArrayList<>();

        this.recentlyInvitedPlayers = new HashMap<>();
        this.recentlyInvitedAllies = new HashMap<>();

        this.loggingFormat = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");

        this.playerSprays = new HashMap<>();

        log(Logging.CREATED.getLoggingMessage(player, clanName));
        save();
    }

    // Add from file
    public Clan(PrimalTribes plugin, String clanName, File clanFolder) throws ClanException {
        this.plugin = plugin;
        this.clanFolder = clanFolder;
        File infoFile = new File(clanFolder, "info.yml");
        if (!infoFile.exists()) throw new ClanException(clanName);
        this.infoFile = infoFile;
        this.infoConfiguration = YamlConfiguration.loadConfiguration(infoFile);
        File logsFile = new File(clanFolder, "logs.yml");
        if (!logsFile.exists()) throw new ClanException(clanName);
        this.logsFile = logsFile;
        this.logsConfiguration = YamlConfiguration.loadConfiguration(logsFile);

        this.name = infoConfiguration.getString("name");
        this.displayName = infoConfiguration.getString("display-name");
        this.description = infoConfiguration.getString("description");
        this.tag = infoConfiguration.getString("tag");
        this.motd = infoConfiguration.getString("motd");
        this.telegram = infoConfiguration.getString("telegram");
        this.discord = infoConfiguration.getString("discord");

        this.invites = new ArrayList<>();

        ConfigurationSection membersSection = infoConfiguration.getConfigurationSection("members");

        if (membersSection == null) this.members = new ArrayList<>();
        else this.members = membersSection.getKeys(false).stream()
                .map(key -> new Member(membersSection.getConfigurationSection(key)))
                .collect(Collectors.toList());

        ConfigurationSection bansSection = infoConfiguration.getConfigurationSection("bans");
        if (bansSection == null) this.bans = new ArrayList<>();
        else this.bans = bansSection.getKeys(false).stream()
                .map(bansSection::getConfigurationSection)
                .filter(Objects::nonNull)
                .map(User::new)
                .collect(Collectors.toList());

        ConfigurationSection warnsSection = infoConfiguration.getConfigurationSection("warns");
        if (warnsSection == null) this.warns = new ArrayList<>();
        else this.warns = warnsSection.getKeys(false).stream()
                .map(key -> new Warned(warnsSection.getConfigurationSection(key)))
                .collect(Collectors.toList());



        this.opened = infoConfiguration.getBoolean("opened");
        this.invitesEnabled = infoConfiguration.getBoolean("invites-enabled");
        this.clanChatMuted = infoConfiguration.getBoolean("clan-chat-muted");
        this.friendlyFire = infoConfiguration.getBoolean("friendly-fire");
        this.warnsLimit = infoConfiguration.getInt("warns-limit");

        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            String publicKey = infoConfiguration.getString("security.public-key");
            byte[] publicKeyBytes = Base64Util.decodeBase64(publicKey);
            EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            this.publicKey = keyFactory.generatePublic(publicKeySpec);

            String privateKey = infoConfiguration.getString("security.private-key");
            byte[] privateKeyBytes = Base64Util.decodeBase64(privateKey);
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            this.privateKey = keyFactory.generatePrivate(privateKeySpec);
        } catch (Exception e) {
            PrimalTribes.logError("There was an error while getting some keys: " + e.getMessage());
        }

        this.allyRequests = new ArrayList<>();
        this.allies = infoConfiguration.getStringList("allies");

        this.recentlyInvitedPlayers = new HashMap<>();
        this.recentlyInvitedAllies = new HashMap<>();

        this.loggingFormat = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");

        this.playerSprays = new HashMap<>();
    }

    public String getInfo() {
        String info = Message.INFO_FORMAT.getMessage(false, false)
                .replace("%name%", getName())
                .replace("%invites%", String.valueOf(getInvitesName().size()))
                .replace("%members%", String.valueOf(getMembers().size()))
                .replace("%bans%", String.valueOf(getBans().size()))
                .replace("%warns%", String.valueOf(getWarns().size()))
                //TODO: Yo man!
                //.replace("%homes%", String.valueOf(getHomes().size()))
                .replace("%allies%", String.valueOf(getAllies().size()))
                .replace("%ally-invites%", String.valueOf(getAllyRequests().size()))
                .replace("%display-name%", getDisplayName())
                .replace("%tag%", getTag())
                .replace("%motd%", getMOTD())
                .replace("%telegram%", getTelegram())
                .replace("%description%", getDescription())
                .replace("%discord%", getDiscord())
                .replace("%clan-status%", getClanStatus())
                .replace("%warns-limit%", String.valueOf(getWarnsLimit()));
        ClanQueryInfoEvent clanQueryInfoEvent = new ClanQueryInfoEvent(this, info);
        Bukkit.getPluginManager().callEvent(clanQueryInfoEvent);
        return clanQueryInfoEvent.getInfo();
    }

    public String getClanStatus() {
        if (opened) return Message.STATUS_OPENED.getMessage(false, false);
        else if (invitesEnabled) return Message.STATUS_INVITES.getMessage(false, false);
        else return Message.STATUS_CLOSED.getMessage(false, false);
    }

    public String getName() {
        return name;
    }

    public void setDisplayName(CommandSender sender, String displayName) {
        log(Logging.SET.getLoggingMessage(sender, "DisplayName", String.format("\"%s\"", this.displayName), String.format("\"%s\"", displayName)));
        this.displayName = parseColorString(displayName).replace("§", "&");
        save();
        notifyUsers(Message.NOTIFY_DISPLAYNAME, Message.NOTIFY_ACTIONBAR_DISPLAYNAME);
    }

    public void unsetDisplayName(CommandSender sender) {
        setDisplayName(sender, name);
    }

    public String getDisplayName() {
        return parseColorString(displayName);
    }

    public void setDescription(CommandSender sender, String description) {
        log(Logging.SET.getLoggingMessage(sender, "Description", String.format("\"%s\"", this.description),
                String.format("\"%s\"", description)));
        this.description = parseColorString(description).replace("§", "&");
        save();
        notifyUsers(Message.NOTIFY_DESCRIPTION, Message.NOTIFY_ACTIONBAR_DESCRIPTION);
    }

    public void unsetDescription(CommandSender sender) {
        setDescription(sender, name);
    }

    public String getDescription() {
        return parseColorString(description);
    }

    public void setTag(CommandSender sender, String tag) {
        log(Logging.SET.getLoggingMessage(sender, "Tag", String.format("\"%s\"", this.tag), String.format("\"%s\"", tag)));
        this.tag = tag;
        save();
        notifyUsers(Message.NOTIFY_TAG, Message.NOTIFY_ACTIONBAR_TAG);
    }

    public void unsetTag(CommandSender sender) {
        setTag(sender, "");
    }

    public String getTag() {
        return tag;
    }

    public void setMOTD(CommandSender sender, String motd) {
        log(Logging.SET.getLoggingMessage(sender, "MOTD", String.format("\"%s\"", this.motd), String.format("\"%s\"", motd)));
        this.motd = parseColorString(motd).replace("§", "&");
        save();
        notifyUsers(Message.NOTIFY_MOTD, Message.NOTIFY_ACTIONBAR_MOTD);
    }

    public void unsetMOTD(CommandSender sender) {
        setMOTD(sender, Message.DEFAULT_MOTD.getMessage(false, false));
    }

    public String getMOTD() {
        return parseColorString(motd)
                .replace("%command-name%", PrimalTribes.getCommandName())
                .replace("%clan-name%", displayName == null ? name : displayName)
                .replace("%leader%", getLeader().getName())
                .replace("%members%", String.valueOf(members.size()));
    }

    public void setTelegram(CommandSender sender, String telegram) {
        log(Logging.SET.getLoggingMessage(sender, "Telegram", String.format("\"%s\"", this.telegram), String.format("\"%s\"", telegram)));
        this.telegram = Base64Util.encrypt(telegram, publicKey);
        save();
        notifyUsers(Message.NOTIFY_TELEGRAM, Message.NOTIFY_ACTIONBAR_TELEGRAM);
    }

    public void unsetTelegram(CommandSender sender) {
        setTelegram(sender, "");
    }

    public String getTelegram() {
        return Base64Util.decrypt(telegram, privateKey);
    }

    public void setDiscord(CommandSender sender, String discord) {
        log(Logging.SET.getLoggingMessage(sender, "Discord", String.format("\"%s\"", this.discord), String.format("\"%s\"", discord)));
        this.discord = Base64Util.encrypt(discord, publicKey);
        save();
        notifyUsers(Message.NOTIFY_DISCORD, Message.NOTIFY_ACTIONBAR_DISCORD);
    }

    public void unsetDiscord(CommandSender sender) {
        setDiscord(sender, "");
    }

    public String getDiscord() {
        return Base64Util.decrypt(discord, privateKey);
    }

    private Invite getInvite(Player player) {
        return invites.stream().filter(i -> i.getUuid().equals(player.getUniqueId())).findAny().orElse(null);
    }

    public Invite getInvite(String playerName) {
        return invites.stream().filter(i -> i.getName().equalsIgnoreCase(playerName)).findAny().orElse(null);
    }

    public boolean isPlayerInvited(Player player) {
        return getInvitesUUID().contains(player.getUniqueId());
    }

    public boolean isPlayerInvited(String playerName) {
        return getInvite(playerName) != null;
    }

    public void invitePlayer(CommandSender sender, Player player) {
        if (isPlayerInvited(player)) return;
        log(Logging.INVITE.getLoggingMessage(sender, player.getName()));
        Invite invite = new Invite(player, Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            Invite i = getInvite(player);
            if (i != null) invites.remove(i);
        }, 20L * ConfigOption.INVITES_TIMEOUT.getInt()));
        invites.add(invite);
        notifyUsers(Message.NOTIFY_INVITE, Message.NOTIFY_ACTIONBAR_INVITE, "player", player.getName());
    }

    public void unInvitePlayer(CommandSender sender, String playerName) {
        Invite invite = getInvite(playerName);
        if (invite == null) return;
        if (sender != null) log(Logging.UNINVITE.getLoggingMessage(sender, playerName));
        invite.cancelTask();
        invites.remove(invite);
        if (sender != null) notifyUsers(Message.NOTIFY_UNINVITE, Message.NOTIFY_ACTIONBAR_UNINVITE, "player", playerName);
        recentlyInvitedPlayers.put(playerName,
                Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () ->
                                recentlyInvitedPlayers.remove(playerName),
                        ConfigOption.INVITES_TIMEOUT.getInt() * 20L));
    }

    public boolean wasRecentlyInvited(Player player) {
        return recentlyInvitedPlayers.containsKey(player.getName());
    }

    public List<String> getInvitesName() {
        return invites.stream().map(User::getName).collect(Collectors.toList());
    }

    public List<UUID> getInvitesUUID() {
        return invites.stream().map(User::getUuid).collect(Collectors.toList());
    }

    public Member getLeader() {
        return getMembers().stream().filter(m -> m.getRole().isLeaderRole()).findAny().orElse(null);
    }

    public Role[] setLeader(CommandSender sender, String playerName) {
        Member member = getMember(playerName);
        if (member == null) return null;
        Role previousRole = member.getRole();
        demotePlayer(sender, getLeader().getName());
        Role leaderRole = plugin.getRolesManager().getLeaderRole();
        member.setRole(leaderRole);
        log(Logging.PROMOTED.getLoggingMessage(sender, playerName));
        save();
        notifyUsers(Message.NOTIFY_PLAYER_PROMOTED, Message.NOTIFY_ACTIONBAR_PLAYER_PROMOTED,
                "player", playerName, "rank", leaderRole.getDisplayName());
        return new Role[]{previousRole, leaderRole};
    }

    public Role[] promotePlayer(CommandSender sender, String playerName) {
        Member member = getMember(playerName);
        if (member == null) return new Role[0];
        Role previousRole = member.getRole();
        Role nextRole = PrimalTribes.getPlugin().getRolesManager().getNextRole(previousRole);
        if (nextRole == null) return new Role[0];
        member.setRole(nextRole);
        log(Logging.PROMOTED.getLoggingMessage(sender, playerName));
        save();
        notifyUsers(Message.NOTIFY_PLAYER_PROMOTED, Message.NOTIFY_ACTIONBAR_PLAYER_PROMOTED,
                "player", playerName, "rank", nextRole.getDisplayName());
        return new Role[]{previousRole, nextRole};
    }

    public Role[] demotePlayer(CommandSender sender, String playerName) {
        Member member = getMember(playerName);
        if (member == null) return null;
        Role memberRole = member.getRole();
        Role newRole = PrimalTribes.getPlugin().getRolesManager().getPreviousRole(memberRole);
        if (newRole == null) return null;
        member.setRole(newRole);
        log(Logging.DEMOTED.getLoggingMessage(sender, playerName));
        save();
        notifyUsers(Message.NOTIFY_PLAYER_DEMOTED, Message.NOTIFY_ACTIONBAR_PLAYER_DEMOTED,
                "player", playerName, "rank", newRole.getDisplayName());
        return new Role[]{memberRole, newRole};
    }

    public Role[] setRank(CommandSender sender, String playerName, Role role) {
        Member member = getMember(playerName);
        if (member == null) return new Role[0];
        Role previousRole = member.getRole();
        member.setRole(role);
        log(Logging.PROMOTED.getLoggingMessage(sender, playerName));
        save();
        Message playerPromoted = role.getPriority() >= previousRole.getPriority() ?
                Message.NOTIFY_PLAYER_PROMOTED : Message.NOTIFY_PLAYER_DEMOTED;
        Message actionbarPlayerPromoted = role.getPriority() >= previousRole.getPriority() ?
                Message.NOTIFY_ACTIONBAR_PLAYER_PROMOTED : Message.NOTIFY_ACTIONBAR_PLAYER_DEMOTED;
        notifyUsers(playerPromoted, actionbarPlayerPromoted,
                "player", playerName, "rank", role.getDisplayName());
        return new Role[]{previousRole, role};
    }

    public Member getMember(Player player) {
        return getMember(player.getUniqueId());
    }

    public Member getMember(String name) {
        return members.stream().filter(m -> m.getName().equalsIgnoreCase(name)).findAny().orElse(null);
    }

    public Member getMember(UUID uuid) {
        return members.stream().filter(m -> m.getUuid().equals(uuid)).findAny().orElse(null);
    }

    public boolean isPlayerMember(Player player) {
        return getMember(player) != null;
    }

    public void addMember(Player player) {
        Member member = getMember(player);
        if (member != null) return;
        unInvitePlayer(null, player.getName());
        log(Logging.JOINED.getLoggingMessage(player.getName()));
        members.add(new Member(player, plugin.getRolesManager().getDefaultRole()));
        save();
        notifyUsers(Message.NOTIFY_JOINED, Message.NOTIFY_ACTIONBAR_JOINED, "player", player.getName());
        recentlyInvitedPlayers.put(player.getName(),
                Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () ->
                                recentlyInvitedPlayers.remove(player.getName()),
                        ConfigOption.INVITES_TIMEOUT.getInt() * 20L));
    }

    public void kickMember(CommandSender sender, String playerName) {
        Member member = getMember(playerName);
        if (member == null) return;
        log(Logging.KICKED.getLoggingMessage(sender.getName(), member.getName()));
        removeMember(playerName, true);
        notifyUsers(Message.NOTIFY_KICKED, Message.NOTIFY_ACTIONBAR_KICKED,
                "issuer", sender.getName(), "player", playerName);
    }

    public void leaveMember(Player player) {
        removeMember(player, true);
        notifyUsers(Message.NOTIFY_LEAVE, Message.NOTIFY_ACTIONBAR_LEAVE, "player", player.getName());
    }

    public void removeMember(Player player, boolean warn) {
        removeMember(player.getUniqueId(), warn);
    }

    public void removeMember(UUID uuid, boolean warn) {
        removeMember(getMember(uuid), warn);
    }

    public void removeMember(String name, boolean warn) {
        removeMember(getMember(name), warn);
    }

    public void removeMember(Member member, boolean warn) {
        if (member == null) return;
        if (warn) log(Logging.LEFT.getLoggingMessage(member.getName()));
        ClanPlayer clanPlayer = plugin.getClanPlayersManager().getClanPlayer(member.getUuid());
        if (clanPlayer != null) clanPlayer.removePlayerFromClan();
        members.remove(member);
        Warned warned = getWarned(member.getUuid());
        if (warned != null) warns.remove(warned);
        save();
    }

    public List<Player> getOnlineMembers() {
        return getMembers().stream()
                .map(m -> Bukkit.getPlayer(m.getUuid()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<Member> getMembers() {
        return members;
    }

    public User getBanned(Player player) {
        return getBanned(player.getUniqueId());
    }

    public User getBanned(UUID uuid) {
        return bans.stream().filter(m -> m.getUuid().equals(uuid)).findAny().orElse(null);
    }

    public User getBanned(String playerName) {
        return bans.stream().filter(m -> m.getName().equalsIgnoreCase(playerName)).findAny().orElse(null);
    }

    public boolean isPlayerBanned(Player player) {
        return getBanned(player) != null;
    }

    public boolean isPlayerBanned(String playerName) {
        return getBanned(playerName) != null;
    }

    public void banPlayer(CommandSender sender, String playerName) {
        User user = getBanned(playerName);
        if (user != null) return;
        log(Logging.BANNED.getLoggingMessage(sender, playerName));
        UUID uuid = Stream.concat(
                Arrays.stream(Bukkit.getOfflinePlayers()).map(p -> new User(p.getUniqueId().toString(), p.getName())),
                        getMembers().stream().map(p -> new User(p.getUuid().toString(), p.getName())))
                .distinct()
                .filter(p -> p.getName().equalsIgnoreCase(playerName))
                .map(User::getUuid).findAny().orElse(null);
        if (uuid == null) return;
        if (getMember(playerName) != null) removeMember(uuid, false);
        bans.add(new User(uuid.toString(), playerName));
        save();
        notifyUsers(Message.NOTIFY_BANNED, Message.NOTIFY_ACTIONBAR_BANNED,
                "issuer", sender.getName(), "player", playerName);
    }

    public void unbanPlayer(CommandSender sender, String playerName) {
        User member = getBanned(playerName);
        if (member == null) return;
        log(Logging.UNBANNED.getLoggingMessage(sender, playerName));
        bans.remove(member);
        save();
        notifyUsers(Message.NOTIFY_UNBANNED, Message.NOTIFY_ACTIONBAR_UNBANNED,
                "issuer", sender.getName(), "player", playerName);
    }

    public List<User> getBans() {
        return bans;
    }

    public int getWarnsLimit() {
        return warnsLimit;
    }

    public void setWarnsKick(CommandSender sender, int warnsKick) {
        this.warnsLimit = warnsKick;
        log(Logging.SET_WARNS_LIMIT.getLoggingMessage(sender, String.valueOf(warnsKick)));
        save();
        notifyUsers(Message.NOTIFY_WARNS_LIMIT, Message.NOTIFY_ACTIONBAR_WARNS_LIMIT, "warns", String.valueOf(warnsKick));
    }

    public Warned getWarned(Player player) {
        return getWarned(player.getUniqueId());
    }

    public Warned getWarned(UUID uuid) {
        return warns.stream().filter(m -> m.getUuid().equals(uuid)).findAny().orElse(null);
    }

    public Warned getWarned(String playerName) {
        return warns.stream().filter(m -> m.getName().equalsIgnoreCase(playerName)).findAny().orElse(null);
    }

    public int getPlayerWarns(Player player) {
        Warned warned = getWarned(player);
        return warned == null ? 0 : warned.getWarns();
    }

    public void warnPlayer(CommandSender sender, String playerName, int expire, boolean isAdminCommand) {
        Warned warned = getWarned(playerName);
        Player player = Bukkit.getPlayer(playerName);
        if (warned == null) {
            if (player == null) return;
            warned = new Warned(player);
        } else warns.remove(warned);
        log(Logging.WARNED.getLoggingMessage(sender, playerName));
        warned.addWarn(expire);
        warns.add(warned);
        save();
        playerName = warned.getName();
        if (warned.getWarns() > warnsLimit) {
            if (player != null)
                player.sendMessage(Message.WARN_LIMIT_EXCEEDED.getMessage(!isAdminCommand, isAdminCommand));
            sender.sendMessage(Message.PLAYER_WARN_KICKED.getMessage(!isAdminCommand, isAdminCommand)
                    .replace("%player%", playerName));
            Player target = Bukkit.getPlayer(playerName);
            if (target != null)
                target.sendMessage(Message.KICKED.getMessage(false, false)
                        .replace("%clan%", displayName));
            kickMember(sender, playerName);
            return;
        }
        if (player != null)
            player.sendMessage(Message.WARNED.getMessage(!isAdminCommand, isAdminCommand)
                .replace("%player%", sender.getName())
                .replace("%time%", TimeUtil.getTime(expire)));
        sender.sendMessage(Message.PLAYER_WARNED.getMessage(!isAdminCommand, isAdminCommand)
                .replace("%player%", playerName));
        notifyUsers(Message.NOTIFY_WARN, Message.NOTIFY_ACTIONBAR_WARN,
                "issuer", sender.getName(), "player", playerName);
    }

    public void unWarnPlayer(CommandSender sender, String playerName, boolean isAdminCommand) {
        Warned warned = getWarned(playerName);
        Player player = Bukkit.getPlayer(playerName);
        if (warned == null) return;
        log(Logging.UNWARNED.getLoggingMessage(sender, playerName));
        warned.clearWarns();
        save();
        playerName = warned.getName();
        if (player != null)
            player.sendMessage(Message.UNWARNED.getMessage(!isAdminCommand, isAdminCommand)
                    .replace("%player%", sender.getName()));
        sender.sendMessage(Message.PLAYER_UNWARNED.getMessage(!isAdminCommand, isAdminCommand)
                .replace("%player%", playerName));
        notifyUsers(Message.NOTIFY_UNWARN, Message.NOTIFY_ACTIONBAR_UNWARN,
                "issuer", sender.getName(), "player", playerName);
    }

    public List<Warned> getWarns() {
        return warns;
    }

    public void open(CommandSender sender) {
        log(Logging.OPENED_CLAN.getLoggingMessage(sender.getName()));
        this.opened = true;
        save();
        notifyUsers(Message.NOTIFY_CLAN_OPENED, Message.NOTIFY_ACTIONBAR_CLAN_OPENED);
    }

    public void close(CommandSender sender) {
        log(Logging.CLOSED_CLAN.getLoggingMessage(sender.getName()));
        this.opened = false;
        save();
        notifyUsers(Message.NOTIFY_CLAN_CLOSED, Message.NOTIFY_ACTIONBAR_CLAN_CLOSED);
    }

    public boolean isOpened() {
        return opened;
    }

    public void enableInvites(CommandSender sender) {
        log(Logging.TOGGLE_INVITES.getLoggingMessage(sender.getName(), Logging.ENABLED.getLoggingMessage()));
        this.invitesEnabled = true;
        save();
        notifyUsers(Message.NOTIFY_CLAN_INVITES, Message.NOTIFY_ACTIONBAR_CLAN_INVITES,
                "issuer", sender.getName(), "status", Message.ENABLED_MALE_PLURAL.getMessage(false, false));
    }

    public void disableInvites(CommandSender sender) {
        log(Logging.TOGGLE_INVITES.getLoggingMessage(sender.getName(), Logging.DISABLED.getLoggingMessage()));
        this.invitesEnabled = false;
        save();
        notifyUsers(Message.NOTIFY_CLAN_INVITES, Message.NOTIFY_ACTIONBAR_CLAN_INVITES,
                "issuer", sender.getName(), "status", Message.DISABLED_MALE_PLURAL.getMessage(false, false));
    }

    public boolean areInvitesEnabled() {
        return invitesEnabled;
    }

    public void muteClanChat(Message status, CommandSender sender) {
        log(Logging.CLAN_MUTED.getLoggingMessage(sender.getName()));
        this.clanChatMuted = true;
        save();
        notifyUsers(Message.NOTIFY_CLAN_CHAT, Message.NOTIFY_ACTIONBAR_CLAN_CHAT,
                "status", status.getMessage(false, false));
    }

    public void unMuteClanChat(Message status, CommandSender sender) {
        log(Logging.CLAN_UNMUTED.getLoggingMessage(sender.getName()));
        this.clanChatMuted = false;
        save();
        notifyUsers(Message.NOTIFY_CLAN_CHAT, Message.NOTIFY_ACTIONBAR_CLAN_CHAT,
                "status", status.getMessage(false, false));
    }

    public boolean isClanChatMuted() {
        return clanChatMuted;
    }

    public void swapFriendlyFire(CommandSender sender, Message status) {
        log(Logging.CHANGED_FRIENDLY_FIRE.getLoggingMessage(sender, friendlyFire ?
                Logging.ENABLED.getLoggingMessage() : Logging.DISABLED.getLoggingMessage()));
        friendlyFire = !friendlyFire;
        save();
        notifyUsers(Message.NOTIFY_FRIENDLY_FIRE, Message.NOTIFY_ACTIONBAR_FRIENDLY_FIRE,
                "status", status.getMessage(false, false));
    }

    public boolean isFriendlyFire() {
        return friendlyFire;
    }

    public List<Clan> getAllies() {
        ClansManager clansManager = PrimalTribes.getPlugin().getClansManager();
        allies.removeIf(n -> clansManager.getClan(n) == null);
        return allies.stream().map(clansManager::getClan).collect(Collectors.toList());
    }

    public List<Clan> getAllyRequests() {
        return allyRequests.stream().map(AllyRequest::getClan).collect(Collectors.toList());
    }

    public AllyRequest getAllyRequest(Clan clan) {
        if (clan == null) return null;
        return allyRequests.stream().filter(c -> clan.equals(c.getClan())).findAny().orElse(null);
    }

    public boolean isAlly(Clan clan) {
        return getAllies().contains(clan);
    }

    public boolean hasBeenAskedForAllyFrom(Clan clan) {
        return getAllyRequest(clan) != null;
    }

    public boolean wasRecentlyInvited(Clan clan) {
        return recentlyInvitedAllies.containsKey(clan.getName());
    }

    public void askAlly(CommandSender sender, Clan clan) {
        if (clan.hasBeenAskedForAllyFrom(this)) return;
        log(Logging.ASK_ALLY.getLoggingMessage(sender, clan.getName()));
        clan.addAllyRequest(this);
        notifyUsers(Message.NOTIFY_ALLY_ASK, Message.NOTIFY_ACTIONBAR_ALLY_ASK,
                "clan", clan.getDisplayName());
    }

    public void unAskAlly(CommandSender sender, Clan clan) {
        if (!clan.hasBeenAskedForAllyFrom(this)) return;
        log(Logging.UN_ASK_ALLY.getLoggingMessage(sender, clan.getName()));
        clan.removeAllyRequest(this);
        notifyUsers(Message.NOTIFY_ALLY_UN_ASK, Message.NOTIFY_ACTIONBAR_ALLY_UN_ASK,
                "clan", clan.getDisplayName());
        recentlyInvitedAllies.put(clan.getName(),
                Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () ->
                                recentlyInvitedAllies.remove(clan.getName()),
                        ConfigOption.ALLY_REQUESTS_TIMEOUT.getInt() * 20L));
    }

    public void addAllyRequest(Clan clan) {
        log(Logging.ALLY_REQUEST.getLoggingMessage(clan.getName()));
        int allyTimeout = ConfigOption.ALLY_REQUESTS_TIMEOUT.getInt();
        AllyRequest allyRequest = new AllyRequest(clan.getName(),
                Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                    AllyRequest a = getAllyRequest(clan);
                    if (a != null) allyRequests.remove(a);
                }, 20L * allyTimeout));
        allyRequests.add(allyRequest);
        notifyUsers(Message.NOTIFY_ALLY_REQUEST, Message.NOTIFY_ACTIONBAR_ALLY_REQUEST,
                "clan", clan.getDisplayName());
        notifyLeader(Message.CLAN_ALLY_REQUEST.getMessage(true, false)
                .replace("%clan-name%", clan.getDisplayName())
                .replace("%time%", TimeUtil.getTime(allyTimeout)));
    }

    public void declineAllyRequest(Clan clan) {
        AllyRequest allyRequest = getAllyRequest(clan);
        if (allyRequest == null) return;
        log(Logging.ALLY_REQUEST_DECLINED.getLoggingMessage(clan.getName()));
        allyRequest.cancelTask();
        allyRequests.remove(allyRequest);
        notifyUsers(Message.NOTIFY_ALLY_REQUEST_DECLINED,
                Message.NOTIFY_ACTIONBAR_ALLY_REQUEST_DECLINED,
                "clan", clan.getDisplayName());
        clan.notifyUsers(Message.NOTIFY_ALLY_CLAN_REQUEST_DECLINED,
                Message.NOTIFY_ACTIONBAR_ALLY_CLAN_REQUEST_DECLINED,
                "clan", getDisplayName());
        recentlyInvitedAllies.put(clan.getName(),
                Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () ->
                                recentlyInvitedAllies.remove(clan.getName()),
                        ConfigOption.ALLY_REQUESTS_TIMEOUT.getInt() * 20L));
    }

    public void removeAllyRequest(Clan clan) {
        AllyRequest allyRequest = getAllyRequest(clan);
        if (allyRequest == null) return;
        log(Logging.ALLY_REQUEST_REMOVED.getLoggingMessage(clan.getName()));
        allyRequest.cancelTask();
        allyRequests.remove(allyRequest);
        notifyUsers(Message.NOTIFY_ALLY_REQUEST_REMOVED, Message.NOTIFY_ACTIONBAR_ALLY_REQUEST_REMOVED,
                "clan", clan.getDisplayName());
        notifyLeader(Message.CLAN_ALLY_REQUEST_REMOVED.getMessage(true, false)
                .replace("%clan-name%", clan.getDisplayName()));
        recentlyInvitedAllies.put(clan.getName(),
                Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () ->
                                recentlyInvitedAllies.remove(clan.getName()),
                        ConfigOption.ALLY_REQUESTS_TIMEOUT.getInt() * 20L));
    }

    public void addAlly(Clan clan) {
        if (clan == null) return;
        if (isAlly(clan)) return;
        AllyRequest allyRequest = getAllyRequest(clan);
        log(Logging.ALLY_REQUEST_ACCEPTED.getLoggingMessage(clan.getName()));
        if (allyRequest != null) {
            allyRequest.cancelTask();
            allyRequests.remove(allyRequest);
        }
        notifyUsers(Message.NOTIFY_ADDED_ALLY, Message.NOTIFY_ACTIONBAR_ADDED_ALLY,
                "clan", clan.getDisplayName());
        notifyLeader(Message.CLAN_NEW_ALLY.getMessage(true, false)
                .replace("%clan%", clan.getDisplayName()));
        allies.add(clan.getName());
        save();
        clan.addAlly(this);
    }

    public void unAlly(CommandSender sender, Clan clan) {
        if (clan == null) return;
        if (!isAlly(clan)) return;
        AllyRequest allyRequest = getAllyRequest(clan);
        if (allyRequest != null) {
            allyRequest.cancelTask();
            allyRequests.remove(allyRequest);
        }
        notifyUsers(Message.NOTIFY_REMOVED_ALLY, Message.NOTIFY_ACTIONBAR_REMOVED_ALLY,
                "issuer", sender.getName(),
                "clan", clan.getDisplayName());
        if (sender instanceof Player && !((Player) sender).getUniqueId().equals(getLeader().getUuid()))
            notifyLeader(Message.CLAN_REMOVED_ALLY.getMessage(true, false)
                .replace("%issuer%", sender.getName())
                .replace("%clan%", clan.getDisplayName()));
        allies.remove(clan.getName());
        save();
        clan.unAlly(sender, this);
    }

    public void notifyUsers(Message notifyMessage, Message notifyActionBar, String... strings) {
        List<Player> onlineMembers = getOnlineMembers();
        String message = notifyMessage.getMessage(false, false)
                .replace("%clan-name%", getDisplayName())
                .replace("%clan-tag%", getTag());
        for (int i = 0; i < strings.length; i += 2)
            message = message.replace("%" + strings[i] + "%", strings[i + 1]);
        if (!message.equalsIgnoreCase(""))
            for (Player p : onlineMembers) p.sendMessage(message);
        String actionBarMessage = notifyActionBar.getMessage(false, false)
                .replace("%clan-name%", getDisplayName())
                .replace("%clan-tag%", getTag());
        for (int i = 0; i < strings.length; i += 2)
            actionBarMessage = actionBarMessage.replace("%" + strings[i] + "%", strings[i + 1]);
        //TODO: FINISH ME!
        //if (!actionBarMessage.equalsIgnoreCase(""))
            //ActionBarUtil.broadcastActionBar(onlineMembers, actionBarMessage);
    }

    public void notifyLeader(String message) {
        members.stream()
                .filter(m -> m.getRole().isLeaderRole())
                .map(m -> Bukkit.getPlayer(m.getUuid()))
                .filter(Objects::nonNull)
                .findAny().ifPresent(p -> p.sendMessage(message));
    }

    public void update(Player player) {
        String playerName = player.getName();
        Member member = getMember(player);
        if (member != null && !member.getName().equalsIgnoreCase(playerName))
            member.updateName(playerName);
        User banned = getBanned(player);
        if (banned != null && !banned.getName().equalsIgnoreCase(playerName))
            banned.updateName(playerName);
        Warned warned = getWarned(player);
        if (warned != null && !warned.getName().equalsIgnoreCase(playerName))
            warned.updateName(playerName);
        save();
    }

    public void quit() {
        if (invites != null) invites.forEach(Invite::cancelTask);
        if (allyRequests != null) allyRequests.forEach(AllyRequest::cancelTask);
        if (recentlyInvitedPlayers != null) recentlyInvitedPlayers.values().forEach(BukkitTask::cancel);
        if (recentlyInvitedAllies != null) recentlyInvitedAllies.values().forEach(BukkitTask::cancel);
        playerSprays.values().stream().filter(Objects::nonNull).forEach(BukkitTask::cancel);
        save();
    }

    public void log(String logMessage) {
        if (logMessage.replace(" ", "").equalsIgnoreCase("")) return;
        logsConfiguration.set(loggingFormat.format(new Date()), logMessage);
    }

    private void checkLogs() {
        Set<Map.Entry<String, Object>> values = logsConfiguration.getValues(false).entrySet();
        values.stream().map(Map.Entry::getKey).forEach(k -> logsConfiguration.set(k, null));

        values = values.stream()
                .filter(entry -> getDate(entry.getKey()) != null)
                .filter(entry -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(getDate(entry.getKey()));
                    calendar.add(Calendar.DAY_OF_YEAR, ConfigOption.LOGGING_DAYS.getInt());
                    return calendar.after(Calendar.getInstance());
                })
                .sorted(Comparator.comparing(entry -> getDate(entry.getKey())))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        values.forEach(entry -> logsConfiguration.set(entry.getKey(), entry.getValue()));

        try {
            ConfigUtils.saveConfig(logsConfiguration, logsFile);
        } catch (IOException e) {
            PrimalTribes.logWarning(BearLoggingMessage.GENERAL_ERROR_OCCURRED.getMessage(
                    "%task%", String.format("saving log files for clan %s", getName()),
                    "%error%", e.getMessage()));
        }
    }

    private String parseColorString(String string) {
        string = ChatColor.translateAlternateColorCodes('&', HexUtils.parseString(string));
        for (ChatColor c : ConfigOption.INVALID_COLORS.getColorCodes())
            string = string.replace(String.valueOf(c), "");
        return string;
    }

    private Date getDate(String stringDate) {
        try {
            return loggingFormat.parse(stringDate);
        } catch (ParseException e) {
            return null;
        }
    }

    public FileConfiguration getInfoConfiguration() {
        return infoConfiguration;
    }

    public void save() {
        SaveClanEvent clanEvent = new SaveClanEvent(this);
        try {
            Bukkit.getPluginManager().callEvent(clanEvent);
            if (clanEvent.isCancelled()) return;
        } catch (IllegalStateException ignored) {}
        infoConfiguration.set("name", name);
        infoConfiguration.set("tag", tag);
        infoConfiguration.set("display-name", displayName);
        infoConfiguration.set("description", description);
        infoConfiguration.set("motd", motd);
        infoConfiguration.set("telegram", telegram);
        infoConfiguration.set("discord", discord);
        infoConfiguration.set("opened", opened);
        infoConfiguration.set("clan-chat-muted", clanChatMuted);
        infoConfiguration.set("friendly-fire", friendlyFire);
        infoConfiguration.set("invites-enabled", invitesEnabled);
        infoConfiguration.set("warns-limit", warnsLimit);

        infoConfiguration.set("members", null);
        ConfigurationSection membersSection = infoConfiguration.createSection("members");
        members.forEach(member -> {
            ConfigurationSection memberSection = membersSection.createSection(member.getUuid().toString());
            memberSection.set("name", member.getName());
            memberSection.set("rank", member.getRole().getName());
        });

        infoConfiguration.set("bans", null);
        ConfigurationSection bansSection = infoConfiguration.createSection("bans");
        bans.forEach(banned -> {
            ConfigurationSection banSection = bansSection.createSection(banned.getUuid().toString());
            banSection.set("name", banned.getName());
        });

        infoConfiguration.set("warns", null);
        ConfigurationSection warnsSection = infoConfiguration.createSection("warns");
        warns.forEach(warned -> {
            ConfigurationSection warnedSection = warnsSection.createSection(warned.getUuid().toString());
            warnedSection.set("name", warned.getName());
            ConfigurationSection warnSection = warnedSection.createSection("warns");
            List<Warn> warnsList = warned.getWarnsList();
            for (int i = 0; i < warnsList.size(); i++) {
                Warn w = warnsList.get(i);
                ConfigurationSection wSection = warnSection.createSection(String.valueOf(i));
                wSection.set("date", w.getStringDate());
                wSection.set("expire", w.getExpireInt());
            }
        });

        infoConfiguration.set("security.public-key", Base64Util.encodeBase64(publicKey.getEncoded()));
        infoConfiguration.set("security.private-key", Base64Util.encodeBase64(privateKey.getEncoded()));

        infoConfiguration.set("allies", allies);

        checkLogs();
    }

    public void destroy() {
        destroy(true);
    }

    public void destroy(boolean notify) {
        if (notify) notifyUsers(Message.NOTIFY_CLAN_DISBAND, Message.NOTIFY_ACTIONBAR_CLAN_DISBAND);
        getMembers().stream()
                .map(m -> Bukkit.getPlayer(m.getUuid()))
                .filter(Objects::nonNull)
                .map(p -> plugin.getClanPlayersManager().getClanPlayer(p))
                .forEach(ClanPlayer::removePlayerFromClan);
        quit();
        try {
            FileUtils.deleteFolder(clanFolder);
        } catch (IOException e) {
            PrimalTribes.logWarning(e.getMessage());
        }
    }
}