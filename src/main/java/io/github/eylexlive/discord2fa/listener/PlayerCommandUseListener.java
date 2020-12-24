package io.github.eylexlive.discord2fa.listener;

import io.github.eylexlive.discord2fa.Main;
import io.github.eylexlive.discord2fa.util.ConfigUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.2
 */

public class PlayerCommandUseListener implements Listener {

    private final Main plugin;

    public PlayerCommandUseListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void handleCommandPreProcess(PlayerCommandPreprocessEvent event) {
        if (!ConfigUtil.getBoolean("canceled-events.command-use.cancel"))
            return;
        final Player player= event.getPlayer();
        if (plugin.getDiscord2FAManager().isInCheck(player)) {
            final String[] commandArguments = event.getMessage().split(" ");
            final boolean cancelled = !ConfigUtil.getStringList("canceled-events.command-use.whitelisted-commands")
                    .contains(commandArguments[0].replaceFirst("/", ""));
            event.setCancelled(cancelled);
            if (cancelled) player.sendMessage(ConfigUtil.getString("messages.event-messages.command-use-message"));
        }
    }
}
