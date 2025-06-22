package it.fulminazzo.primaltribes.Objects.Users;

import it.fulminazzo.primaltribes.Objects.Warn;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Warned extends User {
    private final List<Warn> warns;

    public Warned(ConfigurationSection warnedSection) {
        super(warnedSection);
        ConfigurationSection warnsSection = warnedSection.getConfigurationSection("warns");
        this.warns = warnsSection == null ? new ArrayList<>() :
                warnsSection.getKeys(false).stream()
                        .map(warnsSection::getConfigurationSection)
                        .filter(Objects::nonNull)
                        .map(Warn::new)
                        .collect(Collectors.toList());
        updateWarns();
    }

    public Warned(Player player) {
        super(player);
        this.warns = new ArrayList<>();
        updateWarns();
    }

    public int getWarns() {
        updateWarns();
        return warns.size();
    }

    public List<Warn> getWarnsList() {
        updateWarns();
        return warns;
    }

    public void addWarn(int expire) {
        updateWarns();
        Warn warn = new Warn(new Date(), expire);
        warns.add(warn);
    }

    public void clearWarns() {
        warns.clear();
    }

    private void updateWarns() {
        List<Warn> warns = new ArrayList<>(this.warns);
        warns.removeIf(Warn::isExpired);
        this.warns.clear();
        this.warns.addAll(warns);
    }
}
