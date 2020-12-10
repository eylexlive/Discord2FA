package io.github.eylexlive.discord2fa.listener;

import io.github.eylexlive.discord2fa.Main;
import io.github.eylexlive.discord2fa.util.Color;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.1
 */

public class AsyncPlayerChatListener implements Listener {

    private final Main plugin;

    public AsyncPlayerChatListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void handleChat(AsyncPlayerChatEvent event) {
        if (!plugin.getConfig().getBoolean("canceled-events.chat-use.cancel"))
            return;
        final Player player= event.getPlayer();
        if (plugin.getDiscord2FAManager().isInCheck(player)) {
            plugin.getConfig().getStringList( "canceled-events.chat-use.whitelisted-words")
                    .stream()
                    .filter(whitelistedWord -> !event.getMessage().equalsIgnoreCase(whitelistedWord))
                    .forEach(whitelistedWord -> {
                        event.setCancelled(true);
                        player.sendMessage(Color.translate(plugin.getConfig().getString("messages.event-messages.chat-use-message")));
                    });
        }
    }
}
