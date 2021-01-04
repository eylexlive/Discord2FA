package io.github.eylexlive.discord2fa.manager;

import io.github.eylexlive.discord2fa.Discord2FA;
import io.github.eylexlive.discord2fa.hook.PluginHook;
import io.github.eylexlive.discord2fa.util.ConfigUtil;

import java.util.HashMap;
import java.util.Map;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.4
 */

public class HookManager {

    private final Discord2FA plugin;

    private final Map<HookType, Boolean> hookMap = new HashMap<>();

    public HookManager(Discord2FA plugin) {
        this.plugin = plugin;
        registerHooks();
    }

    private void registerHooks() {
        for (HookType hookType : HookType.values()) {
            if (!ConfigUtil.getBoolean(hookType.name().toLowerCase() + "-support"))
                continue;
            final PluginHook pluginHook = new PluginHook(plugin, hookType).register();
            hookMap.put(hookType, pluginHook.isHooked());
        }
    }

    public boolean isAnyPluginHooked() {
        return hookMap.values().stream().anyMatch(bool -> bool);
    }

    public enum HookType {
        AuthMe,
        LoginSecurity,
    }
}
