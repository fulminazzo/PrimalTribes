package it.fulminazzo.primaltribes.Objects.Users;

import it.fulminazzo.primaltribes.Objects.Clan;
import it.fulminazzo.primaltribes.PrimalTribes;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ClanPlayer extends OfflineClanPlayer {
    private boolean socialSpyOn;
    private boolean clanChatActivated;
    private boolean allyChatActivated;
    private Object[] chatConfirmation;

    public ClanPlayer(PrimalTribes plugin, Player player, File playersFolder) throws IOException {
        super(plugin, new File(playersFolder, player.getUniqueId() + ".yml"));
        socialSpyOn = false;
        clanChatActivated = false;
        allyChatActivated = false;
        updateName(player.getName());
    }

    public boolean isSocialSpyOn() {
        return socialSpyOn;
    }

    public void enableSocialSpy() {
        socialSpyOn = true;
    }

    public void disableSocialSpy() {
        socialSpyOn = false;
    }

    public void enableClanChat() {
        if (isInClan()) clanChatActivated = true;
    }

    public void disableClanChat() {
        clanChatActivated = false;
    }

    public void enableAllyChat() {
        if (isInClan()) allyChatActivated = true;
    }

    public void disableAllyChat() {
        allyChatActivated = false;
    }

    public void addChatConfirmation(Consumer<String> action, Runnable cancelAction) {
        chatConfirmation = new Object[]{action, cancelAction,
                Bukkit.getScheduler().runTaskLaterAsynchronously(PrimalTribes.getPlugin(), () ->
                        chatConfirmation = null, 10 * 20)};
    }

    public boolean isChatConfirmationOn() {
        return chatConfirmation != null;
    }

    public void cancelChatConfirmation() {
        if (!isChatConfirmationOn()) return;
        Runnable action = (Runnable) chatConfirmation[1];
        BukkitTask task = (BukkitTask) chatConfirmation[2];
        if (task != null) task.cancel();
        action.run();
        chatConfirmation = null;
    }

    public void executeChatConfirmation(String message) {
        if (!isChatConfirmationOn()) return;
        Consumer<String> action = (Consumer<String>) chatConfirmation[0];
        BukkitTask task = (BukkitTask) chatConfirmation[2];
        if (task != null) task.cancel();
        action.accept(message);
        chatConfirmation = null;
    }

    public void acceptInvite(Clan clan) {
        List<Clan> clans = getClansByInvite();
        clans.forEach(c -> c.unInvitePlayer(null, getName()));
        clan.addMember(Bukkit.getPlayer(getUuid()));
        addPlayerToClan(clan);
    }

    public boolean isInvitedByClan(Clan clan) {
        return getClansByInvite().contains(clan);
    }

    public List<Clan> getClansByInvite() {
        return PrimalTribes.getPlugin().getClansManager().getClans().stream()
                .filter(c -> c.isPlayerInvited(getName()))
                .collect(Collectors.toList());
    }

    public List<Invite> getClanInvites() {
        return PrimalTribes.getPlugin().getClansManager().getClans().stream()
                .map(c -> c.getInvite(getName()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public boolean isClanChatActivated() {
        return clanChatActivated;
    }

    public boolean isAllyChatActivated() {
        return allyChatActivated;
    }

    public void quitPlayer() throws IOException {
        save();
    }
}