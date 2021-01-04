package io.github.eylexlive.discord2fa.listener;

import io.github.eylexlive.discord2fa.Discord2FA;
import io.github.eylexlive.discord2fa.util.ConfigUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.4
 */

public class PlayerCommandUseListener implements Listener {

    private final Discord2FA plugin;

    public PlayerCommandUseListener(Discord2FA plugin) {
        this.plugin = plugin;
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void handleCommandPreProcess(PlayerCommandPreprocessEvent event) {
        final Player player= event.getPlayer();
        if (!ConfigUtil.getBoolean("canceled-events.command-use.cancel") || !plugin.getDiscord2FAManager().isInCheck(player))
            return;

        final String[] commandArguments = event.getMessage().split(" ");
        final boolean cancelled = !ConfigUtil.getStringList("canceled-events.command-use.whitelisted-commands")
                .contains(commandArguments[0].replaceFirst("/", ""));
        event.setCancelled(cancelled);
        if (cancelled) player.sendMessage(ConfigUtil.getString("messages.event-messages.command-use-message"));
    }
}
