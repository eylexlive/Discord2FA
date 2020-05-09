package io.github.eylexlive.discord2fa.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/*
*	Created by EylexLive on Feb 23, 2020.
*	Currently version: 2.0
*/

public class Utils {
    public static String getServerVersion() { return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3]; }
    public static String translate(String str){
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}
