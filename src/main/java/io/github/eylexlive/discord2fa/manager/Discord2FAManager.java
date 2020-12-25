package io.github.eylexlive.discord2fa.manager;

import io.github.eylexlive.discord2fa.Main;
import io.github.eylexlive.discord2fa.event.AuthFailEvent;
import io.github.eylexlive.discord2fa.runnable.CountdownRunnable;
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

    private final Main plugin;

    private final Map<UUID, String> checkCode = new HashMap<>();
    private final Map<UUID, String> confirmCode = new HashMap<>();

    private final Map<UUID, Integer> playerTasks = new HashMap<>();
    private final Map<UUID, Integer> leftRights = new HashMap<>();

    private final Map<UUID, User> confirmUser = new HashMap<>();

    private final Map<Player, ArmorStand> armorStands = new HashMap<>();

    private final List<Player> checkPlayers = new ArrayList<>();

    public Discord2FAManager(Main plugin) {
        this.plugin = plugin;
    }

    public void addPlayerToCheck(Player player) {
        if (isInCheck(player)) return;
        checkPlayers.add(player);
        final String code = getRandomCode(ConfigUtil.getInt("code-lenght"));

        if (!ConfigUtil.getBoolean("generate-new-code-always")) {
            if (checkCode.get(player.getUniqueId()) == null) sendCode(player, code);
        } else {
            sendCode(player, code);
        }

        new BukkitRunnable() { @Override public void run() { sitPlayer(player); } }.runTaskLater(plugin, 10L);
        player.sendMessage(getAuthMessage(true, -1));
        new CountdownRunnable(player, plugin).runTaskTimer(plugin, 0L, 20L);
    }
    
    public void checkPlayer(Player player) {
        if (plugin.isConnected()) {
            if (!plugin.getProvider().playerExits(player)) return;

            if (ConfigUtil.getBoolean("auto-verification")) {
                final String currentlyIp = player.getAddress().getAddress().getHostAddress();
                final String lastIp = plugin.getProvider().getIP(player);
                if (currentlyIp.equals(lastIp)) {
                    player.sendMessage(ConfigUtil.getString("messages.auto-verify-success-message"));
                    return;
                }
            }
            addPlayerToCheck(player);
        } else {
            if (player.isOp()) player.sendMessage("§4§l[Discord2FA|WARNING] §cHey! please check the console.");
            plugin.getLogger().warning("Ops, the bot connect failed. Please provide the bot connection.");
        }
    }

    public void failPlayer(Player player) {
        final Server server = plugin.getServer();
        leftRights.put(player.getUniqueId(), ConfigUtil.getInt("number-of-rights"));
        server.dispatchCommand(server.getConsoleSender(), ConfigUtil.getString("rights-reached-console-command", "player:" + player.getName()));
        final List<String> adminIds = ConfigUtil.getStringList("logs.admin-ids");

        if (ConfigUtil.getBoolean("logs.enabled"))
            if (!sendLog(adminIds, ConfigUtil.getString("logs.player-reached-limit", "player:" + player.getName()))) {
                player.sendMessage(ConfigUtil.getString("messages.msg-send-failed"));
            }
        plugin.getServer().getPluginManager().callEvent(new AuthFailEvent(player));
    }

    public void failPlayer(Player player, int rightSize) {
        leftRights.put(player.getUniqueId(), leftRights.get(player.getUniqueId()) - rightSize);

        player.sendMessage(ConfigUtil.getString("messages.auth-command.invalid-code-message", "rights:" + leftRights.get(player.getUniqueId())));
        final List<String> adminIds = ConfigUtil.getStringList("logs.admin-ids");

        if (ConfigUtil.getBoolean("logs.enabled"))
           if (!sendLog(adminIds, ConfigUtil.getString("logs.player-entered-wrong-code", "player:" + player.getName(), "left:" + leftRights.get(player.getUniqueId())))) {
               player.sendMessage(ConfigUtil.getString("messages.msg-send-failed"));
           }
    }

    public void removePlayerFromCheck(Player player) {
        if (!isInCheck(player)) return;

        checkPlayers.remove(player);
        unSitPlayer(player);

        if (checkCode.get(player.getUniqueId()) != null
                && ConfigUtil.getBoolean("generate-new-code-always")) {
            checkCode.put(player.getUniqueId(), null);
        }
    }

    private void sendCode(Player player, String code) {
        final String memberId = plugin.getProvider().getMemberID(player);
        checkCode.put(player.getUniqueId(), code);

        if (memberId == null) {
            plugin.getLogger().warning("We're cannot get player's Discord ID?");
            return;
        }

        if (leftRights.get(player.getUniqueId()) == null)
            leftRights.put(player.getUniqueId(), ConfigUtil.getInt("number-of-rights"));

        if (!sendLog(Collections.singletonList(memberId), ConfigUtil.getString("messages.discord-message", "code:" + checkCode.get(player.getUniqueId()))))
            player.sendMessage(ConfigUtil.getString("messages.msg-send-failed"));
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

    public void enable2FA(Player player, String message) {
        final String code = confirmCode.get(player.getUniqueId());
        if (message.equals(code)) {
            final User user = confirmUser.get(player.getUniqueId());

            plugin.getProvider().addToVerifyList(player, user.getId());
            player.sendMessage(ConfigUtil.getString("messages.discord2fa-command.player-auth-enabled"));

            if (!sendLog(ConfigUtil.getString("authentication-for-players.successfully-confirmed"), user))
                player.sendMessage(ConfigUtil.getString("messages.msg-send-failed"));

            confirmCode.put(player.getUniqueId(), null);
            confirmUser.put(player.getUniqueId(), null);

            final Integer taskID = playerTasks.get(player.getUniqueId());
            if (taskID != null)
                Bukkit.getScheduler().cancelTask(taskID);

            playerTasks.put(player.getUniqueId(), null);
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

        final String confCode = confirmCode.get(player.getUniqueId());
        if (confCode != null && confCode.equals("§"))
            return;

        confirmCode.put(player.getUniqueId(), "§");
        player.sendMessage(ConfigUtil.getString("messages.discord2fa-command.player-auth-enter-discord"));

        final BukkitTask task = new BukkitRunnable() { @Override public void run() { final String code = confirmCode.get(player.getUniqueId());if (code != null && code.equals("§")) cancel2FAReq(player); }}.runTaskLater(plugin, 20 * 30L);
        playerTasks.put(player.getUniqueId(), task.getTaskId());
    }

    public void sendConfirmCode(Player player, String message) {
        final String[] split = message.split("#");
        final User user;

        if (split.length == 2)
            user = plugin.getBot().getJDA().getUserByTag((split[0].length() >= 2 && split[0].length() <= 32 ? split[0] : "§§"), (split[1].length() == 4 ? split[1] : "0000"));
        else
            user = null;

        if (user == null) {
            player.sendMessage(ConfigUtil.getString("messages.discord2fa-command.player-auth-discord-acc-not-found"));
            return;
        }

        final String code = getRandomCode(ConfigUtil.getInt("code-lenght"));
        confirmCode.put(player.getUniqueId(), code);

        if (!sendLog(ConfigUtil.getString("authentication-for-players.confirm-your-account", "nl:" + "\n", "code:" + code, "player:" + player.getName()), user))
            player.sendMessage(ConfigUtil.getString("messages.msg-send-failed"));

        confirmUser.put(player.getUniqueId(), user);
        player.sendMessage(ConfigUtil.getString("messages.discord2fa-command.player-auth-confirm-code-sent"));

        final Integer taskID = playerTasks.get(player.getUniqueId());
        if (taskID != null)
            Bukkit.getScheduler().cancelTask(taskID);

        final BukkitTask task = new BukkitRunnable() { @Override public void run() { final String code = confirmCode.get(player.getUniqueId());if (code != null && !code.equals("§")) cancel2FAReq(player); }}.runTaskLater(plugin, 20 * 30L);
        playerTasks.put(player.getUniqueId(), task.getTaskId());
    }

    private void cancel2FAReq(Player player) {
        confirmCode.put(player.getUniqueId(), null);
        confirmUser.put(player.getUniqueId(), null);
        playerTasks.put(player.getUniqueId(), null);
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

    public boolean isInCheck(Player player) { return checkPlayers.contains(player); }

    public Map<UUID, Integer> getLeftRights() { return leftRights; }

    public Map<UUID, String> getCheckCode() { return checkCode; }

    public Map<UUID, String> getConfirmCode() { return confirmCode; }

    public List<Player> getCheckPlayers() { return checkPlayers; }

    public Map<Player, ArmorStand> getArmorStands() { return armorStands; }

    private enum CodeType {
        NUMERIC,
        ALPHANUMERIC,
        ALPHABETIC
    }
}
