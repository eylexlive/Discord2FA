package io.github.eylexlive.discord2fa.listener;

import io.github.eylexlive.discord2fa.Main;
import io.github.eylexlive.discord2fa.hook.HookName;
import io.github.eylexlive.discord2fa.manager.HookManager;
import io.github.eylexlive.discord2fa.util.UpdateCheck;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 2.9
 */

public class PlayerJoinListener implements Listener {
    private final Main plugin;
    public PlayerJoinListener(Main plugin) {
        this.plugin = plugin;
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void handleJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final boolean isDev = player.getName().equals("UmutErarslan_") || player.getName().equals("_Luckk_");
        final HookManager hookManager = this.plugin.getHookManager();
        if (!hookManager.isPluginSupport(HookName.AuthMe) && !hookManager.isPluginSupport(HookName.LoginSecurity)) {
            this.plugin.getDiscord2FAManager().checkPlayer(player);
        }
        if (isDev) {
            player.sendMessage(" §6This server is using the Discord2FA §fVersion: §6v" + this.plugin.getDescription().getVersion());
        }
    }
}
