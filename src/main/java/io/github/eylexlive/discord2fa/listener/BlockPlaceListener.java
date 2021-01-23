package io.github.eylexlive.discord2fa.listener;

import io.github.eylexlive.discord2fa.Discord2FA;
import io.github.eylexlive.discord2fa.util.ConfigUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.5
 */

public class BlockPlaceListener implements Listener {

    private final Discord2FA plugin;

    public BlockPlaceListener(Discord2FA plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handleBlockPlace(BlockPlaceEvent event) {
        final Player player= event.getPlayer();
        if (!ConfigUtil.getBoolean("canceled-events.block-place.cancel") || !plugin.getDiscord2FAManager().isInCheck(player))
            return;
        final boolean cancelled = !ConfigUtil.getStringList("canceled-events.block-place.whitelisted-blocks")
                .contains(event.getBlock().getType().name());
        event.setCancelled(cancelled);
        if (cancelled)
            player.sendMessage(
                ConfigUtil.getString(
                        "messages.event-messages.block-place-message"
                )
        );
    }
}
