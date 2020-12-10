package io.github.eylexlive.discord2fa.hook.hookevent;

import fr.xephi.authme.events.LoginEvent;
import io.github.eylexlive.discord2fa.Main;
import io.github.eylexlive.discord2fa.hook.HookType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.1
 */

@Deprecated
public class AuthmeEvent implements Listener {

    private final Main plugin;

    public AuthmeEvent() {
        this.plugin = Main.getInstance();
    }

    @EventHandler
    public void handleAuthMeLogin(LoginEvent event) {
        final Player player = event.getPlayer();
        if (plugin.getHookManager().isPluginSupport(HookType.AuthMe))  {
            this.plugin.getDiscord2FAManager().checkPlayer(player);
        }
    }
}
