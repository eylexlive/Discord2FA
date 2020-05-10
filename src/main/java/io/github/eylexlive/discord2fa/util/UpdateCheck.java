package io.github.eylexlive.discord2fa.util;

import io.github.eylexlive.discord2fa.Main;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 2.1
 */

public class UpdateCheck {
    private Main plugin;
    public UpdateCheck() {
        this.plugin = Main.getInstance();
        this.checkForUpdate();
    }
   private void checkForUpdate() {
       new BukkitRunnable() {
           @Override
           public void run() {
               plugin.getLogger().info("Checking for updates...");
               if (isAvailable()) {
                   plugin.getLogger().warning("A new update is available at: https://www.spigotmc.org/resources/75451/updates");
               } else {
                   plugin.getLogger().info("No update found!");
               }
           }
       }.runTaskLater(this.plugin,40L);
   }
   public boolean isAvailable() {
       URLConnection urlConnection;
       String spigotPluginVersion;
       try {
           urlConnection = new URL("https://api.spigotmc.org/legacy/update.php?resource=75451").openConnection();
           spigotPluginVersion = (new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))).readLine();
       } catch (IOException e) {
           return false;
       }
       return !this.plugin.getDescription().getVersion().equals(spigotPluginVersion);
   }
}
