package io.github.eylexlive.discord2fa.listener;

import io.github.eylexlive.discord2fa.Discord2FA;
import io.github.eylexlive.discord2fa.util.ConfigUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.5
 */

public class InventoryClickListener implements Listener {

    private final Discord2FA plugin;

    public InventoryClickListener(Discord2FA plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void handleInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            final Player player = (Player) event.getWhoClicked();
            if (!ConfigUtil.getBoolean("canceled-events.inventory-click.cancel") || !plugin.getDiscord2FAManager().isInCheck(player))
                return;

            if (event.getClickedInventory() != null && event.getCurrentItem() != null) {
                final boolean cancelled = !ConfigUtil.getStringList("canceled-events.inventory-click.whitelisted-materials")
                        .contains(event.getCurrentItem().getType().name());
                event.setCancelled(cancelled);
            }
        }
    }
}
