package io.github.eylexlive.discord2fa.manager;

import io.github.eylexlive.discord2fa.Main;
import io.github.eylexlive.discord2fa.hook.HookType;
import io.github.eylexlive.discord2fa.hook.PluginHook;
import io.github.eylexlive.discord2fa.util.ConfigUtil;

import java.util.Arrays;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.3
 */

public class HookManager {

    private final Main plugin;

    public HookManager(Main plugin) {
        this.plugin = plugin;
        Arrays.asList("Authme", "LoginSecurity").forEach(str -> new PluginHook(str, plugin, ConfigUtil.getBoolean(str.toLowerCase() + "-support")).execute());
    }

    public boolean isPluginSupport(HookType pluginName) {
        final String name = pluginName.name().toLowerCase();
        return (plugin.getServer().getPluginManager().getPlugin(name) != null) &&
                ConfigUtil.getBoolean(name + "-support");
    }
}
