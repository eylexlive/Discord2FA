package io.github.eylexlive.discord2fa.util;

import io.github.eylexlive.discord2fa.Main;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Matcher;

public class ConfigUtil {

    private static final Main plugin = Main.getInstance();

    public static @NotNull String getString(String path) {
        final String str = plugin.getConfig().getString(path);
        if (str == null) return "Key not found!";
        if (!str.contains("&"))
            return str;
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public static @NotNull String getString(String path, String... placeholders) {
        String msg = getString(path);
        for (String str : placeholders) {
            final String placeholder = str.split(":")[0];
            final String value = str.replaceFirst(Matcher.quoteReplacement(placeholder + ":"), "");
            msg = msg.replaceAll("%"+ Matcher.quoteReplacement(placeholder)+"%", value);
        }
        return msg;
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
