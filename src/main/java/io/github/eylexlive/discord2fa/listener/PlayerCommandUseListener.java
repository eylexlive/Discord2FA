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
 *	Currently version: 2.1
 */

public class PlayerCommandUseListener implements Listener {
    private Main plugin;
    public PlayerCommandUseListener() {
        this.plugin = Main.getInstance();
    }
    @EventHandler(priority = EventPriority.HIGHEST,ignoreCancelled = true)
    public void handleCommandPreProcess(PlayerCommandPreprocessEvent event) {
        String settingsPrefix = "canceled-events.";
        if (!this.plugin.getConfig().getBoolean(settingsPrefix +"command-use.cancel")) {
            return;
        }
        Player player= event.getPlayer();
        if (this.plugin.getDiscord2FAManager().isInCheck(player)) {
            String[] args = event.getMessage().split(" ");
            for (String whiteListedCommand : this.plugin.getConfig().getStringList(settingsPrefix +"command-use.whitelisted-commands")) {
                if (!args[0].equalsIgnoreCase("/"+whiteListedCommand)) {
                    event.setCancelled(true);
                    player.sendMessage(Color.translate(this.plugin.getConfig().getString("messages.event-messages.command-use-message")));
                }
            }
        }
    }
}
