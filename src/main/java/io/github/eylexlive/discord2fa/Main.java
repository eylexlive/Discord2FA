package io.github.eylexlive.discord2fa;

import io.github.eylexlive.discord2fa.bot.Bot;
import io.github.eylexlive.discord2fa.command.*;
import io.github.eylexlive.discord2fa.file.Config;
import io.github.eylexlive.discord2fa.listener.*;
import io.github.eylexlive.discord2fa.manager.*;
import io.github.eylexlive.discord2fa.provider.MySQLProvider;
import io.github.eylexlive.discord2fa.provider.Provider;
import io.github.eylexlive.discord2fa.provider.YamlProvider;
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
 *	Currently version: 3.0
 */


public class Main extends JavaPlugin {

    private static Main instance;

    private Discord2FAManager discord2FAManager;
    private HookManager hookManager;

    private Config config;

    private Provider provider;

    @Override
    public void onEnable() {
        if (instance != null)
            throw new IllegalStateException("Discord2FA already enabled!");
        instance = this;
        config = new Config("config");
        provider = (isMySQLEnabled() ? new MySQLProvider() : new YamlProvider());
        provider.setupDatabase();
        discord2FAManager = new Discord2FAManager(this);
        hookManager = new HookManager(this);
        getCommand("auth").setExecutor(new AuthCommand(this));
        getCommand("discord2fa").setExecutor(new Discord2FACommand(this));
        registerListeners();
        new Metrics(this);
        CompletableFuture.runAsync(() -> {
            new UpdateCheck(this).checkUpdate();
            new Bot(config.getString("bot-token"), this).login();
        });
    }

    @Override
    public void onDisable() {
        discord2FAManager.getArmorStands().values().forEach(Entity::remove);
        provider.saveDatabase();
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

    public @NotNull Config getConfig() {
        return config;
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
        return config.getBoolean("mysql.enabled");
    }

    public boolean getConnectStatus() {
        return Bot.jda != null;
    }

}
