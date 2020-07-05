package io.github.eylexlive.discord2fa.runnable;

import io.github.eylexlive.discord2fa.Main;
import io.github.eylexlive.discord2fa.util.Color;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 2.5
 */

public class CountdownRunnable extends BukkitRunnable {
    private Main plugin;
    private int countdown;
    private Player player;
    public CountdownRunnable(Player player, Main plugin) {
        this.player = player;
        this.plugin = plugin;
        this.countdown = this.plugin.getConfig().getInt("auth-countdown") - 1;
    }
    @Override
    public void run() {
        if (countdown <= 0){
            this.cancel();
            UUID uuid = this.player.getUniqueId();
            if (this.plugin.getDiscord2FAManager().getLeftRights().get(uuid) > 0) {
                this.plugin.getDiscord2FAManager().getLeftRights().put(uuid, this.plugin.getDiscord2FAManager().getLeftRights().get(uuid)-1);
                String message = this.plugin.getConfig().getString("messages.kick-message");
                message = message.replace("%rights%",String.valueOf(this.plugin.getDiscord2FAManager().getLeftRights().get(uuid)));
                player.kickPlayer(Color.translate(message));
            } else {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        plugin.getDiscord2FAManager().getLeftRights().put(uuid, plugin.getConfig().getInt("number-of-rights"));
                        String command = plugin.getConfig().getString("rights-reached-console-command");
                        command = command.replace("%player%",player.getName());
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),command);
                    }
                }.runTaskLater(this.plugin,2L);
            }
            return;
        } else if (!this.plugin.getDiscord2FAManager().isInCheck(player)) {
            this.cancel();
            return;
        } else if (countdown % 10 == 0 && countdown > 6 || countdown < 6 && countdown > 0) {
            player.sendMessage(this.plugin.getDiscord2FAManager().getAuthMessage(false, countdown));
        }
        countdown--;
    }
}
