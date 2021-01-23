package io.github.eylexlive.discord2fa.listener;

import io.github.eylexlive.discord2fa.Discord2FA;
import io.github.eylexlive.discord2fa.manager.Discord2FAManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spigotmc.event.entity.EntityDismountEvent;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.5
 */

public class EntityDismountListener implements Listener {

    private final Discord2FA plugin;

    public EntityDismountListener(Discord2FA plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void handleEntityDismount(EntityDismountEvent event) {
        if (event.getEntity() instanceof Player && event.getDismounted() instanceof ArmorStand) {
            final Player player = (Player) event.getEntity();
            final ArmorStand armorStand = (ArmorStand) event.getDismounted();

            final Discord2FAManager manager = plugin.getDiscord2FAManager();
            if (manager.isInCheck(player) && manager.getArmorStands().get(player) == armorStand) {
                if (plugin.isValidateEdm())
                    event.setCancelled(true);
                 else
                    Bukkit.getScheduler().runTaskLater(plugin, () -> manager.reSitPlayer(player), 1L);
            }
        }
    }
}
