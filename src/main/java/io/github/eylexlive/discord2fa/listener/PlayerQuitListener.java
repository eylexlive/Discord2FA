package io.github.eylexlive.discord2fa.listener;

import io.github.eylexlive.discord2fa.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 2.3
 */

public class PlayerQuitListener implements Listener {
    private Main plugin;
    public PlayerQuitListener() {
        this.plugin = Main.getInstance();
    }
    @EventHandler
    public void handleQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (this.plugin.getDiscord2FAManager().isAddedToVerifyList(player.getName() ) && this.plugin.getDiscord2FAManager().isInCheck(player)) {
            this.plugin.getDiscord2FAManager().removePlayerFromCheck(player);
        }
    }
}
