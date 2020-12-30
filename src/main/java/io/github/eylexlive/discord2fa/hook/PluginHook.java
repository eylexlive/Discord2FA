package io.github.eylexlive.discord2fa.hook;

import io.github.eylexlive.discord2fa.Discord2FA;
import io.github.eylexlive.discord2fa.manager.HookManager;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import java.util.logging.Logger;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.3
 */

public class PluginHook {

    private final Discord2FA plugin;

    private final HookManager.HookType hookType;

    private boolean hooked = false;

    public PluginHook(Discord2FA plugin, HookManager.HookType hookType) {
        this.plugin = plugin;
        this.hookType = hookType;
    }

    public PluginHook register() {
        final Logger logger = plugin.getLogger();
        final PluginManager pluginManager = plugin.getServer().getPluginManager();

        logger.info("Hooking into " + hookType.name());

        if (pluginManager.getPlugin(hookType.name()) == null) {
            logger.warning("ERROR: There was an error hooking into " + hookType.name() + "!");
            return this;
        }

        try {
            final Class<Listener> listenerClass = (Class<Listener>) Class.forName("io.github.eylexlive.discord2fa.hook.hookevent." + hookType.name() + "Event");
            pluginManager.registerEvents(listenerClass.newInstance(), plugin);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            return this;
        }

        hooked = true;
        logger.info("Hooked into " + hookType.name());
        return this;
    }

    public boolean isHooked() {
        return hooked;
    }
}
