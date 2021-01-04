package io.github.eylexlive.discord2fa.provider;

import io.github.eylexlive.discord2fa.Discord2FA;
import io.github.eylexlive.discord2fa.util.Config;
import io.github.eylexlive.discord2fa.util.ConfigUtil;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.4
 */

public class YamlProvider extends Provider {

    private final Discord2FA plugin;

    private Config yaml;

    public YamlProvider(Discord2FA plugin) {
        this.plugin = plugin;
    }

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
        plugin.getDiscord2FAManager().completeAuth(player);
    }

    @Override
    public List<String> generateBackupCodes(Player player) {
        final StringBuilder codes = new StringBuilder();

        for (int i = 1; i <= 5; i++)
            codes.append(plugin.getDiscord2FAManager().getRandomCode(ConfigUtil.getInt("code-lenght"))).append("-");

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
        for (String c: codesWithList) codes.append(c).append("-");

        yaml.set("verify." + player.getName() + ".backup-codes", codes.toString());
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
