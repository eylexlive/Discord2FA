package io.github.eylexlive.discord2fa.task;

import io.github.eylexlive.discord2fa.data.PlayerData;
import io.github.eylexlive.discord2fa.manager.Discord2FAManager;
import io.github.eylexlive.discord2fa.util.ConfigUtil;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.4
 */

public class CountdownTask extends BukkitRunnable {

    private final Discord2FAManager manager;

    private int countdown;

    private final PlayerData playerData;
    private final Player player;

    public CountdownTask(Discord2FAManager manager, Player player) {
        this.player = player;
        this.manager = manager;
        this.playerData = manager.getPlayerData(player);
        this.countdown = ConfigUtil.getInt("auth-countdown") - 1;
    }

    @Override
    public void run() {
        if (countdown <= 0){
            cancel();
            if (playerData.getLeftRights() > 0) {
                playerData.setLeftRights(playerData.getLeftRights() - 1);
                player.kickPlayer(
                        ConfigUtil.getString(
                                "messages.kick-message",
                                "rights:" + playerData.getLeftRights()
                        )
                );
            } else {
                manager.failPlayer(player);
            }
            return;
        } else if (!manager.isInCheck(player)) {
            cancel();
            return;
        } else if (countdown % 10 == 0 && countdown > 6 || countdown < 6 && countdown > 0) {
            player.sendMessage(manager.getAuthMessage(false, countdown));
        }
        countdown--;
    }
}
