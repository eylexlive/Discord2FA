package io.github.eylexlive.discord2fa.manager;

import io.github.eylexlive.discord2fa.Main;
import io.github.eylexlive.discord2fa.runnable.CountdownRunnable;
import io.github.eylexlive.discord2fa.util.Color;
import lombok.Getter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 2.6
 */

public class Discord2FAManager {
    private final Main plugin;
    @Getter private final Map<UUID, String> checkCode = new HashMap<>();
    @Getter private final ArrayList<Player> checkPlayers = new ArrayList<>();
    @Getter private final Map<UUID,Integer> leftRights = new HashMap<>();
    public Discord2FAManager(Main plugin) {
        this.plugin = plugin;
    }
    @SneakyThrows
    private String getData(String player, String ymlPath , String sqlPath, String sqlTable, boolean mysqlEnabled) {
        if (!mysqlEnabled)
            return this.plugin.getYamlDatabase().getDatabaseConfiguration().getString(ymlPath);
        final PreparedStatement statement =  this.plugin.getMySQLDatabase().getConnection().prepareStatement(
                "SELECT * FROM `"+sqlTable+"` WHERE `player` = ?;");
        statement.setString(1, player);
        final ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getString(sqlPath);
        }
        return null;
    }
    private void setThenSend(Player player, String code) {
        this.checkCode.put(player.getUniqueId(),code);
        final String message = this.plugin.getConfig().getString("messages.discord-message")
                .replace("%code%", this.getCheckCode().get(player.getUniqueId()));
        final String memberId = this.getData(player.getName(),"verify."+ player.getName() + ".discord","discord","2fa",this.plugin.isMySQLEnabled());
        if (memberId == null) {
            this.plugin.getLogger().warning("We're cannot get player's Discord ID?");
            return;
        }
        if (this.leftRights.get(player.getUniqueId()) == null) {
            this.leftRights.put(player.getUniqueId(), this.plugin.getConfig().getInt("number-of-rights"));
        }
        final User user = this.plugin.getBot().getUserById(memberId);
        if (user != null) {
            user.openPrivateChannel().complete().sendMessage((message)).queue();
        } else {
            this.plugin.getLogger().warning("Uh, we're cannot find user with id "+memberId+"");
        }
    }
    public void addPlayerToCheck(Player player) {
        if (this.isInCheck(player))
            return;
        this.checkPlayers.add(player);
        final String code = RandomStringUtils.randomNumeric(this.plugin.getConfig().getInt("code-lenght"));
        if (!this.plugin.getConfig().getBoolean("generate-new-code-always")) {
            if (this.getCheckCode().get(player.getUniqueId()) == null) {
                this.setThenSend(player, code);
            }
        } else {
            this.setThenSend(player, code);
        }
        Bukkit.getScheduler().runTaskLater(this.plugin, new BukkitRunnable() {
            @Override
            public void run() {
                Discord2FAManager.this.plugin.getSitManager().sitPlayer(player);
            }
        }, 10L);
    }
    public void checkPlayer(Player player) {
        if (!this.plugin.getConnectStatus()) {
            player.sendMessage("§4§l[Discord2FA|WARNING] §cHey! please check the console.");
            this.plugin.getLogger().warning("Ops, the bot connect failed. Please provide the bot connection.");
            return;
        }
        if (this.isAddedToVerifyList(player.getName())) {
            if (this.plugin.getConfig().getBoolean("auto-verification")) {
                final String currentlyIp = player.getAddress().getAddress().getHostAddress();
                final String lastIp = this.getData(player.getName(), "verify." + player.getName() + ".ip", "ip", "2fa", this.plugin.isMySQLEnabled());
                if (currentlyIp.equals(lastIp)) {
                    player.sendMessage(Color.translate(this.plugin.getConfig().getString("messages.auto-verify-success-message")));
                    return;
                }
            }
            this.addPlayerToCheck(player);
            player.sendMessage(this.getAuthMessage(true, -1));
            new CountdownRunnable(player, this.plugin)
                    .runTaskTimer(this.plugin, 0L, 20L);
        }
    }
    public void removePlayerFromCheck(Player player) {
        if (!this.isInCheck(player))
            return;
        this.checkPlayers.remove(player);
        this.plugin.getSitManager().unSitPlayer(player);
        if (this.getCheckCode().get(player.getUniqueId()) != null
                && this.plugin.getConfig().getBoolean("generate-new-code-always")) {
            this.checkCode.put(player.getUniqueId(), null);
        }
    }
    @SneakyThrows
    public void auth(Player player, String ip) {
        final boolean mysqlEnabled = this.plugin.isMySQLEnabled();
        if (!mysqlEnabled) {
            this.plugin.getYamlDatabase().getDatabaseConfiguration().set("verify." + player.getName()+".ip", String.valueOf(ip));
            this.plugin.getYamlDatabase().saveDatabaseConfiguration();
        } else {
            final PreparedStatement statement =  this.plugin.getMySQLDatabase().getConnection().prepareStatement(
                    "UPDATE `2fa` SET `ip` = ? WHERE `player` = ?;");
            statement.setString(1,String.valueOf(ip));
            statement.setString(2,player.getName());
            statement.executeUpdate();
        }
        this.removePlayerFromCheck(player);
        this.leftRights.put(player.getUniqueId(),null);
        this.checkCode.put(player.getUniqueId(), null);
        this.plugin.getLogger().info(player.getName() + "'s account was authenticated!");
    }

    @SneakyThrows
    public void addPlayerToVerifyList(String player, String discord) {
        if (this.isAddedToVerifyList(player))
            return;
        final boolean mysqlEnabled = this.plugin.isMySQLEnabled();
        if (!mysqlEnabled) {
            this.plugin.getYamlDatabase().getDatabaseConfiguration().set("verify." + player + ".discord", discord);
            this.plugin.getYamlDatabase().saveDatabaseConfiguration();
        } else {
            final Connection connection = this.plugin.getMySQLDatabase().getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO `2fa` (player, discord, ip)" + "VALUES " + "(?, ?, ?);");
            statement.setString(1, player);
            statement.setString(2, discord);
            statement.setString(3, "CURRENTLY_NULL");
            statement.executeUpdate();

            statement = connection.prepareStatement(
                    "INSERT INTO `2fa_backup` (player, codes)" + "VALUES " + "(?, ?);");
            statement.setString(1, player);
            statement.setString(2,"CURRENTLY_NULL");
            statement.executeUpdate();
        }
    }

    @SneakyThrows
    public void removePlayerFromVerifyList(String player) {
        if (!this.isAddedToVerifyList(player))
            return;
        final boolean mysqlEnabled = this.plugin.isMySQLEnabled();
        if (!mysqlEnabled) {
            this.plugin.getYamlDatabase().getDatabaseConfiguration().set("verify." + player + ".discord", null);
            this.plugin.getYamlDatabase().saveDatabaseConfiguration();
        } else {
            final PreparedStatement statement =  this.plugin.getMySQLDatabase().getConnection().prepareStatement(
                    "DELETE FROM " + "`2fa`" + " WHERE player= '" + player + "';");
            statement.executeUpdate();
            statement.close();
        }
    }

    @SneakyThrows
    public List<String> generateBackupCodes(String player) {
        final StringBuilder codes = new StringBuilder();
        final boolean mysqlEnabled = this.plugin.isMySQLEnabled();
        final PreparedStatement statement;
        for (int i = 1; i <=5; i++) {
            codes.append(RandomStringUtils.randomNumeric(
                    this.plugin.getConfig().getInt("code-lenght"))
            ).append("-");
        }
        if (!this.isBackupCodesGenerated(player)) {
            if (!mysqlEnabled) {
                this.plugin.getYamlDatabase().getDatabaseConfiguration().set("verify."+ player +".backup-codes", codes.toString());
                this.plugin.getYamlDatabase().saveDatabaseConfiguration();
            } else {
                statement = this.plugin.getMySQLDatabase().getConnection().prepareStatement(
                        "INSERT INTO `2fa_backup` (player, codes)" + "VALUES " + "(?, ?);");
                statement.setString(1, player);
                statement.setString(2, codes.toString());
                statement.executeUpdate();
            }
        } else {
            if (!mysqlEnabled) {
                this.plugin.getYamlDatabase().getDatabaseConfiguration().set("verify." + player + ".backup-codes", codes.toString());
                this.plugin.getYamlDatabase().saveDatabaseConfiguration();
            } else {
                statement = this.plugin.getMySQLDatabase().getConnection().prepareStatement(
                        "UPDATE `2fa_backup` SET `codes` = ? WHERE `player` = ?;");
                statement.setString(1, codes.toString());
                statement.setString(2,player);
                statement.executeUpdate();
            }
        }
        return Arrays.asList(codes.toString().split("-"));
    }
    @SneakyThrows
    public void removeBackupCode(Player player, String code) {
        if (!this.isBackupCode(player,code))
            return;
        final String codeData = this.getData(player.getName(),"verify." + player.getName() + ".backup-codes","codes","2fa_backup",this.plugin.isMySQLEnabled());
        final List<String> codesWithList = new ArrayList<>(Arrays.asList(codeData.split("-")));
        codesWithList.remove(code);
        final boolean mysqlEnabled = this.plugin.isMySQLEnabled();
        final StringBuilder codes  = new StringBuilder();
        for (String c: codesWithList) {
            codes.append(c).append("-");
        }
        if (!mysqlEnabled) {
            this.plugin.getYamlDatabase().getDatabaseConfiguration().set("verify." + player.getName() + ".backup-codes", codes.toString());
            this.plugin.getYamlDatabase().saveDatabaseConfiguration();
        } else {
            final PreparedStatement statement = this.plugin.getMySQLDatabase().getConnection().prepareStatement(
                    "UPDATE `2fa_backup` SET `codes` = ? WHERE `player` = ?;");
            statement.setString(1, codes.toString());
            statement.setString(2,player.getName());
            statement.executeUpdate();
        }
    }
    public String[] getAuthMessage(boolean state,int i) {
        final boolean bool = state && i == -1; int format = (bool ? 1 : 2);
        final String replace = (bool ? "%countdown%" : "%seconds%");
        final String replacement = (
                bool ? String.valueOf(this.plugin.getConfig().getInt("auth-countdown")) :
                        i +"§6 second"+(i > 1 ? "s" : "")
        );
        String authMessage = this.plugin.getConfig().getString("messages.auth-message.format-" + format);
        authMessage = Color.translate(authMessage);
        authMessage = authMessage.replace(replace, replacement);
        return authMessage.split("%nl%");
    }
    public boolean isBackupCode(Player player,String code) {
        final String codeData = this.getData(player.getName(),"verify." + player.getName()+".backup-codes","codes","2fa_backup",this.plugin.isMySQLEnabled());
        if (codeData == null)
            return false;
        final List<String> codesWithList = new ArrayList<>(Arrays.asList(codeData.split("-")));
        return codesWithList.contains(code) && !code.equals("CURRENTLY_NULL");
    }
    public boolean isAddedToVerifyList(String player) {
        return this.getData(player,"verify." + player +".discord","discord","2fa", this.plugin.isMySQLEnabled()) != null;
    }
    public boolean isBackupCodesGenerated(String player) {
        return this.getData(player,"verify." + player + ".backup-codes","codes","2fa_backup", this.plugin.isMySQLEnabled()) != null;
    }
    public boolean isInCheck(Player player) {
        return (player != null && this.getCheckPlayers().contains(player));
    }
}
