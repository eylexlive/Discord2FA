package io.github.eylexlive.discord2fa.listener;

import io.github.eylexlive.discord2fa.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.2
 */

public class InventoryClickListener implements Listener {

    private final Main plugin;

    public InventoryClickListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void handleInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            if (!plugin.getConfig().getBoolean("canceled-events.inventory-click.cancel"))
                return;
            final Player player = (Player) event.getWhoClicked();
            if (plugin.getDiscord2FAManager().isInCheck(player) && event.getClickedInventory() != null && event.getCurrentItem() != null) {
                final boolean cancelled = !plugin.getConfig().getStringList("canceled-events.inventory-click.whitelisted-materials")
                        .contains(event.getCurrentItem().getType().name());
                event.setCancelled(cancelled);
            }
        }
    }
}
