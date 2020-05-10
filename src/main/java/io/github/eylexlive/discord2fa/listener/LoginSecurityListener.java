package io.github.eylexlive.discord2fa.listener;

import com.lenis0012.bukkit.loginsecurity.events.AuthActionEvent;
import com.lenis0012.bukkit.loginsecurity.session.AuthActionType;
import io.github.eylexlive.discord2fa.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 2.1
 */

public class LoginSecurityListener implements Listener {
    private Main plugin;
    public LoginSecurityListener(){
        this.plugin = Main.getInstance();
    }

    @EventHandler
    public void handleLoginSecurityLogin(AuthActionEvent event) {
        Player player = event.getPlayer();
        if(event.getType() == AuthActionType.LOGIN) {
            if(this.plugin.isLoginSecuritySupport()){
                this.plugin.getDiscord2FAManager().checkPlayer(player);
            }
        }
    }
}
