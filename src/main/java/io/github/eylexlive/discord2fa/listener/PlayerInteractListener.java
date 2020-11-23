package io.github.eylexlive.discord2fa.listener;

import io.github.eylexlive.discord2fa.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 2.9
 */

public class PlayerInteractListener implements Listener {
    private final Main plugin;
    public PlayerInteractListener(Main plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void handleInteract(PlayerInteractEvent event) {
        if (!this.plugin.getConfig().getBoolean("canceled-events.player-interact.cancel"))
            return;
        final Player player = event.getPlayer();
        if (this.plugin.getDiscord2FAManager().isInCheck(player)) {
            this.plugin.getConfig().getStringList( "canceled-events.player-interact.whitelisted-actions")
                    .stream().filter(whitelistedAction -> !event.getAction().name().equalsIgnoreCase(whitelistedAction))
                    .forEach(whitelistedAction -> event.setCancelled(true));
        }
    }
}
