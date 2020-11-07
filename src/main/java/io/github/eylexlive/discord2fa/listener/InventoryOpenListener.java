package io.github.eylexlive.discord2fa.listener;

import io.github.eylexlive.discord2fa.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 2.8
 */

public class InventoryOpenListener implements Listener {
    private final Main plugin;
    public InventoryOpenListener(Main plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void handleInvOpen(InventoryOpenEvent event) {
        final String settingsPrefix = "canceled-events.";
        if (!this.plugin.getConfig().getBoolean(settingsPrefix+"inventory-open.cancel"))
            return;
        final Player player = (Player) event.getPlayer();
        if (this.plugin.getDiscord2FAManager().isInCheck(player)) {
            this.plugin.getConfig().getStringList(settingsPrefix + "inventory-open.whitelisted-inventory-types")
                    .stream()
                    .filter(whitelistedType -> !event.getInventory().getType().name().equalsIgnoreCase(whitelistedType))
                    .forEach(whitelistedType -> event.setCancelled(true));
        }
    }
}
