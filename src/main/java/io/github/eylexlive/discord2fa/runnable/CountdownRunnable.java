package io.github.eylexlive.discord2fa.runnable;

import io.github.eylexlive.discord2fa.Main;
import io.github.eylexlive.discord2fa.manager.Discord2FAManager;
import io.github.eylexlive.discord2fa.util.Color;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 2.9
 */

public class CountdownRunnable extends BukkitRunnable {
    private final Main plugin;
    private final Discord2FAManager discord2FAManager;
    private final Player player;
    private int countdown;
    public CountdownRunnable(Player player, Main plugin) {
        this.player = player;
        this.plugin = plugin;
        this.discord2FAManager = this.plugin.getDiscord2FAManager();
        this.countdown = this.plugin.getConfig().getInt("auth-countdown") - 1;
    }
    @Override
    public void run() {
        if (this.countdown <= 0){
            this.cancel();
            final UUID uuid = this.player.getUniqueId();
            if (this.discord2FAManager.getLeftRights().get(uuid) > 0) {
                this.discord2FAManager.getLeftRights().put(uuid, this.discord2FAManager.getLeftRights().get(uuid) - 1);
                String message = this.plugin.getConfig().getString("messages.kick-message");
                message = message.replace("%rights%", String.valueOf(this.discord2FAManager.getLeftRights().get(uuid)));
                player.kickPlayer(Color.translate(message));
            } else {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        final Server server = plugin.getServer();
                        discord2FAManager.getLeftRights().put(uuid, plugin.getConfig().getInt("number-of-rights"));
                        String command = plugin.getConfig().getString("rights-reached-console-command");
                        command = command.replace("%player%", player.getName());
                        server.dispatchCommand(server.getConsoleSender(), command);
                    }
                }.runTaskLater(this.plugin,2L);
            }
            return;
        } else if (!this.discord2FAManager.isInCheck(player)) {
            this.cancel();
            return;
        } else if (this.countdown % 10 == 0 && this.countdown > 6 || this.countdown < 6 && this.countdown > 0) {
            player.sendMessage(this.discord2FAManager.getAuthMessage(false, countdown));
        }
        countdown--;
    }
}
