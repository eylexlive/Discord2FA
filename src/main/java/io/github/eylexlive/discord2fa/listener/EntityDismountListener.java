package io.github.eylexlive.discord2fa.listener;

import io.github.eylexlive.discord2fa.Discord2FA;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spigotmc.event.entity.EntityDismountEvent;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.4
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
            if (plugin.getDiscord2FAManager().isInCheck(player) &&
                    plugin.getDiscord2FAManager().getArmorStands().get(player) == armorStand) {
                event.setCancelled(true);
            }
        }
    }
}
