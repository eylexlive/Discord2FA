package io.github.eylexlive.discord2fa.util;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.0
 */

public class Color {

    public static @NotNull String translate(String str){
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}
