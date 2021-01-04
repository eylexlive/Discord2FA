package io.github.eylexlive.discord2fa;

import io.github.eylexlive.discord2fa.bot.Bot;
import io.github.eylexlive.discord2fa.command.*;
import io.github.eylexlive.discord2fa.listener.*;
import io.github.eylexlive.discord2fa.manager.*;
import io.github.eylexlive.discord2fa.provider.MySQLProvider;
import io.github.eylexlive.discord2fa.provider.Provider;
import io.github.eylexlive.discord2fa.provider.YamlProvider;
import io.github.eylexlive.discord2fa.util.Config;
import io.github.eylexlive.discord2fa.util.ConfigUtil;
import io.github.eylexlive.discord2fa.util.Metrics;
import io.github.eylexlive.discord2fa.util.UpdateCheck;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.4
 */

public class Discord2FA extends JavaPlugin {

    private static Discord2FA instance;

    private Bot bot;

    private Config config;

    private Discord2FAManager discord2FAManager;
    private HookManager hookManager;

    private Provider provider;

    @Override
    public void onEnable() {
        if (instance != null) throw new IllegalStateException("Discord2FA already enabled!");

        instance = this;

        config = new Config("config");

        getCommand("auth").setExecutor(new AuthCommand(this));
        getCommand("discord2fa").setExecutor(new Discord2FACommand(this));

        registerListeners();

        discord2FAManager = new Discord2FAManager(this);
        hookManager = new HookManager(this);

        provider = (
                isMYSQLEnabled() ? new MySQLProvider(this)
                        :
                        new YamlProvider(this)
        );
        provider.setupDatabase();

        new Metrics(this);
        new UpdateCheck(this);

        bot = new Bot(this);
        bot.login();

    }

    @Override
    public void onDisable() {
        discord2FAManager.getArmorStands().values().forEach(Entity::remove);
        getServer().getScheduler().cancelTasks(this);
        discord2FAManager.getCheckPlayers().forEach(player -> player.kickPlayer("§cServer closed or Discord2FA reloaded!"));

        if (provider != null) provider.saveDatabase();

        if (bot != null) bot.logout();
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
                new ConnectionListener(this),
                new EntityDismountListener(this)
        ).forEach(listener ->
                pluginManager.registerEvents(listener, this)
        );
    }

    @NotNull
    public static Discord2FA getInstance() {
        return instance;
    }

    @NotNull
    public Bot getBot() {
        return bot;
    }

    @NotNull
    public Config getConfig() {
        return config;
    }

    @NotNull
    public Discord2FAManager getDiscord2FAManager() {
        return discord2FAManager;
    }

    @NotNull
    public HookManager getHookManager() {
        return hookManager;
    }

    @NotNull
    public Provider getProvider() {
        return provider;
    }

    public boolean isConnected() {
        return bot.getJDA() != null;
    }

    public boolean isMYSQLEnabled() {
        return ConfigUtil.getBoolean("mysql.enabled");
    }
}
