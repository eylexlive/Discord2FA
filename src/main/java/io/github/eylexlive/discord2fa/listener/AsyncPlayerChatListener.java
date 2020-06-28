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
 *	Currently version: 2.4
 */

public class AsyncPlayerChatListener implements Listener {
    private Main plugin;
    public AsyncPlayerChatListener(Main plugin) {
        this.plugin = plugin;
    }
    @EventHandler(priority = EventPriority.LOWEST,ignoreCancelled = true)
    public void handleChat(AsyncPlayerChatEvent event) {
        String settingsPrefix = "canceled-events.";
        if (!this.plugin.getConfig().getBoolean(settingsPrefix+"chat-use.cancel"))
            return;
        Player player= event.getPlayer();
        if (this.plugin.getDiscord2FAManager().isInCheck(player)) {
            this.plugin.getConfig().getStringList(settingsPrefix + "chat-use.whitelisted-words").stream().filter(whitelistedWord -> !event.getMessage().toLowerCase().contains(whitelistedWord.toLowerCase())).forEach(whitelistedWord -> {
                event.setCancelled(true);
                player.sendMessage(Color.translate(this.plugin.getConfig().getString("messages.event-messages.chat-use-message")));
            });
        }
    }
}
