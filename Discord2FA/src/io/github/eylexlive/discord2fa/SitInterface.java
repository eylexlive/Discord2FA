package io.github.eylexlive.discord2fa;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/*
*	Created by EylexLive on Feb 23, 2020.
*	Currently version: 2.0
*/

public interface SitInterface {
    public void sitPlayer(Player player, Location location);
    public void unsitPlayer(Player player);
}
