package it.fulminazzo.primaltribes.Enums;

import it.angrybear.Enums.ClassEnum;

public class ClanPermission extends ClassEnum {
    public static final ClanPermission ALL = new ClanPermission();
    public static final ClanPermission MOTD = new ClanPermission();
    public static final ClanPermission MOTD_SET = new ClanPermission();
    public static final ClanPermission MOTD_UNSET = new ClanPermission();
    public static final ClanPermission SET_DISPLAYNAME = new ClanPermission();
    public static final ClanPermission UNSET_DISPLAYNAME = new ClanPermission();
    public static final ClanPermission SET_TAG = new ClanPermission();
    public static final ClanPermission UNSET_TAG = new ClanPermission();
    public static final ClanPermission CHAT = new ClanPermission();
    public static final ClanPermission MUTE_CHAT = new ClanPermission();
    public static final ClanPermission BYPASS_MUTE_CHAT = new ClanPermission();
    public static final ClanPermission SILENCE_CHAT = new ClanPermission();
    public static final ClanPermission TOGGLE_FRIENDLY_FIRE = new ClanPermission();
    public static final ClanPermission TELEGRAM = new ClanPermission();
    public static final ClanPermission TELEGRAM_SET = new ClanPermission();
    public static final ClanPermission TELEGRAM_UNSET = new ClanPermission();
    public static final ClanPermission DISCORD = new ClanPermission();
    public static final ClanPermission DISCORD_SET = new ClanPermission();
    public static final ClanPermission DISCORD_UNSET = new ClanPermission();
    public static final ClanPermission INVITE = new ClanPermission();
    public static final ClanPermission UNINVITE = new ClanPermission();
    public static final ClanPermission KICK = new ClanPermission();
    public static final ClanPermission BAN = new ClanPermission();
    public static final ClanPermission UNBAN = new ClanPermission();
    public static final ClanPermission JOIN = new ClanPermission();
    public static final ClanPermission OPEN = new ClanPermission();
    public static final ClanPermission CLOSE = new ClanPermission();
    public static final ClanPermission DISBAND = new ClanPermission();
    public static final ClanPermission LEAVE = new ClanPermission();
    public static final ClanPermission SET_LEADER = new ClanPermission();
    public static final ClanPermission PROMOTE = new ClanPermission();
    public static final ClanPermission DEMOTE = new ClanPermission();
    public static final ClanPermission SET_WARNS_LIMIT = new ClanPermission();
    public static final ClanPermission WARN = new ClanPermission();
    public static final ClanPermission UNWARN = new ClanPermission();
    public static final ClanPermission UNWARN_SELF = new ClanPermission();
    public static final ClanPermission VAULT = new ClanPermission();
    public static final ClanPermission VAULT_MODIFY = new ClanPermission();
    public static final ClanPermission DEPOSIT_ITEM = new ClanPermission();
    public static final ClanPermission TOGGLE_INVITES = new ClanPermission();
    public static final ClanPermission DECLINE = new ClanPermission();
    public static final ClanPermission DESCRIPTION = new ClanPermission();
    public static final ClanPermission DESCRIPTION_SET = new ClanPermission();
    public static final ClanPermission DESCRIPTION_UNSET = new ClanPermission();
    public static final ClanPermission SET_SPRAY = new ClanPermission();
    public static final ClanPermission RESET_SPRAY = new ClanPermission();
    public static final ClanPermission SPRAY = new ClanPermission();
    public static final ClanPermission ALLY = new ClanPermission();
    public static final ClanPermission ALLY_INVITE = new ClanPermission();
    public static final ClanPermission ALLY_UNINVITE = new ClanPermission();
    public static final ClanPermission ALLY_ACCEPT = new ClanPermission();
    public static final ClanPermission ALLY_DECLINE = new ClanPermission();
    public static final ClanPermission ALLY_CHAT = new ClanPermission();
    public static final ClanPermission UNALLY = new ClanPermission();
    public static final ClanPermission ALLIES = new ClanPermission();
    public static final ClanPermission MEMBERS = new ClanPermission();
    public static final ClanPermission SET_RANK = new ClanPermission();
    public static final ClanPermission CHAT_COLORED = new ClanPermission();
    public static final ClanPermission HELP = new ClanPermission();

    public static ClanPermission valueOf(String name) {
        return valueOf(ClanPermission.class, name);
    }

    public static ClanPermission[] values() {
        return values(ClanPermission.class);
    }
}