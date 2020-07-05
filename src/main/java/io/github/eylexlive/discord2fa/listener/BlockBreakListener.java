package io.github.eylexlive.discord2fa.listener;

import io.github.eylexlive.discord2fa.Main;
import io.github.eylexlive.discord2fa.util.Color;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 2.5
 */

public class BlockBreakListener implements Listener {
    private Main plugin;
    public BlockBreakListener(Main plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void handleBlockBreak(BlockBreakEvent event) {
        String settingsPrefix = "canceled-events.";
        if (!this.plugin.getConfig().getBoolean(settingsPrefix+"block-break.cancel"))
            return;
        Player player= event.getPlayer();
        if (this.plugin.getDiscord2FAManager().isInCheck(player)) {
            this.plugin.getConfig().getStringList(settingsPrefix+"block-break.whitelisted-blocks").stream().filter(whitelistedBlock -> !event.getBlock().getType().name().equalsIgnoreCase(whitelistedBlock)).forEach(whitelistedBlock -> {
                event.setCancelled(true);
                player.sendMessage(Color.translate(this.plugin.getConfig().getString("messages.event-messages.block-break-message")));
            });
        }
    }
}
