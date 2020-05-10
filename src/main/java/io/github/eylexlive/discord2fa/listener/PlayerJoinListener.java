package io.github.eylexlive.discord2fa.listener;

import io.github.eylexlive.discord2fa.Main;
import io.github.eylexlive.discord2fa.util.UpdateCheck;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 2.1
 */

public class PlayerJoinListener implements Listener {
    private Main plugin;
    public PlayerJoinListener() {
        this.plugin = Main.getInstance();
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void handleJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        boolean isDev = player.getName().equals("UmutErarslan_") || player.getName().equals("_Luckk_");
        if (isDev)
            player.sendMessage("§f§lHEY §6This server is using the Discord2FA plugin!");
        if (!this.plugin.isAuthmeSupport() && !this.plugin.isLoginSecuritySupport()) {
            this.plugin.getDiscord2FAManager().checkPlayer(player);
        }
    }
}
