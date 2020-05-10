package io.github.eylexlive.discord2fa.listener;

import io.github.eylexlive.discord2fa.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 2.1
 */

public class InventoryOpenListener implements Listener {
    private Main plugin;
    public InventoryOpenListener() {
        this.plugin = Main.getInstance();
    }
    @EventHandler
    public void handleInvOpen(InventoryOpenEvent event) {
        String settingsPrefix = "canceled-events.";
        if (!this.plugin.getConfig().getBoolean(settingsPrefix+"inventory-open.cancel")) {
            return;
        }
        Player player = (Player) event.getPlayer();
        if (this.plugin.getDiscord2FAManager().isInCheck(player)) {
            for (String whitelistedType : this.plugin.getConfig().getStringList(settingsPrefix+"inventory-open.whitelisted-inventory-types")) {
                if (!event.getInventory().getType().name().toUpperCase().equals(whitelistedType.toUpperCase())) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
