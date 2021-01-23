package io.github.eylexlive.discord2fa.task;

import io.github.eylexlive.discord2fa.data.PlayerData;
import io.github.eylexlive.discord2fa.manager.Discord2FAManager;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.5
 */

public class Cancel2FAReqTask extends BukkitRunnable {

    private final Discord2FAManager manager;

    private final PlayerData playerData;
    private final Player player;

    private final boolean state;

    public Cancel2FAReqTask(Discord2FAManager manager, Player player, boolean state) {
        this.manager = manager;
        this.player = player;
        this.playerData = manager.getPlayerData(player);
        this.state = state;
    }

    @Override
    public void run() {
        final String code = playerData.getConfirmCode();
        if (code == null)
            return;
        final boolean condition = state == code.equals("§");
        if (condition) manager.cancel2FAReq(player);
    }
}
