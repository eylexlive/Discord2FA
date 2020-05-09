package io.github.eylexlive.discord2fa.listener;

import io.github.eylexlive.discord2fa.Main;
import io.github.eylexlive.discord2fa.util.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/*
*	Created by EylexLive on Feb 23, 2020.
*	Currently version: 2.0
*/

public class AsyncPlayerChatListener implements Listener {
    private Main plugin;
    public AsyncPlayerChatListener() {
        this.plugin = Main.getInstance();
    }
    @EventHandler(priority = EventPriority.HIGHEST,ignoreCancelled = true)
    public void handleChat(AsyncPlayerChatEvent event) {
        String settingsPrefix = "canceled-events.";
        if (!this.plugin.getConfig().getBoolean(settingsPrefix+"chat-use.cancel")) {
            return;
        }
        Player player= event.getPlayer();
        if (this.plugin.getDiscord2FAManager().isInCheck(player)) {
            for (String whitelistedWord : this.plugin.getConfig().getStringList(settingsPrefix+"chat-use.whitelisted-words")) {
                if (!event.getMessage().toLowerCase().contains(whitelistedWord.toLowerCase())) {
                    event.setCancelled(true);
                    player.sendMessage(Utils.translate(this.plugin.getConfig().getString("messages.event-messages.chat-use-message")));
                }
            }
        }
    }
}
