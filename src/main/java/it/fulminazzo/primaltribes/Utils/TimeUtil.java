package it.fulminazzo.primaltribes.Utils;

import it.fulminazzo.primaltribes.Enums.Message;

public class TimeUtil {
    public static String getTime(long sec) {
        int seconds = (int) sec;
        String timeMessage = "";
        timeMessage = parseTimeString(timeMessage, seconds / 3600, Message.HOURS, Message.HOUR);
        timeMessage = parseTimeString(timeMessage, seconds % 3600 / 60, Message.MINUTES, Message.MINUTE);
        timeMessage = parseTimeString(timeMessage, seconds % 3600 % 60, Message.SECONDS, Message.SECOND);
        return timeMessage;
    }

    private static String parseTimeString(String timeMessage, int time, Message plural, Message singular) {
        if (time == 0) return timeMessage;
        if (!timeMessage.equalsIgnoreCase(""))
            timeMessage = timeMessage.concat("&8, ");
        timeMessage = timeMessage.concat(Message.TIME.getMessage(false, false)
                .replace("%time%", String.valueOf(time))
                .replace("%unit%", time > 1 ?
                        plural.getMessage(false, false) :
                        singular.getMessage(false, false)));
        return timeMessage;
    }
}