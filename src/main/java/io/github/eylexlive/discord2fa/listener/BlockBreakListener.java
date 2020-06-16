package io.github.eylexlive.discord2fa.listener;

import io.github.eylexlive.discord2fa.Main;
import io.github.eylexlive.discord2fa.util.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 2.3
 */

public class BlockBreakListener implements Listener {
    private Main plugin;
    public BlockBreakListener() {
        this.plugin = Main.getInstance();
    }
    @EventHandler
    public void handleBlockBreak(BlockBreakEvent event) {
        String settingsPrefix = "canceled-events.";
        if (!this.plugin.getConfig().getBoolean(settingsPrefix+"block-break.cancel")) {
            return;
        }
        Player player= event.getPlayer();
        if (this.plugin.getDiscord2FAManager().isInCheck(player)) {
            for (String whitelistedBlock : this.plugin.getConfig().getStringList(settingsPrefix+"block-break.whitelisted-blocks")) {
                if (event.getBlock().getType() != Material.getMaterial(whitelistedBlock)) {
                    event.setCancelled(true);
                    player.sendMessage(Color.translate(this.plugin.getConfig().getString("messages.event-messages.block-break-message")));
                }
            }
        }
    }
}
