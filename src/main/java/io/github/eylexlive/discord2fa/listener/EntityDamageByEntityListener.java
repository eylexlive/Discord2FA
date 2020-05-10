package io.github.eylexlive.discord2fa.listener;

import io.github.eylexlive.discord2fa.Main;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 2.1
 */

public class EntityDamageByEntityListener implements Listener {
    private Main plugin;
    public EntityDamageByEntityListener() {
        this.plugin = Main.getInstance();
    }
    @EventHandler
    public void handleEntityDamageEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if (this.plugin.getDiscord2FAManager().isInCheck(player)) {
                event.setCancelled(true);
            }
        }else if (!(event.getDamager() instanceof  Player) && event.getEntity() instanceof  Player) {
            if (this.plugin.getDiscord2FAManager().isInCheck((Player)event.getEntity())) {
                event.setCancelled(true);
            }
        } else if (event.getEntity() instanceof Player && event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile)event.getDamager();
            ProjectileSource projectileSource = projectile.getShooter();
            if (projectileSource instanceof Player) {
                Player player = (Player)projectileSource;
                if (this.plugin.getDiscord2FAManager().isInCheck(player)) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
