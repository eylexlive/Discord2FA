package io.github.eylexlive.discord2fa.provider;

import com.zaxxer.hikari.HikariDataSource;
import io.github.eylexlive.discord2fa.Discord2FA;
import io.github.eylexlive.discord2fa.util.ConfigUtil;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.3
 */

public class MySQLProvider extends Provider {

    private final Discord2FA plugin = Discord2FA.getInstance();

    private HikariDataSource dataSource;

    private String getData(Player player, String sqlPath, String sqlTable) {
        try (Connection connection = getConnection()) {
            final PreparedStatement statement =  connection.prepareStatement(
                    "SELECT * FROM `"+ sqlTable +"` WHERE `player` = ?;");
            statement.setString(1, player.getName());
            final ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
                return resultSet.getString(sqlPath);
        } catch (SQLException e) {
            plugin.getLogger().warning("Data cannot be getting:");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setupDatabase() {
        dataSource = new HikariDataSource();
        dataSource.setPoolName("Discord2FAMYSQLPool");

        dataSource.setJdbcUrl(
                "jdbc:mysql://" +
                        ConfigUtil.getString("mysql.host") +
                        ":" +
                        ConfigUtil.getInt("mysql.port") +
                        "/" +
                        ConfigUtil.getString("mysql.database")
        );

        dataSource.setUsername(ConfigUtil.getString("mysql.username"));
        dataSource.setPassword(ConfigUtil.getString("mysql.password"));

        dataSource.addDataSourceProperty("autoReconnect", "true");
        dataSource.addDataSourceProperty("autoReconnectForPools", "true");

        dataSource.addDataSourceProperty("characterEncoding", "UTF-8");
        dataSource.addDataSourceProperty("useSSL", String.valueOf(ConfigUtil.getBoolean("mysql.use-ssl")));

        try (Connection connection = getConnection()) {
            connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS `" + "2fa_backup" + "`(`player` TEXT, `codes` VARCHAR(" + (ConfigUtil.getInt("code-lenght") * 10 + 10)+"))"
            ).execute();

            connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS `" + "2fa" + "`(`player` TEXT, `discord` VARCHAR(60), `ip` TEXT)"
            ).execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        plugin.getLogger().info("[MySQL] Successfully connected to the database!");
    }

    @Override
    public void saveDatabase() {
        if (dataSource != null && !dataSource.isClosed())
            dataSource.close();
    }

    @Override
    public void addToVerifyList(Player player, String discord) {
        if (playerExits(player))
            return;

        try (Connection connection = getConnection()) {
            final PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO `2fa` (player, discord)" + "VALUES " + "(?, ?);");
            statement.setString(1, player.getName());
            statement.setString(2, discord);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeFromVerifyList(Player player) {
        if (!playerExits(player))
            return;

        try (Connection connection = getConnection()) {
            final PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM " + "`2fa`" + " WHERE player= '" + player.getName() + "';");
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void authPlayer(Player player) {
        try (Connection connection = getConnection()) {
            final PreparedStatement statement = connection.prepareStatement(
                    "UPDATE `2fa` SET `ip` = ? WHERE `player` = ?;");
            statement.setString(1, String.valueOf(player.getAddress().getAddress().getHostAddress()));
            statement.setString(2, player.getName());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        plugin.getDiscord2FAManager().completeAuth(player);
    }

    @Override
    public List<String> generateBackupCodes(Player player) {
        final StringBuilder codes = new StringBuilder();

        for (int i = 1; i <= 5; i++)
            codes.append(plugin.getDiscord2FAManager().getRandomCode(ConfigUtil.getInt("code-lenght"))).append("-");

        final boolean state = getData(player,"codes","2fa_backup") == null;
        final String sql = (
                state ? "INSERT INTO `2fa_backup` (player, codes)" + "VALUES " + "(?, ?);"
                :
                "UPDATE `2fa_backup` SET `codes` = ? WHERE `player` = ?;"
        );

        try (Connection connection = getConnection()) {
            final PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, state ? player.getName() : codes.toString());
            statement.setString(2, state ? codes.toString() : player.getName());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Arrays.asList(codes.toString().split("-"));
    }

    @Override
    public void removeBackupCode(Player player, String code) {
        if (!isBackupCode(player, code))
            return;

        final String codeData = getData(player,"codes","2fa_backup");

        if (codeData == null)
            return;

        final List<String> codesWithList = new ArrayList<>(Arrays.asList(codeData.split("-")));
        codesWithList.remove(code);

        final StringBuilder codes  = new StringBuilder();
        for (String c: codesWithList) codes.append(c).append("-");

        try (Connection connection = getConnection()) {
            final PreparedStatement statement = connection.prepareStatement(
                    "UPDATE `2fa_backup` SET `codes` = ? WHERE `player` = ?;");
            statement.setString(1, codes.toString());
            statement.setString(2, player.getName());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isBackupCode(Player player, String code) {
        final String codeData = getData(player,"codes","2fa_backup");

        if (codeData == null)
            return false;

        final List<String> codesWithList = new ArrayList<>(Arrays.asList(codeData.split("-")));
        return codesWithList.contains(code);
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
        final StringBuilder stringBuilder = new StringBuilder().append("\n");
        final CompletableFuture<StringBuilder> future = CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getConnection()) {
                final PreparedStatement statement =  connection.prepareStatement("SELECT * FROM `2fa`;");
                final ResultSet resultSet = statement.executeQuery();
                while(resultSet.next()) {
                    if (stringBuilder.length() > 0)
                        stringBuilder.append("\n");
                    stringBuilder.append(resultSet.getString(1)).append("/").append(resultSet.getString(2));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return stringBuilder;
        });
        return future.join().toString();
    }

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
