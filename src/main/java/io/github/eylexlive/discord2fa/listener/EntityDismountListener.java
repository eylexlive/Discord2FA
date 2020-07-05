package io.github.eylexlive.discord2fa.listener;

import io.github.eylexlive.discord2fa.Main;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spigotmc.event.entity.EntityDismountEvent;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 2.5
 */

public class EntityDismountListener implements Listener {
    private Main plugin;
    public EntityDismountListener(Main plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void handleEntityDimount(EntityDismountEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (event.getDismounted() instanceof ArmorStand) {
                ArmorStand armorStand = (ArmorStand) event.getDismounted();
                if (this.plugin.getDiscord2FAManager().isInCheck(player) && this.plugin.getSitManager().getArmorStands().get(player).getUniqueId().toString().equals(armorStand.getUniqueId().toString())) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
