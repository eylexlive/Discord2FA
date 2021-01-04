package io.github.eylexlive.discord2fa.util;

import io.github.eylexlive.discord2fa.Discord2FA;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.4
 */

public class UpdateCheck {

    private final Discord2FA plugin;

    public UpdateCheck(Discord2FA plugin) {
        this.plugin = plugin;
        checkUpdate();
    }

    private void checkUpdate() {
        if (!ConfigUtil.getBoolean("check-for-updates"))
            return;

        log("-----------------------------");
        log("     Discord2FA Updater     ");
        log(" ");
        log("v" + plugin.getDescription().getVersion() + " running now");

        if (isAvailable()) {
            log("A new update is available at");
            log("spigotmc.org/resources/75451");
            log(" ");
        } else {
            log("The last version of");
            log("Discord2FA");
            log(" ");
        }
        log("-----------------------------");
    }

    private boolean isAvailable() {
        final String spigotPluginVersion;
        try {
            final URLConnection urlConnection = new URL(
                    "https://api.spigotmc.org/legacy/update.php?resource=75451"
            ).openConnection();
            spigotPluginVersion = new BufferedReader(
                    new InputStreamReader(
                            urlConnection.getInputStream())
            ).readLine();
        } catch (IOException e) {
            return false;
        }
        return !plugin.getDescription().getVersion().equals(spigotPluginVersion);
    }
    
    private void log(String str) {
        System.out.println(str);
    } 
}
