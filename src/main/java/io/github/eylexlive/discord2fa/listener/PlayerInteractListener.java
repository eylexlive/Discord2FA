package io.github.eylexlive.discord2fa.listener;

import io.github.eylexlive.discord2fa.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.2
 */

public class PlayerInteractListener implements Listener {

    private final Main plugin;

    public PlayerInteractListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void handleInteract(PlayerInteractEvent event) {
        if (!plugin.getConfig().getBoolean("canceled-events.player-interact.cancel"))
            return;
        final Player player = event.getPlayer();
        if (plugin.getDiscord2FAManager().isInCheck(player)) {
            final boolean cancelled = !plugin.getConfig().getStringList("canceled-events.player-interact.whitelisted-actions")
                    .contains(event.getAction().name());
            event.setCancelled(cancelled);
        }
    }
}
