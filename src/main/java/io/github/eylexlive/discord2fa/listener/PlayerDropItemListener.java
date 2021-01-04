package io.github.eylexlive.discord2fa.listener;

import io.github.eylexlive.discord2fa.Discord2FA;
import io.github.eylexlive.discord2fa.util.ConfigUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.4
 */

public class PlayerDropItemListener implements Listener {

    private final Discord2FA plugin;

    public PlayerDropItemListener(Discord2FA plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void handleItemDrop(PlayerDropItemEvent event) {
        final Player player= event.getPlayer();
        if (!ConfigUtil.getBoolean("canceled-events.item-drop.cancel") || !plugin.getDiscord2FAManager().isInCheck(player))
            return;

        final boolean cancelled = !ConfigUtil.getStringList("canceled-events.item-drop.whitelisted-materials")
                .contains(event.getItemDrop().getItemStack().getType().name());
        event.setCancelled(cancelled);
        if (cancelled) player.sendMessage(ConfigUtil.getString("messages.event-messages.item-drop-message"));
    }
}
