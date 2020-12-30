package io.github.eylexlive.discord2fa.manager;

import io.github.eylexlive.discord2fa.Discord2FA;
import io.github.eylexlive.discord2fa.data.PlayerData;
import io.github.eylexlive.discord2fa.event.AuthCompleteEvent;
import io.github.eylexlive.discord2fa.event.AuthFailEvent;
import io.github.eylexlive.discord2fa.task.Cancel2FAReqTask;
import io.github.eylexlive.discord2fa.task.CountdownTask;
import io.github.eylexlive.discord2fa.util.ConfigUtil;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.3
 */

public class Discord2FAManager {

    private final Discord2FA plugin;

    private final Map<UUID, PlayerData> players = new HashMap<>();

    private final Map<Player, ArmorStand> armorStands = new HashMap<>();

    private final Set<Player> checkPlayers = new HashSet<>();

    public Discord2FAManager(Discord2FA plugin) {
        this.plugin = plugin;
    }

    public PlayerData loadData(Player player) {
        final UUID uuid = player.getUniqueId();

        if (getPlayerData(uuid) != null)
            return getPlayerData(uuid);

        final PlayerData data = new PlayerData(uuid);

        players.put(data.getUuid(), data);
        return data;
    }

    public void unloadData(Player player) {
        players.remove(player.getUniqueId());
    }

    public PlayerData getPlayerData(Player player) {
      if (getPlayerData(player.getUniqueId()) == null)
          return loadData(player);
      return players.get(player.getUniqueId());
    }

    public PlayerData getPlayerData(UUID uuid) {
        return players.get(uuid);
    }

    public void addPlayerToCheck(Player player) {
        if (isInCheck(player))
            return;

        checkPlayers.add(player);

        final String code = getRandomCode(ConfigUtil.getInt("code-lenght"));
        final PlayerData playerData =loadData(player);

        if (!ConfigUtil.getBoolean("generate-new-code-always")) {
            if (playerData.getCheckCode() == null) sendCode(player, code);
        } else {
            sendCode(player, code);
        }

        new BukkitRunnable() { @Override public void run() { sitPlayer(player); } }.runTaskLater(plugin, 10L);

        player.sendMessage(getAuthMessage(true, -1));
        new CountdownTask(this, player).runTaskTimer(plugin, 0L, 20L);
    }

    public void removePlayerFromCheck(Player player) {
        if (!isInCheck(player))
            return;

        checkPlayers.remove(player);
        unSitPlayer(player);

        final PlayerData playerData = getPlayerData(player);

        if (playerData.getCheckCode() != null
                && ConfigUtil.getBoolean("generate-new-code-always")) {
            playerData.setCheckCode(null);
        }
    }

    public void checkPlayer(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            final boolean playerExits = plugin.getProvider().playerExits(player);
            if (!plugin.isConnected()) {
                if (playerExits) player.sendMessage("§4§[lDiscord2FA] §cHey! The bot connection must be provided to we send a code.");
                plugin.getLogger().warning("Ops, the bot connect failed. Please provide the bot connection.");
                return;
            }

            if (!playerExits)
                return;

            if (ConfigUtil.getBoolean("auto-verification")) {
                final String currentlyIp = player.getAddress().getAddress().getHostAddress();
                final String lastIp = plugin.getProvider().getIP(player);

                if (currentlyIp.equals(lastIp)) {
                    player.sendMessage(ConfigUtil.getString("messages.auto-verify-success-message"));
                    return;
                }
            }
            addPlayerToCheck(player);
        });
    }

    public void completeAuth(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!isInCheck(player))
                return;

            checkPlayers.remove(player);

            unSitPlayer(player);
            unloadData(player);

            if (ConfigUtil.getBoolean("logs.enabled")) sendLog(ConfigUtil.getStringList("logs.admin-ids"), ConfigUtil.getString("logs.player-authenticated", "player:" + player.getName()));

            plugin.getLogger().info(player.getName() + "'s account was authenticated!");
        });
        plugin.getServer().getPluginManager().callEvent(new AuthCompleteEvent(player));
    }

    private void sendCode(Player player, String code) {
        final PlayerData playerData = getPlayerData(player);
        final String memberId = plugin.getProvider().getMemberID(player);
        playerData.setCheckCode(code);

        if (memberId == null) {
            plugin.getLogger().warning("We're cannot get player's Discord ID?");
            return;
        }

        if (playerData.getLeftRights() == 0) playerData.setLeftRights(ConfigUtil.getInt("number-of-rights"));

        if (!sendLog(Collections.singletonList(memberId), ConfigUtil.getString("messages.discord-message", "code:" + playerData.getCheckCode())))
            player.sendMessage(ConfigUtil.getString("messages.msg-send-failed"));
    }

    public void failPlayer(Player player) {
        final Server server = plugin.getServer();

        getPlayerData(player).setLeftRights(ConfigUtil.getInt("number-of-rights"));
        server.dispatchCommand(server.getConsoleSender(), ConfigUtil.getString("rights-reached-console-command", "player:" + player.getName()));

        if (ConfigUtil.getBoolean("logs.enabled"))
            if (!sendLog(ConfigUtil.getStringList("logs.admin-ids"), ConfigUtil.getString("logs.player-reached-limit", "player:" + player.getName()))) {
                player.sendMessage(ConfigUtil.getString("messages.msg-send-failed"));
            }
        plugin.getServer().getPluginManager().callEvent(new AuthFailEvent(player));
    }

    public void failPlayer(Player player, int i) {
        final PlayerData playerData = getPlayerData(player);
        playerData.setLeftRights(playerData.getLeftRights() - i);

        player.sendMessage(ConfigUtil.getString("messages.auth-command.invalid-code-message", "rights:" + playerData.getLeftRights()));

        if (ConfigUtil.getBoolean("logs.enabled"))
            if (!sendLog(ConfigUtil.getStringList("logs.admin-ids"), ConfigUtil.getString("logs.player-entered-wrong-code", "player:" + player.getName(), "left:" + playerData.getLeftRights()))) {
                player.sendMessage(ConfigUtil.getString("messages.msg-send-failed"));
            }
    }

    public String[] getAuthMessage(boolean state, int i) {
        final boolean bool = state && i == -1;
        final int format = (bool ? 1 : 2);

        final String replaceKey = (bool ? "countdown" : "seconds");
        final String replacementKey = (bool ? String.valueOf(ConfigUtil.getInt("auth-countdown")) :
                        i +" second"+(i > 1 ? "s" : "")
        );
        return ConfigUtil.getString("messages.auth-message.format-" + format, replaceKey + ":" + replacementKey).split("%nl%");
    }

    public String getRandomCode(int count) {
        final CodeType codeType;
        try {
            codeType = CodeType.valueOf(ConfigUtil.getString("code-type"));
        } catch (IllegalArgumentException e) {
            return "Invalid code type. **Please check it in config.yml** (Write it in upper case)";
        }
        switch (codeType) {
            case NUMERIC:
                return RandomStringUtils.randomNumeric(count);
            case ALPHANUMERIC:
                return RandomStringUtils.randomAlphanumeric(count);
            case ALPHABETIC:
                return RandomStringUtils.randomAlphabetic(count);
        }
        return null;
    }

    public void enable2FA(Player player, String str) {
        final PlayerData playerData = getPlayerData(player);
        final String code = playerData.getConfirmCode();
        if (str.equals(code)) {
            final User user = playerData.getConfirmUser();

            plugin.getProvider().addToVerifyList(player, user.getId());
            player.sendMessage(ConfigUtil.getString("messages.discord2fa-command.player-auth-enabled"));

            if (!sendLog(ConfigUtil.getString("authentication-for-players.successfully-confirmed"), user))
                player.sendMessage(ConfigUtil.getString("messages.msg-send-failed"));

            final int taskID = playerData.getPlayerTaskID();
            if (taskID != 0) Bukkit.getScheduler().cancelTask(taskID);
            unloadData(player);
        }
    }

    public void disable2FA(Player player) {
        if (!plugin.getProvider().playerExits(player)) {
            player.sendMessage(ConfigUtil.getString("messages.discord2fa-command.player-auth-already-disabled"));
            return;
        }

        plugin.getProvider().removeFromVerifyList(player);
        player.sendMessage(ConfigUtil.getString("messages.discord2fa-command.player-auth-disabled"));
    }

    public void sendEnabling2FARequest(Player player) {
        if (plugin.getProvider().playerExits(player)) {
            player.sendMessage(ConfigUtil.getString("messages.discord2fa-command.player-auth-already-enabled"));
            return;
        }

        final PlayerData playerData = getPlayerData(player);
        final String confCode = playerData.getConfirmCode();

        if (confCode != null && confCode.equals("§"))
            return;

        playerData.setConfirmCode("§");
        player.sendMessage(ConfigUtil.getString("messages.discord2fa-command.player-auth-enter-discord"));

        final BukkitTask task = new Cancel2FAReqTask(this, player, true).runTaskLater(plugin, 20 * 30L);
        playerData.setPlayerTaskID(task.getTaskId());
    }

    public void sendConfirmCode(Player player, String str) {
        final String[] split = str.split("#");
        final User user;

        if (split.length == 2)
            user = plugin.getBot().getJDA().getUserByTag((split[0].length() >= 2 && split[0].length() <= 32 ? split[0] : "§§"), (split[1].length() == 4 ? split[1] : "0000"));
        else
            user = null;

        if (user == null) {
            player.sendMessage(ConfigUtil.getString("messages.discord2fa-command.player-auth-discord-acc-not-found"));
            return;
        }

        final PlayerData playerData = getPlayerData(player);
        final String code = getRandomCode(ConfigUtil.getInt("code-lenght"));
        playerData.setConfirmCode(code);

        if (!sendLog(ConfigUtil.getString("authentication-for-players.confirm-your-account", "nl:" + "\n", "code:" + code, "player:" + player.getName()), user))
            player.sendMessage(ConfigUtil.getString("messages.msg-send-failed"));

        playerData.setConfirmUser(user);
        player.sendMessage(ConfigUtil.getString("messages.discord2fa-command.player-auth-confirm-code-sent"));

        final int taskID = playerData.getPlayerTaskID();
        if (taskID != 0) Bukkit.getScheduler().cancelTask(taskID);

        final BukkitTask task = new Cancel2FAReqTask(this, player, false).runTaskLater(plugin, 20 * 30L);
        playerData.setPlayerTaskID(task.getTaskId());
    }

    public void cancel2FAReq(Player player) {
        unloadData(player);
        player.sendMessage(ConfigUtil.getString("messages.discord2fa-command.player-auth-timeout"));
    }

    public boolean sendLog(String path, User user) {
        final boolean[] successfullySent = {true};
        user.openPrivateChannel()
                .submit()
                .thenCompose(channel -> channel.sendMessage(path).submit())
                .exceptionally((error) -> {
                    successfullySent[0] = false;
                    return null;
                }).join();
        return successfullySent[0];
    }

    public boolean sendLog(List<String> stringList, String path) {
        final boolean[] successfullySent = {true};
        stringList.forEach(id ->  {
            final User user = plugin.getBot().getJDA().getUserById(id);
            if (user == null)
                return;
            user.openPrivateChannel()
                    .submit()
                    .thenCompose(channel -> channel.sendMessage(path).submit())
                    .exceptionally((error) -> {
                        successfullySent[0] = false;
                        return null;
                    }).join();
        });
        return successfullySent[0];
    }


    public void sitPlayer(Player player) {
        final ArmorStand armorStand = (ArmorStand) Objects.requireNonNull(
                player.getLocation().getWorld()).spawnEntity(player.getLocation(), EntityType.ARMOR_STAND
        );
        armorStand.setVisible(false);
        armorStand.setPassenger(player);
        armorStands.put(player,armorStand);
    }

    public void unSitPlayer(Player player) {
        if (armorStands.containsKey(player))
            armorStands.get(player).remove();
    }

    public boolean isInCheck(Player player) {
        return checkPlayers.contains(player);
    }

    public Set<Player> getCheckPlayers() {
        return checkPlayers;
    }

    public Map<Player, ArmorStand> getArmorStands() {
        return armorStands;
    }

    private enum CodeType {
        NUMERIC,
        ALPHANUMERIC,
        ALPHABETIC
    }
}
