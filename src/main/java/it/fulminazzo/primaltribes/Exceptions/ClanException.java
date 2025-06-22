package it.fulminazzo.primaltribes.Exceptions;

public class ClanException extends Exception {
    public ClanException(String clanName) {
        super(String.format("Clan %s files were missing or corrupted. Please try to check your YAML files and try again.", clanName));
    }
}
