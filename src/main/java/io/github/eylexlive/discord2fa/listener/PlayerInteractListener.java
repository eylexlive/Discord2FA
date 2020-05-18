package io.github.eylexlive.discord2fa.listener;

import io.github.eylexlive.discord2fa.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 2.2
 */

public class PlayerInteractListener implements Listener {
    private Main plugin;
    public PlayerInteractListener() {
        this.plugin = Main.getInstance();
    }
    @EventHandler
    public void handleInteract(PlayerInteractEvent event) {
        String settingsPrefix = "canceled-events.";
        if (!this.plugin.getConfig().getBoolean(settingsPrefix+"player-interact.cancel")) {
            return;
        }
        Player player = event.getPlayer();
        if (this.plugin.getDiscord2FAManager().isInCheck(player)) {
            for (String whitelistedAction : this.plugin.getConfig().getStringList(settingsPrefix+"player-interact.whitelisted-actions")) {
                if (!event.getAction().name().equals(whitelistedAction.toUpperCase())) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
