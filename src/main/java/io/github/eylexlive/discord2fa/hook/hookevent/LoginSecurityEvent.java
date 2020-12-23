package io.github.eylexlive.discord2fa.hook.hookevent;

import com.lenis0012.bukkit.loginsecurity.events.AuthActionEvent;
import com.lenis0012.bukkit.loginsecurity.session.AuthActionType;
import io.github.eylexlive.discord2fa.Main;
import io.github.eylexlive.discord2fa.hook.HookType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.2
 */

@Deprecated
public class LoginSecurityEvent implements Listener {

    private final Main plugin;

    public LoginSecurityEvent(){
        this.plugin = Main.getInstance();
    }

    @EventHandler
    public void handleLoginSecurityLogin(AuthActionEvent event) {
        final Player player = event.getPlayer();
        if(event.getType() == AuthActionType.LOGIN && plugin.getHookManager().isPluginSupport(HookType.LoginSecurity)) {
            plugin.getDiscord2FAManager().checkPlayer(player);
        }
    }
}
