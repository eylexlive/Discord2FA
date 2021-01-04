package io.github.eylexlive.discord2fa.util;

import io.github.eylexlive.discord2fa.Discord2FA;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.4
 */

public class ConfigUtil {

    private static final Discord2FA plugin = Discord2FA.getInstance();

    @NotNull
    public static String getString(String path) {
        final String str = plugin.getConfig().getString(path);
        if (str == null)
            return "Key not found!";
        final Pattern pattern = Pattern.compile("&([0-fk-or])");
        if (!pattern.matcher(str).find())
            return str;
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    @NotNull
    public static String getString(String path, String... placeholders) {
        String s = getString(path);
        for (String str : placeholders) {
            final String placeholder = str.split(":")[0];
            final String value = str.replaceFirst(Matcher.quoteReplacement(placeholder + ":"), "");
            s = s.replaceAll("%"+ Matcher.quoteReplacement(placeholder)+"%", value);
        }
        return s;
    }

    public static List<String> getStringList(String path) {
        return plugin.getConfig().getStringList(path);
    }

    public static boolean getBoolean(String path) {
        return plugin.getConfig().getBoolean(path);
    }

    public static int getInt(String path) {
        return plugin.getConfig().getInt(path);
    }

}
