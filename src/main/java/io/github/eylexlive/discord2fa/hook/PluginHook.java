package io.github.eylexlive.discord2fa.hook;

import io.github.eylexlive.discord2fa.Main;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 2.8
 */

public class PluginHook  {
    private final String pluginName;
    private final boolean enabled;
    private final Main plugin;
    public PluginHook(String pluginName, Main plugin, boolean enabled) {
        this.pluginName = pluginName;
        this.enabled = enabled;
        this.plugin = plugin;
    }
    public void hook() {
        if (!this.enabled)
            return;
        this.plugin.getLogger().info("Hooking into " + this.pluginName);
        if (this.plugin.getServer().getPluginManager().getPlugin(this.pluginName) == null) {
            this.plugin.getLogger().warning("ERROR: There was an error hooking into " + this.pluginName + "!");
            return;
        }
        final HookListener hookListener = new HookListener(
                "io.github.eylexlive.discord2fa.hook.hookevent." + this.pluginName + "Event");
        this.plugin.getServer().getPluginManager().registerEvents(hookListener.getListener(), this.plugin);
        this.plugin.getLogger().info("Hooked into " + this.pluginName);
    }
}
