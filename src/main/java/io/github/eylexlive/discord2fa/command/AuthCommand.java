package io.github.eylexlive.discord2fa.command;

import io.github.eylexlive.discord2fa.Discord2FA;
import io.github.eylexlive.discord2fa.data.PlayerData;
import io.github.eylexlive.discord2fa.manager.Discord2FAManager;
import io.github.eylexlive.discord2fa.provider.Provider;
import io.github.eylexlive.discord2fa.util.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.3
 */

public class AuthCommand implements CommandExecutor {

    private final Discord2FA plugin;

    public AuthCommand(Discord2FA plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player))
            return true;

        final Discord2FAManager manager = plugin.getDiscord2FAManager();
        final Provider provider = plugin.getProvider();

        final Player player = (Player) sender;
        if (!provider.playerExits(player)) {
            player.sendMessage("§cYou cannot do this yet...");
            return true;
        }

        if (!manager.isInCheck(player)) {
            player.sendMessage(ConfigUtil.getString("messages.auth-command.already-verified-message"));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ConfigUtil.getString("messages.auth-command.help-message"));
        }

        else if (args.length == 1) {
            if (!plugin.isConnected()){
                player.sendMessage("§cOps, the bot connection failed. You cannot do this now.");
                return true;
            }

            final PlayerData playerData = manager.getPlayerData(player);
            if (!args[0].equals(playerData.getCheckCode()) &&
                    !provider.isBackupCode(player, args[0])) {
                if (playerData.getLeftRights() > 1) {
                    manager.failPlayer(player, 1);
                } else {
                    manager.failPlayer(player);
                }
                return true;
            }

            if (provider.isBackupCode(player, args[0])) {
                provider.removeBackupCode(player, args[0]);
                player.sendMessage(ConfigUtil.getString("messages.auth-command.backup-code-used"));
            }

            provider.authPlayer(player);
            player.sendMessage(ConfigUtil.getString("messages.auth-command.auth-success-message"));
        }
        return true;
    }
}
