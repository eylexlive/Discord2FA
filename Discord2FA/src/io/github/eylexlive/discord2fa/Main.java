package io.github.eylexlive.discord2fa;

import io.github.eylexlive.discord2fa.command.AuthCommand;
import io.github.eylexlive.discord2fa.command.Discord2FACommand;
import io.github.eylexlive.discord2fa.database.MySQLDatabase;
import io.github.eylexlive.discord2fa.database.YAMLDatabase;
import io.github.eylexlive.discord2fa.listener.*;
import io.github.eylexlive.discord2fa.manager.Discord2FAManager;
import io.github.eylexlive.discord2fa.nms.NMS;
import io.github.eylexlive.discord2fa.util.UpdateCheck;
import net.dv8tion.jda.api.JDA;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

/*
*	Created by EylexLive on Feb 23, 2020.
*	Currently version: 2.0
*/

public class Main extends JavaPlugin {
    private static Main instance;
    private Discord2FAManager discord2FAManager;
    private NMS nms;
    private MySQLDatabase mySQLDatabase;
    private YAMLDatabase yamlDatabase;
    @Override
    public void onEnable() {
        instance = this;
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        this.discord2FAManager = new Discord2FAManager();
        this.nms = new NMS();
        this.registerCommands();
        this.registerListeners();
        this.setupDatabase();
        this.transferDataIfExits();
        new LoginBot();
        new UpdateCheck();
    }
    @Override
    public void onDisable() {
        this.saveConfig();
        if (!this.isMySQLEnabled()) this.yamlDatabase.saveDatabaseConfiguration();
    }
    public static Main getInstance() {
        return instance;
    }
    public NMS getNMS() {
        return this.nms;
    }
    public Discord2FAManager getDiscord2FAManager() {
        return this.discord2FAManager;
    }

    private void registerCommands() {
        this.getCommand("auth").setExecutor(new AuthCommand());
        this.getCommand("discord2fa").setExecutor(new Discord2FACommand());
    }
    private void registerListeners() {
        Arrays.asList(new AsyncPlayerChatListener(),new BlockBreakListener(),new BlockPlaceListener(),new EntityDamageByEntityListener(),new InventoryClickListener(),new InventoryOpenListener(),new PlayerCommandUseListener(),new PlayerDropItemListener(),new PlayerInteractListener(),new PlayerJoinListener(),new PlayerQuitListener()).forEach(listener -> this.getServer().getPluginManager().registerEvents(listener,this));
        if (this.getConfig().getBoolean("authme-support")) {
            this.getLogger().info("Registering AuthMe Support...");
            if (this.getServer().getPluginManager().getPlugin("AuthMe") == null) {
                this.getLogger().warning("The AuthMe Support can't registered,because AuthMe plugin is not enabled.");
                return;
            }
            this.getServer().getPluginManager().registerEvents(new AuthmeLoginListener(),this);
            this.getLogger().info("AuthMe support registered!");
        }
    }
    public JDA getBot() {
        return LoginBot.jda;
    }
    private void setupDatabase() {
        if (this.isMySQLEnabled())
            this.mySQLDatabase = new MySQLDatabase();
        else
            this.yamlDatabase = new YAMLDatabase();
    }
    private void transferDataIfExits() {
        if (this.isMySQLEnabled()) return;
        this.yamlDatabase.getDatabaseConfiguration().set("ip-addresses",null);
        List<String> autoSetList = this.yamlDatabase.getDatabaseConfiguration().getStringList("verify-list");
        if (autoSetList.size() < 1) return;
        autoSetList.forEach(key-> {
            String[] split = key.split("/");
            this.yamlDatabase.getDatabaseConfiguration().set("verify."+split[0]+".discord",split[1]);
        });
        this.yamlDatabase.getDatabaseConfiguration().set("verify-list",null);
        this.yamlDatabase.saveDatabaseConfiguration();
    }
    public boolean isAuthmeSupport() {
        return ((this.getServer().getPluginManager().getPlugin("AuthMe") != null || this.getServer().getPluginManager().getPlugin("AuthMeReloaded") != null)  && this.getConfig().getBoolean("authme-support"));
    }
    public MySQLDatabase getMySQLDatabase() { return this.mySQLDatabase; }
    public YAMLDatabase getYamlDatabase(){ return this.yamlDatabase; }
    public boolean isMySQLEnabled() { return this.getConfig().getBoolean("mysql.enabled"); }
}
