package io.github.eylexlive.discord2fa;

import io.github.eylexlive.discord2fa.command.AuthCommand;
import io.github.eylexlive.discord2fa.command.Discord2FACommand;
import io.github.eylexlive.discord2fa.database.MySQLDatabase;
import io.github.eylexlive.discord2fa.database.YAMLDatabase;
import io.github.eylexlive.discord2fa.listener.*;
import io.github.eylexlive.discord2fa.manager.Discord2FAManager;
import io.github.eylexlive.discord2fa.manager.SitManager;
import io.github.eylexlive.discord2fa.util.UpdateCheck;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 2.2
 */

public class Main extends JavaPlugin {
    @Getter 
	private static Main instance;
    @Getter 
	private Discord2FAManager discord2FAManager;
    @Getter 
	private SitManager sitManager;
    @Getter 
	private MySQLDatabase mySQLDatabase;
    @Getter 
	private YAMLDatabase yamlDatabase;

    @Override
    public void onEnable() {
        instance = this;
 //       this.saveDefaultConfig();
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        this.discord2FAManager = new Discord2FAManager();
        this.sitManager = new SitManager();
        this.registerCommands();
        this.registerListeners();
        this.hookPlugins();
        this.setupDatabase();
        this.transferDataIfExits();
        new UpdateCheck();
        new LoginBot();
    }
    @Override
    public void onDisable() {
        this.saveConfig();
        if (!this.isMySQLEnabled())
            this.yamlDatabase.saveDatabaseConfiguration();
    }
    private void setupDatabase() {
        if (this.isMySQLEnabled())
            this.mySQLDatabase = new MySQLDatabase();
        else
            this.yamlDatabase = new YAMLDatabase();
            this.getLogger().info("Loading Discord2FA database.yml");
    }
    private void registerCommands() {
        this.getCommand("auth").setExecutor(new AuthCommand());
        this.getCommand("discord2fa").setExecutor(new Discord2FACommand());
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
                new PlayerQuitListener()).
                forEach(listener -> this.getServer().getPluginManager().registerEvents(listener, this));
        }

    private void hookPlugins() {
        if (this.getConfig().getBoolean("authme-support")) {
            this.getLogger().info("Hooking into AuthMe");
            if (this.getServer().getPluginManager().getPlugin("AuthMe") == null) {
                this.getLogger().warning("ERROR: AuthMe is not enabled!");
                 return;
             }
            this.getServer().getPluginManager().registerEvents(new AuthmeLoginListener(),this);
            this.getLogger().info("Hooked into AuthMe");
        }else if (this.getConfig().getBoolean("loginsecurity-support")) {
            this.getLogger().info("Hooking into LoginSecurity");
            if (this.getServer().getPluginManager().getPlugin("LoginSecurity") == null) {
                this.getLogger().warning("ERROR: LoginSecurity is not enabled!");
                return;
            }
            this.getServer().getPluginManager().registerEvents(new LoginSecurityListener(), this);
            this.getLogger().info("Hooked into LoginSecurity");
        }
    }
    private void transferDataIfExits() {
        if (this.isMySQLEnabled())
            return;
        this.yamlDatabase.getDatabaseConfiguration().set("ip-addresses",null);
        List<String> autoSetList = this.yamlDatabase.getDatabaseConfiguration().getStringList("verify-list");
        if (autoSetList.size() < 1)
            return;
        autoSetList.forEach(key-> {
            String[] split = key.split("/");
            this.yamlDatabase.getDatabaseConfiguration().set("verify."+split[0]+".discord",split[1]);
        });
        this.yamlDatabase.getDatabaseConfiguration().set("verify-list",null);
        this.yamlDatabase.saveDatabaseConfiguration();
    }
    public boolean isAuthmeSupport() {
        return ((this.getServer().getPluginManager().getPlugin("AuthMe") != null || this.getServer().getPluginManager().getPlugin("AuthMeReloaded") != null)  && !this.isLoginSecuritySupport() && this.getConfig().getBoolean("authme-support"));
    }
    public boolean isLoginSecuritySupport() {
        return (this.getServer().getPluginManager().getPlugin("LoginSecurity") != null) && !this.isAuthmeSupport() && this.getConfig().getBoolean("loginsecurity-support");
    }
    public boolean isMySQLEnabled() {
        return this.getConfig().getBoolean("mysql.enabled");
    }
    public JDA getBot() {
        return LoginBot.jda;
    }
    public boolean getConnectStatus() { return this.getBot() != null; }
}
