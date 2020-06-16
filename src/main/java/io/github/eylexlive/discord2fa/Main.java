package io.github.eylexlive.discord2fa;

import io.github.eylexlive.discord2fa.bot.Bot;
import io.github.eylexlive.discord2fa.command.AuthCommand;
import io.github.eylexlive.discord2fa.command.Discord2FACommand;
import io.github.eylexlive.discord2fa.database.MysqlDb;
import io.github.eylexlive.discord2fa.database.YmlDb;
import io.github.eylexlive.discord2fa.hook.PluginHook;
import io.github.eylexlive.discord2fa.listener.*;
import io.github.eylexlive.discord2fa.manager.Discord2FAManager;
import io.github.eylexlive.discord2fa.manager.SitManager;
import io.github.eylexlive.discord2fa.util.UpdateCheck;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 2.3
 */

public class Main extends JavaPlugin {
    @Getter private static Main instance;
    @Getter private Discord2FAManager discord2FAManager;
    @Getter private SitManager sitManager;
    @Getter private MysqlDb mySQLDatabase;
    @Getter private YmlDb yamlDatabase;
    @Override
    public void onEnable() {
        instance = this;
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        this.discord2FAManager = new Discord2FAManager();
        this.sitManager = new SitManager();
        this.registerCommands();
        this.registerListeners();
        this.hookPlugins();
        this.setupDatabase();
        new UpdateCheck(instance).checkUpdate();
        new Bot(this.getConfig().getString("bot-token"), instance).login();
    }
    @Override
    public void onDisable() {
        this.saveConfig();
        if (!this.isMySQLEnabled())
            this.yamlDatabase.saveDatabaseConfiguration();
    }
    private void setupDatabase() {
        if (this.isMySQLEnabled())
            this.mySQLDatabase = new MysqlDb();
        else
            this.yamlDatabase = new YmlDb();
    }
    private void registerListeners() {
        Arrays.asList(new AsyncPlayerChatListener(),
                new BlockBreakListener(),
                new BlockPlaceListener(),
                new EntityDamageByEntityListener(),
                new InventoryClickListener(),
                new InventoryOpenListener(),
                new PlayerCommandUseListener(),
                new PlayerDropItemListener(),
                new PlayerInteractListener(),
                new PlayerJoinListener(),
                new EntityDismountListener(),
                new PlayerQuitListener())
                .forEach(listener ->
                        this.getServer().getPluginManager().registerEvents(listener, this)
                );
    }
    private void registerCommands() {
        this.getCommand("auth").setExecutor(new AuthCommand());
        this.getCommand("discord2fa").setExecutor(new Discord2FACommand());
    }
    private void hookPlugins() {
        Arrays.asList("Authme", "LoginSecurity").forEach(plugin ->  {
            PluginHook pluginHook = new PluginHook(plugin,
                    instance, this.getConfig().getBoolean(plugin.toLowerCase() + "-support")
            );
            pluginHook.hookPlugin();
        });
    }
    public boolean isAuthmeSupport() { return ((this.getServer().getPluginManager().getPlugin("AuthMe") != null || this.getServer().getPluginManager().getPlugin("AuthMeReloaded") != null)  && !this.isLoginSecuritySupport() && this.getConfig().getBoolean("authme-support")); }
    public boolean isLoginSecuritySupport() { return (this.getServer().getPluginManager().getPlugin("LoginSecurity") != null) && !this.isAuthmeSupport() && this.getConfig().getBoolean("loginsecurity-support"); }
    public boolean isMySQLEnabled() { return this.getConfig().getBoolean("mysql.enabled"); }
    public JDA getBot() { return Bot.jda; }
    public boolean getConnectStatus() { return this.getBot() != null; }
}
