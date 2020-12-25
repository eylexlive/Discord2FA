package io.github.eylexlive.discord2fa.listener;

import io.github.eylexlive.discord2fa.Main;
import io.github.eylexlive.discord2fa.manager.Discord2FAManager;
import io.github.eylexlive.discord2fa.util.ConfigUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.3
 */

public class AsyncPlayerChatListener implements Listener {

    private final Main plugin;

    public AsyncPlayerChatListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void handleChat(AsyncPlayerChatEvent event) {
        if (!ConfigUtil.getBoolean("canceled-events.chat-use.cancel"))
            return;
        final Player player = event.getPlayer();
        final Discord2FAManager discord2FAManager = plugin.getDiscord2FAManager();
        final String confirmCode = discord2FAManager.getConfirmCode().get(player.getUniqueId());
        if (discord2FAManager.isInCheck(player)) {
            final boolean cancelled = !ConfigUtil.getStringList("canceled-events.chat-use.whitelisted-words")
                    .contains(event.getMessage());
            event.setCancelled(cancelled);
            if (cancelled) player.sendMessage(ConfigUtil.getString("messages.event-messages.chat-use-message"));
        }
        else if (confirmCode != null && confirmCode.equals("§")) {
            event.setCancelled(true);
            discord2FAManager.sendConfirmCode(player, event.getMessage());
        }
        else if (confirmCode != null) {
            event.setCancelled(true);
            discord2FAManager.enable2FA(player, event.getMessage());
        }
    }
}
