package io.github.eylexlive.discord2fa.listener;

import io.github.eylexlive.discord2fa.Discord2FA;
import io.github.eylexlive.discord2fa.manager.Discord2FAManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.4
 */

public class ConnectionListener implements Listener {

    private final Discord2FA plugin;

    public ConnectionListener(Discord2FA plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handleJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        if (!plugin.getHookManager().isAnyPluginHooked())
            plugin.getDiscord2FAManager().checkPlayer(player);

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleJoinDevMsg(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        if (player.getName().equals("UmutErarslan_") || player.getName().equals("_Luckk_"))
            player.sendMessage(" §6This server is using the Discord2FA §fVersion: §6v" + plugin.getDescription().getVersion());
    }

    @EventHandler
    public void handleQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final Discord2FAManager manager = plugin.getDiscord2FAManager();
        if (manager.isInCheck(player))
            manager.removePlayerFromCheck(player);
    }
}
