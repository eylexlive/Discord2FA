package io.github.eylexlive.discord2fa.hook;

import io.github.eylexlive.discord2fa.Main;
import org.bukkit.plugin.PluginManager;

import java.util.logging.Logger;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.0
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
        if (!this.enabled)
            return;
        final Logger logger = this.plugin.getLogger();
        logger.info("Hooking into " + this.pluginName);
        final PluginManager pluginManager = this.plugin.getServer().getPluginManager();
        if (pluginManager.getPlugin(this.pluginName) == null) {
            logger.warning("ERROR: There was an error hooking into " + this.pluginName + "!");
            return;
        }
        final HookListener hookListener = new HookListener(
                "io.github.eylexlive.discord2fa.hook.hookevent." + this.pluginName + "Event");
        pluginManager.registerEvents(hookListener.getListener(), this.plugin);
        logger.info("Hooked into " + this.pluginName);
    }
}
