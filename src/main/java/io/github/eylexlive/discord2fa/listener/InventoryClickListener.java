package io.github.eylexlive.discord2fa.listener;

import io.github.eylexlive.discord2fa.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 2.4
 */

public class InventoryClickListener implements Listener {
    private Main plugin;
    public InventoryClickListener(Main plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void handleInventoryClick(InventoryClickEvent event) {
        String settingsPrefix = "canceled-events.";
        if (event.getWhoClicked() instanceof Player) {
            if (!this.plugin.getConfig().getBoolean(settingsPrefix+"inventory-click.cancel"))
                return;
            Player player = (Player) event.getWhoClicked();
            if (this.plugin.getDiscord2FAManager().isInCheck(player) && event.getClickedInventory() != null && event.getCurrentItem() != null) {
                this.plugin.getConfig().getStringList(settingsPrefix+"inventory-click.whitelisted-materials").stream().filter(whitelistedMaterial -> !event.getCurrentItem().getType().name().equalsIgnoreCase(whitelistedMaterial)) .forEach(whitelistedMaterial -> event.setCancelled(true));
            }
        }
    }
}
