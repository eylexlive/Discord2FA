package io.github.eylexlive.discord2fa.util;

import org.bukkit.ChatColor;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 2.8
 */

public class Color {
    public static String translate(String str){
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}
