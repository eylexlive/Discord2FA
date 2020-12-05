package io.github.eylexlive.discord2fa.manager;

import io.github.eylexlive.discord2fa.Main;
import io.github.eylexlive.discord2fa.bot.Bot;
import io.github.eylexlive.discord2fa.event.AuthFailEvent;
import io.github.eylexlive.discord2fa.runnable.CountdownRunnable;
import io.github.eylexlive.discord2fa.util.Color;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Server;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.0
 */

public class Discord2FAManager {

    private final Main plugin;

    private final Map<UUID, String> checkCode = new HashMap<>();
    private final Map<UUID, Integer> leftRights = new HashMap<>();
    private final Map<Player, ArmorStand> armorStands = new HashMap<>();

    private final List<Player> checkPlayers = new ArrayList<>();

    public Discord2FAManager(Main plugin) {
        this.plugin = plugin;
    }

    private void setThenSend(Player player, String code) {
        checkCode.put(player.getUniqueId(), code);
        final String message = plugin.getConfig().getString("messages.discord-message")
                .replace("%code%", checkCode.get(player.getUniqueId()));
        final String memberId = plugin.getProvider().getMemberID(player);
        if (memberId == null) {
            plugin.getLogger().warning("We're cannot get player's Discord ID?");
            return;
        }
        if (leftRights.get(player.getUniqueId()) == null) {
            leftRights.put(player.getUniqueId(), plugin.getConfig().getInt("number-of-rights"));
        }
        sendLog(Collections.singletonList(memberId), message);
    }

    public void addPlayerToCheck(Player player) {
        if (isInCheck(player))
            return;
        checkPlayers.add(player);
        final String code = RandomStringUtils.randomNumeric(plugin.getConfig().getInt("code-lenght"));
        if (!plugin.getConfig().getBoolean("generate-new-code-always")) {
            if (checkCode.get(player.getUniqueId()) == null) setThenSend(player, code);
        } else {
            setThenSend(player, code);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                sitPlayer(player);
            }
        }.runTaskLater(plugin, 10L);
    }
    
    public void checkPlayer(Player player) {
        if (plugin.getConnectStatus()) {
            if (!plugin.getProvider().playerExits(player))
                return;
            if (plugin.getConfig().getBoolean("auto-verification")) {
                final String currentlyIp = player.getAddress().getAddress().getHostAddress();
                final String lastIp = plugin.getProvider().getIP(player);
                if (currentlyIp.equals(lastIp)) {
                    player.sendMessage(Color.translate(plugin.getConfig().getString("messages.auto-verify-success-message")));
                    return;
                }
            }
            addPlayerToCheck(player);
            player.sendMessage(getAuthMessage(true, -1));
            new CountdownRunnable(player, plugin)
                    .runTaskTimer(plugin, 0L, 20L);
        } else {
            player.sendMessage("§4§l[Discord2FA|WARNING] §cHey! please check the console.");
            plugin.getLogger().warning("Ops, the bot connect failed. Please provide the bot connection.");
        }
    }

    public void failPlayer(Player player) {
        final Server server = plugin.getServer();
        leftRights.put(player.getUniqueId(), plugin.getConfig().getInt("number-of-rights"));
        String command = plugin.getConfig().getString("rights-reached-console-command");
        command = command.replace("%player%", player.getName());
        server.dispatchCommand(server.getConsoleSender(), command);
        final List<String> adminIds = plugin.getConfig().getStringList("logs.admin-ids");
        if (plugin.getConfig().getBoolean("logs.enabled"))
            sendLog(adminIds, plugin.getConfig().getString("logs.player-reached-limit").replace("%player%",player.getName()));
        plugin.getServer().getPluginManager().callEvent(new AuthFailEvent(player));
    }

    public void failPlayer(Player player, int rightSize) {
        leftRights.put(player.getUniqueId(), leftRights.get(player.getUniqueId()) - rightSize);
        String message = plugin.getConfig().getString("messages.auth-command.invalid-code-message");
        message = message.replace("%rights%", String.valueOf(leftRights.get(player.getUniqueId())));
        player.sendMessage(Color.translate(message));
        final List<String> adminIds = plugin.getConfig().getStringList("logs.admin-ids");
        if (plugin.getConfig().getBoolean("logs.enabled"))
           sendLog(adminIds, plugin.getConfig().getString("logs.player-entered-wrong-code")
                   .replace("%player%",player.getName())
                   .replace("%left%",leftRights.get(player.getUniqueId())+ "")
           );
    }

    public void removePlayerFromCheck(Player player) {
        if (!isInCheck(player))
            return;
        checkPlayers.remove(player);
        unSitPlayer(player);
        if (checkCode.get(player.getUniqueId()) != null
                && plugin.getConfig().getBoolean("generate-new-code-always")) {
            checkCode.put(player.getUniqueId(), null);
        }
    }

    public String[] getAuthMessage(boolean state, int i) {
        final boolean bool = state && i == -1; int format = (bool ? 1 : 2);
        final String replaceKey = (bool ? "%countdown%" : "%seconds%");
        final String replacementKey = (
                bool ? String.valueOf(plugin.getConfig().getInt("auth-countdown")) :
                        i +" second"+(i > 1 ? "s" : "")
        );
        String authMessage = plugin.getConfig().getString("messages.auth-message.format-" + format);
        authMessage = Color.translate(authMessage);
        authMessage = authMessage.replace(replaceKey, replacementKey);
        return authMessage.split("%nl%");
    }

    public void sendLog(List<String> stringList, String path) {
        stringList.forEach(id ->  {
            final User user = Bot.jda.getUserById(id);
            if (user == null)
                return;
            Objects.requireNonNull(user)
                    .openPrivateChannel()
                    .complete()
                    .sendMessage(path)
                    .queue();
        });
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

    public Map<UUID, Integer> getLeftRights() {
        return leftRights;
    }

    public Map<UUID, String> getCheckCode() {
        return checkCode;
    }

    public Map<Player, ArmorStand> getArmorStands() {
        return armorStands;
    }
}
