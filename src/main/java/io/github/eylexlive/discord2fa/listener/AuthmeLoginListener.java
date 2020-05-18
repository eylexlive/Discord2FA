package io.github.eylexlive.discord2fa.listener;

import fr.xephi.authme.events.LoginEvent;
import io.github.eylexlive.discord2fa.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 2.2
 */

public class AuthmeLoginListener implements Listener {
    private Main plugin;
    public AuthmeLoginListener() {
        this.plugin = Main.getInstance();
    }
    @EventHandler
    public void handleAuthMeLogin(LoginEvent event) {
        Player player = event.getPlayer();
        if (this.plugin.isAuthmeSupport())  {
            this.plugin.getDiscord2FAManager().checkPlayer(player);
        }
    }
}
