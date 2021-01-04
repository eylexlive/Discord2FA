package io.github.eylexlive.discord2fa.listener;

import io.github.eylexlive.discord2fa.Discord2FA;
import io.github.eylexlive.discord2fa.manager.Discord2FAManager;
import io.github.eylexlive.discord2fa.util.ConfigUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.4
 */

public class AsyncPlayerChatListener implements Listener {

    private final Discord2FA plugin;

    public AsyncPlayerChatListener(Discord2FA plugin) {
        this.plugin = plugin;
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void handleChat(AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        if (!ConfigUtil.getBoolean("canceled-events.chat-use.cancel") || !plugin.getDiscord2FAManager().isInCheck(player))
            return;

        final boolean cancelled = !ConfigUtil.getStringList("canceled-events.chat-use.whitelisted-words")
                .contains(event.getMessage());
        event.setCancelled(cancelled);
        if (cancelled) player.sendMessage(ConfigUtil.getString("messages.event-messages.chat-use-message"));
    }

    @EventHandler
    public void handleChatConfirm(AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();

        final Discord2FAManager manager = plugin.getDiscord2FAManager();
        final String confirmCode = manager.getPlayerData(player).getConfirmCode();

        if (confirmCode == null || manager.isInCheck(player))
            return;

        if (confirmCode.equals("§"))
            manager.sendConfirmCode(player, event.getMessage());
         else
            manager.enable2FA(player, event.getMessage());

        event.setCancelled(true);
    }
}
