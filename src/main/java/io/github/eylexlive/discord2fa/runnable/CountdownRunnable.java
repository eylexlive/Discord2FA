package io.github.eylexlive.discord2fa.runnable;

import io.github.eylexlive.discord2fa.Main;
import io.github.eylexlive.discord2fa.manager.Discord2FAManager;
import io.github.eylexlive.discord2fa.util.ConfigUtil;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.3
 */

public class CountdownRunnable extends BukkitRunnable {

    private final Main plugin;

    private final Discord2FAManager discord2FAManager;

    private int countdown;
    private final Player player;


    public CountdownRunnable(Player player, Main plugin) {
        this.player = player;
        this.plugin = plugin;
        this.discord2FAManager = plugin.getDiscord2FAManager();
        this.countdown = ConfigUtil.getInt("auth-countdown") - 1;
    }

    @Override
    public void run() {
        if (countdown <= 0){
            cancel();
            final UUID uuid = player.getUniqueId();
            if (discord2FAManager.getLeftRights().get(uuid) > 0) {
                discord2FAManager.getLeftRights().put(uuid, discord2FAManager.getLeftRights().get(uuid) - 1);
                player.kickPlayer(ConfigUtil.getString("messages.kick-message", "rights:" + discord2FAManager.getLeftRights().get(uuid)));
            } else {
                new BukkitRunnable() { @Override public void run() { discord2FAManager.failPlayer(player); }}.runTaskLater(plugin,2L);
            }
            return;
        } else if (!discord2FAManager.isInCheck(player)) {
            cancel();
            return;
        } else if (countdown % 10 == 0 && countdown > 6 || countdown < 6 && countdown > 0) {
            player.sendMessage(discord2FAManager.getAuthMessage(false, countdown));
        }
        countdown--;
    }
}
