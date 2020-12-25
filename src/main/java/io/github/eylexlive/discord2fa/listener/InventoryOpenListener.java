package io.github.eylexlive.discord2fa.listener;

import io.github.eylexlive.discord2fa.Main;
import io.github.eylexlive.discord2fa.util.ConfigUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.3
 */

public class InventoryOpenListener implements Listener {

    private final Main plugin;

    public InventoryOpenListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void handleInvOpen(InventoryOpenEvent event) {
        if (!ConfigUtil.getBoolean("canceled-events.inventory-open.cancel"))
            return;
        final Player player = (Player) event.getPlayer();
        if (plugin.getDiscord2FAManager().isInCheck(player)) {
            final boolean cancelled = !ConfigUtil.getStringList("canceled-events.inventory-open.whitelisted-inventory-types")
                    .contains(event.getInventory().getType().name());
            event.setCancelled(cancelled);
        }
    }
}
