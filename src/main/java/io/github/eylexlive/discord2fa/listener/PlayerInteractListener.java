package io.github.eylexlive.discord2fa.listener;

import io.github.eylexlive.discord2fa.Discord2FA;
import io.github.eylexlive.discord2fa.util.ConfigUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.4
 */

public class PlayerInteractListener implements Listener {

    private final Discord2FA plugin;

    public PlayerInteractListener(Discord2FA plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void handleInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (!ConfigUtil.getBoolean("canceled-events.player-interact.cancel") || !plugin.getDiscord2FAManager().isInCheck(player))
            return;

        final boolean cancelled = !ConfigUtil.getStringList("canceled-events.player-interact.whitelisted-actions")
                .contains(event.getAction().name());
        event.setCancelled(cancelled);
    }
}
