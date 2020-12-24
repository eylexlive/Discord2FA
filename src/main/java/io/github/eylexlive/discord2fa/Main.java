package io.github.eylexlive.discord2fa;

import io.github.eylexlive.discord2fa.bot.Bot;
import io.github.eylexlive.discord2fa.command.*;
import io.github.eylexlive.discord2fa.listener.*;
import io.github.eylexlive.discord2fa.manager.*;
import io.github.eylexlive.discord2fa.provider.MySQLProvider;
import io.github.eylexlive.discord2fa.provider.Provider;
import io.github.eylexlive.discord2fa.provider.YamlProvider;
import io.github.eylexlive.discord2fa.util.ConfigUtil;
import io.github.eylexlive.discord2fa.util.Metrics;
import io.github.eylexlive.discord2fa.util.UpdateCheck;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.2
 */

public class Main extends JavaPlugin {

    private static Main instance;

    private Bot bot;

    private Discord2FAManager discord2FAManager;
    private HookManager hookManager;

    private Provider provider;

    @Override
    public void onEnable() {
        if (instance != null)
            throw new IllegalStateException("Discord2FA already enabled!");
        instance = this;

        getConfig().options().copyDefaults(true);
        saveConfig();

        getCommand("auth").setExecutor(new AuthCommand(this));
        getCommand("discord2fa").setExecutor(new Discord2FACommand(this));

        registerListeners();

        discord2FAManager = new Discord2FAManager(this);
        hookManager = new HookManager(this);

        provider = isMySQLEnabled() ? new MySQLProvider() : new YamlProvider();
        provider.setupDatabase();

        new Metrics(this);
        new UpdateCheck(this).checkUpdate();
        CompletableFuture.runAsync(() -> {
            bot = new Bot(ConfigUtil.getString("bot-token"), this).login();
        }).join();
    }

    @Override
    public void onDisable() {
        discord2FAManager.getArmorStands().values().forEach(Entity::remove);
        getServer().getScheduler().cancelTasks(this);
        if (provider != null)
            provider.saveDatabase();
        if (bot != null)
            bot.logout();
    }

    private void registerListeners() {
        final PluginManager pluginManager = getServer().getPluginManager();
        Arrays.asList(
                new AsyncPlayerChatListener(this),
                new BlockBreakListener(this),
                new BlockPlaceListener(this),
                new EntityDamageByEntityListener(this),
                new InventoryClickListener(this),
                new InventoryOpenListener(this),
                new PlayerCommandUseListener(this),
                new PlayerDropItemListener(this),
                new PlayerInteractListener(this),
                new PlayerJoinListener(this),
                new EntityDismountListener(this),
                new PlayerQuitListener(this)
        ).forEach(listener -> pluginManager.registerEvents(listener, this));
    }

    public static @NotNull Main getInstance() {
        return instance;
    }

    public @NotNull Provider getProvider() {
        return provider;
    }

    public @NotNull Discord2FAManager getDiscord2FAManager() {
        return discord2FAManager;
    }

    public @NotNull HookManager getHookManager() {
        return hookManager;
    }

    public boolean isMySQLEnabled() {
        return ConfigUtil.getBoolean("mysql.enabled");
    }

    public boolean isConnected() { return bot.getJDA() != null; }

    public Bot getBot() { return bot; }
}
