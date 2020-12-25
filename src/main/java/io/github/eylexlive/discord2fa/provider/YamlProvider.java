package io.github.eylexlive.discord2fa.provider;

import io.github.eylexlive.discord2fa.Main;
import io.github.eylexlive.discord2fa.event.AuthCompleteEvent;
import io.github.eylexlive.discord2fa.file.Config;
import io.github.eylexlive.discord2fa.manager.Discord2FAManager;
import io.github.eylexlive.discord2fa.util.ConfigUtil;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.3
 */

public class YamlProvider extends Provider {

    private final Main plugin = Main.getInstance();

    private Config yaml;

    private String getData(String ymlPath) {
        return yaml.getString(ymlPath);
    }

    @Override
    public void setupDatabase() {
        yaml = new Config("database");
    }

    @Override
    public void saveDatabase() {
        yaml.save();
    }

    @Override
    public void addToVerifyList(Player player, String discord) {
        if (playerExits(player))
            return;
        yaml.set("verify." + player.getName() + ".discord", discord);
    }

    @Override
    public void removeFromVerifyList(Player player) {
        if (!playerExits(player))
            return;
        yaml.set("verify." + player.getName(), null);
    }

    @Override
    public void authPlayer(Player player) {
        yaml.set("verify." + player.getName() + ".ip", String.valueOf(player.getAddress().getAddress().getHostAddress()));

        final Discord2FAManager discord2FAManager = plugin.getDiscord2FAManager();
        discord2FAManager.removePlayerFromCheck(player);
        discord2FAManager.getLeftRights().put(player.getUniqueId(), null);
        discord2FAManager.getCheckCode().put(player.getUniqueId(), null);

        plugin.getLogger().info(player.getName() + "'s account was authenticated!");
        final List<String> adminIds = ConfigUtil.getStringList("logs.admin-ids");
        if (ConfigUtil.getBoolean("logs.enabled"))
            discord2FAManager.sendLog(adminIds, ConfigUtil.getString("logs.player-authenticated", "player:" + player.getName()));
        plugin.getServer().getPluginManager().callEvent(new AuthCompleteEvent(player));
    }

    @Override
    public List<String> generateBackupCodes(Player player) {
        final StringBuilder codes = new StringBuilder();
        for (int i = 1; i <= 5; i++)
            codes.append(plugin.getDiscord2FAManager().getRandomCode(
                    ConfigUtil.getInt("code-lenght"))
            ).append("-");

        yaml.set("verify."+ player.getName() +".backup-codes", codes.toString());
        return Arrays.asList(codes.toString().split("-"));
    }

    @Override
    public void removeBackupCode(Player player, String code) {
        if (!isBackupCode(player, code))
            return;
        final String codeData = getData("verify." + player.getName() + ".backup-codes");

        if (codeData == null)
            return;

        final List<String> codesWithList = new ArrayList<>(Arrays.asList(codeData.split("-")));
        codesWithList.remove(code);

        final StringBuilder codes  = new StringBuilder();
        for (String c: codesWithList)
            codes.append(c).append("-");

        yaml.set("verify." + player.getName() + ".backup-codes", codes.toString());
    }

    @Override
    public boolean isBackupCodesGenerated(Player player) {
        return getData("verify." + player.getName() + ".backup-codes") != null;
    }

    @Override
    public boolean isBackupCode(Player player, String code) {
        final String codeData = getData("verify." + player.getName() + ".backup-codes");

        if (codeData == null)
            return false;

        final List<String> codesWithList = new ArrayList<>(Arrays.asList(codeData.split("-")));
        return codesWithList.contains(code);
    }

    @Override
    public boolean playerExits(Player player) {
        return getData("verify." + player.getName() + ".discord") != null;
    }

    @Override
    public String getIP(Player player) {
        return getData("verify." + player.getName() + ".ip");
    }

    @Override
    public String getMemberID(Player player) {
        return getData("verify." + player.getName() + ".discord");
    }

    @Override
    public String getListMessage() {
        final StringBuilder stringBuilder = new StringBuilder().append("\n");
        if (yaml.getConfigurationSection("verify") == null)
            return "Verify list empty.";
        yaml.getConfigurationSection("verify")
                .getKeys(false)
                .forEach(key -> {
                    if(stringBuilder.length() > 0)
                        stringBuilder.append("\n");
                    stringBuilder.append(key).append("/").append(getData("verify." + key + ".discord"));
                });
        return stringBuilder.toString();
    }
}
