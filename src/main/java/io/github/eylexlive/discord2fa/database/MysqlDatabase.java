package io.github.eylexlive.discord2fa.database;

import io.github.eylexlive.discord2fa.Main;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 2.8
 */

public class MysqlDatabase {
    private Connection connection;
    private final Main plugin;
    public MysqlDatabase(Main plugin) {
        this.plugin = plugin;
        this.openConnection(false);
        this.createTablesIfNotExits();
    }
    public synchronized void openConnection(boolean isReconnect) {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://"
                            + this.plugin.getConfig().getString("mysql.host")
                            + ":" + this.plugin.getConfig().getInt("mysql.port")
                            + "/" + this.plugin.getConfig().getString("mysql.database")
                            + "?autoReconnect=true"
                            + "&useSSL="
                            + this.plugin.getConfig().getBoolean("mysql.use-ssl")
                            + "&characterEncoding=UTF-8",
                    this.plugin.getConfig().getString("mysql.username"),
                    this.plugin.getConfig().getString("mysql.password")
            );
            this.plugin.getLogger().info("[MySQL] Successfully " + (isReconnect ? "re-" : "") + " connected to database!");
        } catch (SQLException e) {
            e.printStackTrace();
            this.plugin.getLogger().warning("[MySQL] " + (isReconnect ? "Re-" : "") + "Connection to database failed!");
            this.plugin.getLogger().warning("[MySQL] Please make sure that details in config.yml are correct.");
        }
    }
    @SneakyThrows
    private void createTablesIfNotExits() {
        final Statement statement = this.getConnection().createStatement();
        statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS `" + "2fa_backup" + "`(`player` TEXT, `codes` VARCHAR(" + (this.plugin.getConfig().getInt("code-lenght")*10+10)+"))");
        statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS `" + "2fa" + "`(`player` TEXT, `discord` VARCHAR(60), `ip` TEXT)");
    }
    @SneakyThrows
    public Connection getConnection() {
        if (this.connection == null || !this.connection.isValid(1)) {
            this.openConnection(true);
        }
        return this.connection;
    }
}
