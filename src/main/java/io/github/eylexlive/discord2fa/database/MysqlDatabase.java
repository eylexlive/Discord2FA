package io.github.eylexlive.discord2fa.database;

import io.github.eylexlive.discord2fa.Main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 2.4
 */

public class MysqlDatabase {
    private Connection connection;
    private Main plugin;
    public MysqlDatabase(Main plugin) {
        this.plugin = plugin;
        this.openConnection(false);
        this.createTablesIfNotExits();
    }
    public void openConnection(boolean isReconnect) {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" +
                            this.plugin.getConfig().getString("mysql.host") + ":" +
                            this.plugin.getConfig().getInt("mysql.port") + "/" +
                            this.plugin.getConfig().getString("mysql.database") +
                            "?useSSL=" + this.plugin.getConfig().getBoolean("mysql.use-ssl") +
                            "&characterEncoding=UTF-8&autoReconnect=true",
                    this.plugin.getConfig().getString("mysql.username"),
                    this.plugin.getConfig().getString("mysql.password"));
            this.plugin.getLogger().info("[MySQL] Successfully " + (isReconnect ? "re-" : "") + "connected to database!");
        } catch (SQLException e) {
            e.printStackTrace();
            this.plugin.getLogger().warning("[MySQL] "+ (isReconnect ? "Re-" : "") + "Connection to database failed!");
            if (isReconnect) return;
            this.plugin.getLogger().warning("[MySQL] Please make sure that details in config.yml are correct.");
        }
    }
    public Connection getConnection() {
        if (this.connection == null)
            this.openConnection(true);
        return this.connection;
    }
    private void createTablesIfNotExits() {
        try {
            Statement statement = this.getConnection().createStatement();
            try {
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS `" + "2fa_backup" + "`(`player` TEXT, `codes` VARCHAR("+(this.plugin.getConfig().getInt("code-lenght")*10+10)+"))");
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS `" + "2fa" + "`(`player` TEXT, `discord` VARCHAR(60), `ip` TEXT)");
            } catch (SQLException e) {
                System.out.print(e.getMessage());
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
