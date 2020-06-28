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
 *	Currently version: 2.4
 */

public class UpdateCheck {
    private Main plugin;
    public UpdateCheck(Main plugin) {
        this.plugin = plugin;
    }
   public void checkUpdate() {
       System.out.println("-----------------------------");
       System.out.println("     Discord2FA Updater     ");
       System.out.println(" ");
       System.out.println("v" + this.plugin.getDescription().getVersion() + " running now");
       if (this.isAvailable()) {
           System.out.println("A new update is available at");
           System.out.println("spigotmc.org/resources/75451");
           System.out.println(" ");
       } else {
           System.out.println("The last version of");
           System.out.println("Discord2FA");
           System.out.println(" ");
       }
       System.out.println("-----------------------------");
   }
   private boolean isAvailable() {
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
