package io.github.eylexlive.discord2fa.manager;

import lombok.Getter;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 2.6
 */

public class SitManager {
    @Getter private final Map<Player,ArmorStand> armorStands = new HashMap<>();
    public void sitPlayer(Player player) {
        final ArmorStand armorStand = (ArmorStand) Objects.requireNonNull(
                player.getLocation().getWorld()).spawnEntity(player.getLocation(), EntityType.ARMOR_STAND
        );
        armorStand.setVisible(false);
        armorStand.setPassenger(player);
        this.armorStands.put(player,armorStand);
    }
    public void unSitPlayer(Player player) {
        if (this.armorStands.containsKey(player))
            this.armorStands.get(player).remove();
    }
}
