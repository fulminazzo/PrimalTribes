package it.fulminazzo.primaltribes.Utils;

import it.fulminazzo.primaltribes.PrimalTribes;

import java.util.ArrayList;
import java.util.List;

public class StringUtils extends it.angrybear.Utils.StringUtils {

    public static String getCommandSyntax(String command, String... parameters) {
        if (command == null) return "";
        String cmd = String.format("&c/%s ", command);
        for (String parameter : parameters) {
            switch (parameter.toLowerCase()) {
                case "player": {
                    cmd = cmd.concat(formatSyntax("&cplayer"));
                    break;
                }
                case "money": {
                    cmd = cmd.concat(formatSyntax("&6money"));
                    break;
                }
                case "message": {
                    cmd = cmd.concat(formatSyntax("&7message"));
                    break;
                }
                case "clan": {
                    cmd = cmd.concat(formatSyntax("&4" + PrimalTribes.getCommandName()));
                    break;
                }
                case "subcommand": {
                    cmd = cmd.concat(formatSyntax("&9subcommand"));
                    break;
                }
                case "home": {
                    cmd = cmd.concat(formatSyntax("&3home"));
                    break;
                }
                case "number": {
                    cmd = cmd.concat(formatSyntax("&dnumber"));
                    break;
                }
                default: {
                    cmd = cmd.concat(formatSyntax("&e" + parameter));
                }
            }
            cmd = cmd.concat(" ");
        }
        return parseMessage(cmd);
    }

    private static String formatSyntax(String s) {
        return String.format("&8<%s&8>", s);
    }

    public static String partialFormat(String message, String s) {
        int form = message.indexOf("%s");
        if (form == -1) return message;
        else return message.substring(0, form) + s + message.substring(Math.min(form + 2, message.length()));
    }

    public static boolean validateString(String toValidate, String format) {
        List<String> validate = splitString(toValidate);
        List<String> formatList = splitString(format);
        if (validate.size() != formatList.size()) return false;
        for (int i = 0; i < validate.size(); i++) {
            String v = validate.get(i);
            String f = formatList.get(i);
            if (f.startsWith("%") && f.length() > 1) {
                switch (f) {
                    case "%d": {
                        if (!v.matches("\\d")) return false;
                        break;
                    }
                    case "%u": {
                        if (!v.matches("[A-Z]")) return false;
                        break;
                    }
                    case "%l": {
                        if (!v.matches("[a-z]")) return false;
                        break;
                    }
                    case "%s": {
                        if (v.matches("[a-zA-Z\\d]+")) return false;
                        break;
                    }
                    case "%c": {
                        if (!v.matches("[a-zA-Z]")) return false;
                        break;
                    }
                    case "%b": {
                        if (!v.matches("[a-zA-Z0-9]+")) return false;
                        break;
                    }
                    case "%a": {
                        break;
                    }
                }
            } else if (!v.equalsIgnoreCase(f)) return false;
        }
        return true;
    }

    private static List<String> splitString(String string) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (c == '%' && i != string.length() - 1)
                result.add(c + String.valueOf(string.charAt(++i)));
            else result.add(String.valueOf(c));
        }
        return result;
    }
}