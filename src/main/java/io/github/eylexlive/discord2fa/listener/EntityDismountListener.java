package io.github.eylexlive.discord2fa.listener;

import io.github.eylexlive.discord2fa.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.spigotmc.event.entity.EntityDismountEvent;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.3
 */

public class EntityDismountListener implements Listener {

    private final Main plugin;

    public EntityDismountListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void handleEntityDismount(EntityDismountEvent event) {
        if (event.getEntity() instanceof Player) {
            final Player player = (Player) event.getEntity();
            if (event.getDismounted() instanceof ArmorStand) {
                final ArmorStand armorStand = (ArmorStand) event.getDismounted();
                if (plugin.getDiscord2FAManager().isInCheck(player) && plugin.getDiscord2FAManager().getArmorStands().get(player).getUniqueId().toString().equals(armorStand.getUniqueId().toString())) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
