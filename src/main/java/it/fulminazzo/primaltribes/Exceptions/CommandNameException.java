package it.fulminazzo.primaltribes.Exceptions;

import it.fulminazzo.primaltribes.Enums.ConfigOption;

public class CommandNameException extends Exception {
    public CommandNameException() {
        super(String.format("Command name %s not valid! Please check your config file.", ConfigOption.COMMAND_NAME.getString()));
    }
}
