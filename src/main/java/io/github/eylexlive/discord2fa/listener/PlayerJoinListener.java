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
 *	Currently version: 2.5
 */

public class PlayerJoinListener implements Listener {
    private Main plugin;
    public PlayerJoinListener(Main plugin) {
        this.plugin = plugin;
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void handleJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        boolean isDev = player.getName().equals("UmutErarslan_") || player.getName().equals("_Luckk_");
        if (isDev)
            player.sendMessage(" §6This server is using the Discord2FA §fVersion: §6v" + this.plugin.getDescription().getVersion());
        if (!this.plugin.isAuthmeSupport() && !this.plugin.isLoginSecuritySupport()) {
            this.plugin.getDiscord2FAManager().checkPlayer(player);
        }
    }
}
