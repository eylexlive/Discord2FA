package io.github.eylexlive.discord2fa;

import io.github.eylexlive.discord2fa.bot.Bot;
import io.github.eylexlive.discord2fa.command.AuthCommand;
import io.github.eylexlive.discord2fa.command.Discord2FACommand;
import io.github.eylexlive.discord2fa.database.MysqlDatabase;
import io.github.eylexlive.discord2fa.database.YmlDatabase;
import io.github.eylexlive.discord2fa.listener.*;
import io.github.eylexlive.discord2fa.manager.*;
import io.github.eylexlive.discord2fa.util.Metrics;
import io.github.eylexlive.discord2fa.util.UpdateCheck;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

/*
 *	Created by EylexLive on Feb 23, 2020.
<<<<<<< HEAD
 *	Currently version: 2.5
=======
 *	Currently version: 2.4
>>>>>>> dfad98a4fc7847a4362b00b745bb4d6b273ef8dd
 */

public class Main extends JavaPlugin {
    @Getter private static Main instance;
    @Getter public MysqlDatabase mySQLDatabase;
    @Getter public YmlDatabase yamlDatabase;
    @Getter private Discord2FAManager discord2FAManager;
    @Getter private SitManager sitManager;
    @Getter private LogManager logManager;
    @Override
    public void onEnable() {
        instance = this;
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        this.registerManagers();
        this.registerCommands();
        this.registerListeners();
        new Metrics(this);
        new HookManager(this).hook();
        new DatabaseManager(this).setup();
        new UpdateCheck(this).checkUpdate();
        new Bot(this.getConfig().getString("bot-token"), this).login();
    }
    @Override
    public void onDisable() {
        this.saveConfig();
        this.sitManager.getArmorStands().values().forEach(Entity::remove);
        if (!this.isMySQLEnabled()) {
            this.yamlDatabase.saveDatabaseConfiguration();
        }
    }
    private void registerListeners() {
        PluginManager pluginManager = this.getServer().getPluginManager();
        Arrays.asList(new AsyncPlayerChatListener(this), new BlockBreakListener(this), new BlockPlaceListener(this), new EntityDamageByEntityListener(this), new InventoryClickListener(this), new InventoryOpenListener(this), new PlayerCommandUseListener(this), new PlayerDropItemListener(this), new PlayerInteractListener(this), new PlayerJoinListener(this), new EntityDismountListener(this), new PlayerQuitListener(this)).forEach(listener ->
                pluginManager.registerEvents(listener, this)
        );
    }
    private void registerManagers() {
        this.discord2FAManager = new Discord2FAManager(this);
        this.sitManager = new SitManager();
        this.logManager = new LogManager(this);
    }
    private void registerCommands() {
        this.getCommand("auth").setExecutor(new AuthCommand(this));
        this.getCommand("discord2fa").setExecutor(new Discord2FACommand(this));
    }
    public boolean isAuthmeSupport() { return ((this.getServer().getPluginManager().getPlugin("AuthMe") != null || this.getServer().getPluginManager().getPlugin("AuthMeReloaded") != null)  && !this.isLoginSecuritySupport() && this.getConfig().getBoolean("authme-support")); }
    public boolean isLoginSecuritySupport() { return (this.getServer().getPluginManager().getPlugin("LoginSecurity") != null) && !this.isAuthmeSupport() && this.getConfig().getBoolean("loginsecurity-support"); }
    public boolean isMySQLEnabled() { return this.getConfig().getBoolean("mysql.enabled"); }
    public JDA getBot() { return Bot.jda; }
    public boolean getConnectStatus() { return this.getBot() != null; }
}
