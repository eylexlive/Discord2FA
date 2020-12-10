package io.github.eylexlive.discord2fa.provider;

import io.github.eylexlive.discord2fa.Main;
import io.github.eylexlive.discord2fa.event.AuthCompleteEvent;
import io.github.eylexlive.discord2fa.manager.Discord2FAManager;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.1
 */

public class MySQLProvider extends Provider {

    private final Main plugin = Main.getInstance();
    private Connection connection;

    private String getData(Player player, String sqlPath, String sqlTable) {
        try {
            final PreparedStatement statement =  getConnection().prepareStatement(
                    "SELECT * FROM `"+ sqlTable +"` WHERE `player` = ?;");
            statement.setString(1, player.getName());
            final ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString(sqlPath);
            }
        } catch (SQLException exception) {
            plugin.getLogger().warning("Data cannot be getting:");
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    public void setupDatabase() {
        final Logger logger = plugin.getLogger();
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://"
                            + plugin.getConfig().getString("mysql.host")
                            + ":" + plugin.getConfig().getInt("mysql.port")
                            + "/" + plugin.getConfig().getString("mysql.database")
                            + "?autoReconnect=true"
                            + "&useSSL="
                            + plugin.getConfig().getBoolean("mysql.use-ssl")
                            + "&characterEncoding=UTF-8",
                    plugin.getConfig().getString("mysql.username"),
                    plugin.getConfig().getString("mysql.password")
            );
            logger.info("[MySQL] Successfully connected to the database!");
            final Statement statement = this.getConnection().createStatement();
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS `" + "2fa_backup" + "`(`player` TEXT, `codes` VARCHAR(" + (this.plugin.getConfig().getInt("code-lenght") * 10 + 10)+"))");
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS `" + "2fa" + "`(`player` TEXT, `discord` VARCHAR(60), `ip` TEXT)");
        } catch (SQLException exception) {
            logger.warning("[MySQL] Connection to database failed!");
            logger.warning("[MySQL] Please make sure that details in config.yml are correct.");
            exception.printStackTrace();
        }

    }

    @Override
    public void saveDatabase() {
        try {
            getConnection().close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void addToVerifyList(Player player, String discord) {
        if (playerExits(player))
            return;
        PreparedStatement statement;
        try {
            statement = getConnection().prepareStatement(
                    "INSERT INTO `2fa` (player, discord, ip)" + "VALUES " + "(?, ?, ?);");
            statement.setString(1, player.getName());
            statement.setString(2, discord);
            statement.setString(3, "CURRENTLY_NULL");
            statement.executeUpdate();

            statement = getConnection().prepareStatement(
                    "INSERT INTO `2fa_backup` (player, codes)" + "VALUES " + "(?, ?);");
            statement.setString(1, player.getName());
            statement.setString(2,"CURRENTLY_NULL");
            statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void removeFromVerifyList(Player player) {
        if (!playerExits(player))
            return;
        try {
            final PreparedStatement statement = getConnection().prepareStatement(
                    "DELETE FROM " + "`2fa`" + " WHERE player= '" + player.getName() + "';");
            statement.executeUpdate();
            statement.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void authPlayer(Player player) {
        try {
            final PreparedStatement statement = getConnection().prepareStatement(
                    "UPDATE `2fa` SET `ip` = ? WHERE `player` = ?;");
            statement.setString(1, String.valueOf(player.getAddress().getAddress().getHostAddress()));
            statement.setString(2, player.getName());
            statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        final Discord2FAManager discord2FAManager = plugin.getDiscord2FAManager();
        discord2FAManager.removePlayerFromCheck(player);
        discord2FAManager.getLeftRights().put(player.getUniqueId(), null);
        discord2FAManager.getCheckCode().put(player.getUniqueId(), null);
        plugin.getLogger().info(player.getName() + "'s account was authenticated!");
        final List<String> adminIds = plugin.getConfig().getStringList("logs.admin-ids");
        if (plugin.getConfig().getBoolean("logs.enabled"))
            discord2FAManager.sendLog(adminIds, plugin.getConfig().getString("logs.player-authenticated")
                    .replace("%player%",player.getName()));
        plugin.getServer().getPluginManager().callEvent(new AuthCompleteEvent(player));
    }

    @Override
    public List<String> generateBackupCodes(Player player) {
        final StringBuilder codes = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            codes.append(plugin.getDiscord2FAManager().getRandomCode(
                    plugin.getConfig().getInt("code-lenght"))
            ).append("-");
        }
        final PreparedStatement statement;
        if (!isBackupCodesGenerated(player)) {
            try {
                statement = getConnection().prepareStatement(
                        "INSERT INTO `2fa_backup` (player, codes)" + "VALUES " + "(?, ?);");
                statement.setString(1, player.getName());
                statement.setString(2, codes.toString());
                statement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        } else {
            try {
                statement = getConnection().prepareStatement(
                        "UPDATE `2fa_backup` SET `codes` = ? WHERE `player` = ?;");
                statement.setString(1, codes.toString());
                statement.setString(2,player.getName());
                statement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
        return Arrays.asList(codes.toString().split("-"));
    }

    @Override
    public void removeBackupCode(Player player, String code) {
        if (!isBackupCode(player, code))
            return;
        final String codeData = getData(player,"codes","2fa_backup");
        final List<String> codesWithList = new ArrayList<>(Arrays.asList(codeData.split("-")));
        codesWithList.remove(code);
        final StringBuilder codes  = new StringBuilder();
        for (String c: codesWithList) {
            codes.append(c).append("-");
        }
        try {
            final PreparedStatement statement = getConnection().prepareStatement(
                    "UPDATE `2fa_backup` SET `codes` = ? WHERE `player` = ?;");
            statement.setString(1, codes.toString());
            statement.setString(2,player.getName());
            statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public boolean isBackupCodesGenerated(Player player) {
        return getData(player,"codes","2fa_backup") != null;
    }

    @Override
    public boolean isBackupCode(Player player, String code) {
        final String codeData = getData(player,"codes","2fa_backup");
        if (codeData == null)
            return false;
        final List<String> codesWithList = new ArrayList<>(Arrays.asList(codeData.split("-")));
        return codesWithList.contains(code) && !code.equals("CURRENTLY_NULL");
    }

    @Override
    public boolean playerExits(Player player) {
        return getData(player,"discord","2fa") != null;
    }

    @Override
    public String getIP(Player player) {
        return getData(player, "ip", "2fa");
    }

    @Override
    public String getMemberID(Player player) {
        return getData(player,  "discord", "2fa");
    }

    @Override
    public String getListMessage() {
        return "§cThis mode is turned off while mysql is enabled right now.";
    }

    public Connection getConnection() {
        try {
            if (connection == null || !connection.isValid(1))
                setupDatabase();
        } catch (SQLException exception) {
            plugin.getLogger().warning("[MySQL] Re-connection to the database failed!");
            exception.printStackTrace();
        }
        return connection;
    }
}
