package it.fulminazzo.primaltribes.Enums;

import it.fulminazzo.primaltribes.Utils.StringUtils;
import org.bukkit.command.CommandSender;

public class Logging {
    public static final Logging CREATED = new Logging(Message.LOGGING_CREATED);
    public static final Logging SET = new Logging(Message.LOGGING_SET);
    public static final Logging INVITE = new Logging(Message.LOGGING_INVITE);
    public static final Logging UNINVITE = new Logging(Message.LOGGING_UNINVITE);
    public static final Logging JOINED = new Logging(Message.LOGGING_JOINED);
    public static final Logging LEFT = new Logging(Message.LOGGING_LEFT);
    public static final Logging KICKED = new Logging(Message.LOGGING_KICKED);
    public static final Logging BANNED = new Logging(Message.LOGGING_BANNED);
    public static final Logging UNBANNED = new Logging(Message.LOGGING_UNBANNED);
    public static final Logging WARNED = new Logging(Message.LOGGING_WARNED);
    public static final Logging UNWARNED = new Logging(Message.LOGGING_UNWARNED);
    public static final Logging PROMOTED = new Logging(Message.LOGGING_PROMOTED);
    public static final Logging DEMOTED = new Logging(Message.LOGGING_DEMOTED);
    public static final Logging SET_WARNS_LIMIT = new Logging(Message.LOGGING_SET_WARNS_LIMIT);
    public static final Logging OPENED_CLAN = new Logging(Message.LOGGING_OPENED_CLAN);
    public static final Logging CLOSED_CLAN = new Logging(Message.LOGGING_CLOSED_CLAN);
    public static final Logging TOGGLE_INVITES = new Logging(Message.LOGGING_TOGGLE_INVITES);
    public static final Logging ENABLED = new Logging(Message.LOGGING_ENABLED);
    public static final Logging DISABLED = new Logging(Message.LOGGING_DISABLED);
    public static final Logging CLAN_MUTED = new Logging(Message.LOGGING_CLAN_MUTED);
    public static final Logging CLAN_UNMUTED = new Logging(Message.LOGGING_CLAN_UNMUTED);
    public static final Logging ASK_ALLY = new Logging(Message.LOGGING_ASK_ALLY);
    public static final Logging UN_ASK_ALLY = new Logging(Message.LOGGING_UN_ASK_ALLY);
    public static final Logging ALLY_REQUEST = new Logging(Message.LOGGING_ALLY_REQUEST);
    public static final Logging ALLY_REQUEST_ACCEPTED = new Logging(Message.LOGGING_ALLY_REQUEST_ACCEPTED);
    public static final Logging ALLY_REQUEST_DECLINED = new Logging(Message.LOGGING_ALLY_REQUEST_DECLINED);
    public static final Logging ALLY_REQUEST_REMOVED = new Logging(Message.LOGGING_ALLY_REQUEST_REMOVED);
    public static final Logging CHANGED_FRIENDLY_FIRE = new Logging(Message.LOGGING_CHANGED_FRIENDLY_FIRE);

    public final Message loggingMessage;

    public Logging(Message loggingMessage) {
        this.loggingMessage = loggingMessage;
    }

    public String getLoggingMessage(String... values) {
        String message = loggingMessage.getMessage(false, false);
        for (String value : values) message = StringUtils.partialFormat(message, value);
        return message;
    }

    public String getLoggingMessage(CommandSender issuer, String... values) {
        String message = StringUtils.partialFormat(loggingMessage.getMessage(false, false), issuer.getName());
        for (String value : values) message = StringUtils.partialFormat(message, value);
        return message;
    }
}