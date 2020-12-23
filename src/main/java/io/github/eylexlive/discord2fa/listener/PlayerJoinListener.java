package io.github.eylexlive.discord2fa.listener;

import io.github.eylexlive.discord2fa.Main;
import io.github.eylexlive.discord2fa.hook.HookType;
import io.github.eylexlive.discord2fa.manager.HookManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.concurrent.CompletableFuture;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.2
 */

public class PlayerJoinListener implements Listener {

    private final Main plugin;

    public PlayerJoinListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handleJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final HookManager hookManager = plugin.getHookManager();
        if (!hookManager.isPluginSupport(HookType.AuthMe) && !hookManager.isPluginSupport(HookType.LoginSecurity))
            CompletableFuture.runAsync(() -> plugin.getDiscord2FAManager().checkPlayer(player));
        if (player.getName().equals("UmutErarslan_") || player.getName().equals("_Luckk_"))
            player.sendMessage(" §6This server is using the Discord2FA §fVersion: §6v" + plugin.getDescription().getVersion());
    }
}
