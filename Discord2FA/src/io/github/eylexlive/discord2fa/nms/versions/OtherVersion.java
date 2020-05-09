package io.github.eylexlive.discord2fa.nms.versions;

import io.github.eylexlive.discord2fa.SitInterface;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/*
*	Created by EylexLive on Feb 23, 2020.
*	Currently version: 2.0
*/

public class OtherVersion implements SitInterface {
    @Override
    public void sitPlayer(Player player, Location location) {
        location.setYaw(90.0F);
        player.teleport(location);
    }
    @Override
    public void unsitPlayer(Player player) {
    }
}
