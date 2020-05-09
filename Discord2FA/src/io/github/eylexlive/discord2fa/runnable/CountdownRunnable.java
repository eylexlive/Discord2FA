package io.github.eylexlive.discord2fa.runnable;

import io.github.eylexlive.discord2fa.Main;
import io.github.eylexlive.discord2fa.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/*
*	Created by EylexLive on Feb 23, 2020.
*	Currently version: 2.0
*/

public class CountdownRunnable extends BukkitRunnable {
    private Main plugin;
    private int countdown;
    private Player player;
    private Location playerLoc;

    public CountdownRunnable(Player player, Location location) {
        this.player = player;
        this.playerLoc = location;
        this.plugin = Main.getInstance();
        this.countdown = this.plugin.getConfig().getInt("auth-countdown") - 1;
    }
    @Override
    public void run() {
        if (countdown <= 0){
            this.cancel();
            if (this.plugin.getDiscord2FAManager().getLeftRights(player) > 0) {
                this.plugin.getDiscord2FAManager().setLeftRights(player,this.plugin.getDiscord2FAManager().getLeftRights(player)-1);
                String message = this.plugin.getConfig().getString("messages.kick-message");
                message = message.replace("%rights%",String.valueOf(this.plugin.getDiscord2FAManager().getLeftRights(player)));
                player.kickPlayer(Utils.translate(message));
            }else {
                new BukkitRunnable() {
                    private Main plugin = Main.getInstance();
                    @Override
                    public void run() {
                        this.plugin.getDiscord2FAManager().setLeftRights(player,Integer.parseInt(this.plugin.getConfig().getString("number-of-rights")));
                        String command = this.plugin.getConfig().getString("rights-reached-console-command");
                        command = command.replace("%player%",player.getName());
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),command);
                    }
                }.runTaskLater(this.plugin,2L);
            }
            return;
        }else if (!this.plugin.getDiscord2FAManager().isInCheck(player)) {
            this.cancel();
            return;
        }else if (countdown % 10 == 0 && countdown > 6) {
            player.sendMessage(this.plugin.getDiscord2FAManager().getAuthMessage(false,countdown));
        }else if (countdown < 6 && countdown > 0) {
            player.sendMessage(this.plugin.getDiscord2FAManager().getAuthMessage(false,countdown));
        }
        this.plugin.getNMS().getSitInterface().sitPlayer(player,playerLoc);
        countdown--;
    }
}
