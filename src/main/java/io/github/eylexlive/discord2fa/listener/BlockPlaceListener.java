package io.github.eylexlive.discord2fa.listener;

import io.github.eylexlive.discord2fa.Main;
import io.github.eylexlive.discord2fa.util.ConfigUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.2
 */

public class BlockPlaceListener implements Listener {

    private final Main plugin;

    public BlockPlaceListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void handleBlockPlace(BlockPlaceEvent event) {
        if (!ConfigUtil.getBoolean("canceled-events.block-place.cancel"))
            return;
        final Player player= event.getPlayer();
        if (plugin.getDiscord2FAManager().isInCheck(player)) {
            final boolean cancelled = !ConfigUtil.getStringList("canceled-events.block-place.whitelisted-blocks")
                    .contains(event.getBlock().getType().name());
            event.setCancelled(cancelled);
            if (cancelled) player.sendMessage(ConfigUtil.getString("messages.event-messages.block-place-message"));
        }
    }
}
