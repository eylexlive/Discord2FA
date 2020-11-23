package io.github.eylexlive.discord2fa.listener;

import io.github.eylexlive.discord2fa.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 2.9
 */

public class InventoryOpenListener implements Listener {
    private final Main plugin;
    public InventoryOpenListener(Main plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void handleInvOpen(InventoryOpenEvent event) {
        if (!this.plugin.getConfig().getBoolean("canceled-events.inventory-open.cancel"))
            return;
        final Player player = (Player) event.getPlayer();
        if (this.plugin.getDiscord2FAManager().isInCheck(player)) {
            this.plugin.getConfig().getStringList("canceled-events.inventory-open.whitelisted-inventory-types")
                    .stream()
                    .filter(whitelistedType -> !event.getInventory().getType().name().equalsIgnoreCase(whitelistedType))
                    .forEach(whitelistedType -> event.setCancelled(true));
        }
    }
}
