package io.github.eylexlive.discord2fa.manager;

import io.github.eylexlive.discord2fa.Main;
import io.github.eylexlive.discord2fa.hook.HookType;
import io.github.eylexlive.discord2fa.hook.PluginHook;

import java.util.Arrays;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.2
 */

public class HookManager {

    private final Main plugin;

    public HookManager(Main plugin) {
        this.plugin = plugin;
        Arrays.asList("Authme", "LoginSecurity").forEach(hookPl ->  {
            final PluginHook pluginHook = new PluginHook(
                    hookPl, plugin, plugin.getConfig().getBoolean(hookPl.toLowerCase() + "-support")
            );
            pluginHook.execute();
        });
    }

    public boolean isPluginSupport(HookType pluginName) {
        final String name = pluginName.name().toLowerCase();
        return (plugin.getServer().getPluginManager().getPlugin(name) != null) &&
                plugin.getConfig().getBoolean(name + "-support");
    }
}
