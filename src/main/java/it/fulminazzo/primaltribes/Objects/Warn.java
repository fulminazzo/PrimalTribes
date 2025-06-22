package it.fulminazzo.primaltribes.Objects;

import it.fulminazzo.primaltribes.PrimalTribes;
import org.bukkit.configuration.ConfigurationSection;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Warn {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
    private final Date date;
    private final int expire;

    public Warn(Date date, int expire) {
        this.date = date;
        this.expire = expire;
    }

    public Warn(ConfigurationSection warnSection) {
        String dateString = warnSection.getString("date");
        Date date;
        int expire;
        try {
            if (dateString == null) {
                date = null;
                expire = 0;
            } else {
                date = dateFormat.parse(dateString);
                expire = warnSection.getInt("expire");
            }
        } catch (ParseException e) {
            PrimalTribes.logError("There was an error parsing date: " + dateString);
            date = null;
            expire = -1;
        }
        this.date = date;
        this.expire = expire;
    }

    public boolean isExpired() {
        return getExpire().before(new Date());
    }

    public Date getExpire() {
        if (date == null) return new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, expire);
        return calendar.getTime();
    }

    public int getExpireInt() {
        return expire;
    }

    public String getStringDate() {
        return dateFormat.format(getDate());
    }

    public Date getDate() {
        return date;
    }
}