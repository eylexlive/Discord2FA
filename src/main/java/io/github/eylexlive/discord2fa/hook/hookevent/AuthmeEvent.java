package io.github.eylexlive.discord2fa.hook.hookevent;

import fr.xephi.authme.events.LoginEvent;
import io.github.eylexlive.discord2fa.Discord2FA;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.4
 */

@SuppressWarnings("unused")
public class AuthMeEvent implements Listener {

    private final Discord2FA plugin;

    public AuthMeEvent() {
        this.plugin = Discord2FA.getInstance();
    }

    @EventHandler
    public void handleAuthMeLogin(LoginEvent event) {
        plugin.getDiscord2FAManager().checkPlayer(event.getPlayer());
    }
}
