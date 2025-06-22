package it.fulminazzo.primaltribes.Enums;

import it.angrybear.Enums.BearPermission;
import it.fulminazzo.primaltribes.PrimalTribes;

public class Permissions extends BearPermission {
    public static final Permissions MOTD = new Permissions("motd");
    public static final Permissions MOTD_SET = new Permissions("motd.set");
    public static final Permissions MOTD_UNSET = new Permissions("motd.unset");
    public static final Permissions SET_DISPLAYNAME = new Permissions("set-displayname");
    public static final Permissions UNSET_DISPLAYNAME = new Permissions("unset-displayname");
    public static final Permissions SET_TAG = new Permissions("set-tag");
    public static final Permissions UNSET_TAG = new Permissions("unset-tag");
    public static final Permissions CREATE = new Permissions("create");
    public static final Permissions CREATE_BYPASS = new Permissions("create.bypass");
    public static final Permissions CREATE_ADMIN = new Permissions("create");
    public static final Permissions CHAT = new Permissions("chat");
    public static final Permissions CHAT_COLORED = new Permissions("chat.colored");
    public static final Permissions MUTE_CHAT = new Permissions("chat.mute");
    public static final Permissions SILENCE_CHAT = new Permissions("chat.silence");
    public static final Permissions TOGGLE_FRIENDLY_FIRE = new Permissions("toggle-friendly-fire");
    public static final Permissions DESCRIPTION = new Permissions("description");
    public static final Permissions DESCRIPTION_SET = new Permissions("description.set");
    public static final Permissions DESCRIPTION_UNSET = new Permissions("description.unset");
    public static final Permissions TELEGRAM = new Permissions("telegram");
    public static final Permissions TELEGRAM_SET = new Permissions("telegram.set");
    public static final Permissions TELEGRAM_UNSET = new Permissions("telegram.unset");
    public static final Permissions DISCORD = new Permissions("discord");
    public static final Permissions DISCORD_SET = new Permissions("discord.set");
    public static final Permissions DISCORD_UNSET = new Permissions("discord.unset");
    public static final Permissions INVITE = new Permissions("invite");
    public static final Permissions UNINVITE = new Permissions("uninvite");
    public static final Permissions KICK = new Permissions("kick");
    public static final Permissions BAN = new Permissions("ban");
    public static final Permissions UNBAN = new Permissions("unban");
    public static final Permissions JOIN = new Permissions("join");
    public static final Permissions OPEN = new Permissions("open");
    public static final Permissions CLOSE = new Permissions("close");
    public static final Permissions DISBAND = new Permissions("disband");
    public static final Permissions LEAVE = new Permissions("leave");
    public static final Permissions SET_LEADER = new Permissions("set-leader");
    public static final Permissions PROMOTE = new Permissions("promote");
    public static final Permissions DEMOTE = new Permissions("demote");
    public static final Permissions SET_WARNS_LIMIT = new Permissions("set-warns-limit");
    public static final Permissions WARN = new Permissions("warn");
    public static final Permissions UNWARN = new Permissions("unwarn");
    public static final Permissions VAULT = new Permissions("vault");
    public static final Permissions DEPOSIT_ITEM = new Permissions("deposit-item");
    public static final Permissions TOGGLE_INVITES = new Permissions("toggle-invites");
    public static final Permissions DECLINE = new Permissions("decline");
    public static final Permissions SET_SPRAY = new Permissions("set-spray");
    public static final Permissions RESET_SPRAY = new Permissions("reset-spray");
    public static final Permissions SPRAY = new Permissions("spray");
    public static final Permissions ALLY = new Permissions("ally");
    public static final Permissions ALLY_INVITE = new Permissions("ally.invite");
    public static final Permissions ALLY_UNINVITE = new Permissions("ally.uninvite");
    public static final Permissions ALLY_ACCEPT = new Permissions("ally.accept");
    public static final Permissions ALLY_DECLINE = new Permissions("ally.decline");
    public static final Permissions ALLY_CHAT = new Permissions("ally-chat");
    public static final Permissions ALLY_CHAT_COLORED = new Permissions("ally-chat.colored");
    public static final Permissions UNALLY = new Permissions("unally");
    public static final Permissions ALLIES = new Permissions("allies");
    public static final Permissions MEMBERS = new Permissions("members");
    public static final Permissions INFO = new Permissions("info");
    public static final Permissions SET_RANK = new Permissions("set-rank");
    public static final Permissions ADD = new Permissions("add");
    public static final Permissions ADD_BYPASS_BAN = new Permissions("add.bypass.ban");
    public static final Permissions ADD_BYPASS_MEMBERS = new Permissions("add.bypass.members");
    public static final Permissions RELOAD = new Permissions("reload");
    public static final Permissions SOCIAL_SPY = new Permissions("social-spy");
    public static final Permissions HELP = new Permissions("help");

    public Permissions(String permission) {
        super(permission);
    }

    public String getPermission(boolean isAdminCommand) {
        return PrimalTribes.getPlugin().getName().toLowerCase() + ".clan" + (isAdminCommand ? "a" : "") + "." + this.permission;
    }

    @Override
    public String getPermission() {
        return getPermission(false);
    }
}