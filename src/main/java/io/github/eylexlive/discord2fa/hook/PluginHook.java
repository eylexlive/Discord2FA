package io.github.eylexlive.discord2fa.hook;

import io.github.eylexlive.discord2fa.Main;
import org.bukkit.plugin.PluginManager;

import java.util.logging.Logger;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.1
 */

public class PluginHook {

    private final Main plugin;

    private final String pluginName;
    private final boolean enabled;

    public PluginHook(String pluginName, Main plugin, boolean enabled) {
        this.pluginName = pluginName;
        this.enabled = enabled;
        this.plugin = plugin;
    }

    public void execute() {
        if (!enabled)
            return;
        final Logger logger = plugin.getLogger();
        logger.info("Hooking into " + pluginName);
        final PluginManager pluginManager = plugin.getServer().getPluginManager();
        if (pluginManager.getPlugin(pluginName) == null) {
            logger.warning("ERROR: There was an error hooking into " + pluginName + "!");
            return;
        }
        final HookListener hookListener = new HookListener(
                "io.github.eylexlive.discord2fa.hook.hookevent." + pluginName + "Event");
        pluginManager.registerEvents(hookListener.getListener(), plugin);
        logger.info("Hooked into " + pluginName);
    }
}
