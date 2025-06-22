package it.fulminazzo.primaltribes.Enums;

import it.angrybear.Enums.BearConfigOption;
import it.fulminazzo.primaltribes.PrimalTribes;

import java.util.Arrays;

public class ConfigOption extends BearConfigOption {
    public static final ConfigOption COMMAND_NAME = new ConfigOption("command-name");
    public static final ConfigOption BANNED_WORDS = new ConfigOption("banned-words");
    public static final ConfigOption INVALID_COLORS = new ConfigOption("invalid-colors");
    public static final ConfigOption MAX_WARN_EXPIRE = new ConfigOption("max-warn-expire");
    public static final ConfigOption MAX_SIZE_NAME = new ConfigOption("max-size-name");
    public static final ConfigOption MIN_SIZE_NAME = new ConfigOption("min-size-name");
    public static final ConfigOption MAX_SIZE_TAG = new ConfigOption("max-size-tag");
    public static final ConfigOption MIN_SIZE_TAG = new ConfigOption("min-size-tag");
    public static final ConfigOption MAXIMUM_WARNS = new ConfigOption("maximum-warns");
    public static final ConfigOption MEMBERS_PER_CLAN = new ConfigOption("members-per-clan");
    public static final ConfigOption INVITES_TIMEOUT = new ConfigOption("invites-timeout");
    public static final ConfigOption ALLY_REQUESTS_TIMEOUT = new ConfigOption("ally-requests-timeout");
    public static final ConfigOption LOGGING_DAYS = new ConfigOption("logging-days");
    public static final ConfigOption TELEGRAM_FORMAT = new ConfigOption("telegram-format");
    public static final ConfigOption DISCORD_FORMAT = new ConfigOption("discord-format");

    public ConfigOption(String permission) {
        super(PrimalTribes.getPlugin(), permission);
    }

    public CommandName getCommandName() {
        String commandName = getString();
        if (commandName == null) return null;
        return Arrays.stream(CommandName.values()).filter(c -> c.name().equalsIgnoreCase(commandName)).findAny().orElse(null);
    }
}
