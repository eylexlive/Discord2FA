package io.github.eylexlive.discord2fa.listener;

import io.github.eylexlive.discord2fa.Discord2FA;
import io.github.eylexlive.discord2fa.util.ConfigUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.4
 */

public class InventoryOpenListener implements Listener {

    private final Discord2FA plugin;

    public InventoryOpenListener(Discord2FA plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void handleInvOpen(InventoryOpenEvent event) {
        final Player player = (Player) event.getPlayer();
        if (!ConfigUtil.getBoolean("canceled-events.inventory-open.cancel") || !plugin.getDiscord2FAManager().isInCheck(player))
            return;

        final boolean cancelled = !ConfigUtil.getStringList("canceled-events.inventory-open.whitelisted-inventory-types")
                .contains(event.getInventory().getType().name());
        event.setCancelled(cancelled);
    }
}
