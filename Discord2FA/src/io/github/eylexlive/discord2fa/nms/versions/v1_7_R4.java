package io.github.eylexlive.discord2fa.nms.versions;

import io.github.eylexlive.discord2fa.SitInterface;
import io.github.eylexlive.discord2fa.manager.Discord2FAManager;
import net.minecraft.server.v1_7_R4.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

/*
*	Created by EylexLive on Feb 23, 2020.
*	Currently version: 2.0
*/

public class v1_7_R4 implements SitInterface {
    @Override
    public void sitPlayer(Player player, Location location) {
        EntityPlayer ep = ((CraftPlayer) player).getHandle();
        Location loc = player.getLocation();

        EntityBat bat = new EntityBat(((CraftWorld) player.getWorld()).getHandle());
        bat.setPosition(loc.getX(), loc.getY(), loc.getZ());
        bat.setInvisible(true);
        bat.setHealth(6F);

        Discord2FAManager.entityIds.put(player, bat.getId());

        ep.playerConnection.sendPacket(new PacketPlayOutSpawnEntityLiving(bat));
        ep.playerConnection.sendPacket(new PacketPlayOutAttachEntity(0, ep, bat));
    }

    @Override
    public void unsitPlayer(Player player) {
        if (Discord2FAManager.entityIds.containsKey(player)) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(Discord2FAManager.entityIds.get(player)));
        }
    }
}
