package io.github.eylexlive.discord2fa.listener;

import io.github.eylexlive.discord2fa.Main;
import io.github.eylexlive.discord2fa.util.Color;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 2.6
 */

public class PlayerCommandUseListener implements Listener {
    private final Main plugin;
    public PlayerCommandUseListener(Main plugin) {
        this.plugin = plugin;
    }
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void handleCommandPreProcess(PlayerCommandPreprocessEvent event) {
        final String settingsPrefix = "canceled-events.";
        if (!this.plugin.getConfig().getBoolean(settingsPrefix +"command-use.cancel"))
            return;
        final Player player= event.getPlayer();
        if (this.plugin.getDiscord2FAManager().isInCheck(player)) {
            final String[] args = event.getMessage().split(" ");
            this.plugin.getConfig().getStringList(settingsPrefix + "command-use.whitelisted-commands")
                    .stream()
                    .filter(whitelistedCommand -> !args[0].equalsIgnoreCase("/"+whitelistedCommand))
                    .forEach(whitelistedCommand -> {
                        event.setCancelled(true);
                        player.sendMessage(Color.translate(this.plugin.getConfig().getString("messages.event-messages.command-use-message")));
                    });
        }
    }
}
