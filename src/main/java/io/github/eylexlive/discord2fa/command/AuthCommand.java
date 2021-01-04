package io.github.eylexlive.discord2fa.command;

import io.github.eylexlive.discord2fa.Discord2FA;
import io.github.eylexlive.discord2fa.data.PlayerData;
import io.github.eylexlive.discord2fa.manager.Discord2FAManager;
import io.github.eylexlive.discord2fa.provider.Provider;
import io.github.eylexlive.discord2fa.util.ConfigUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.4
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

        if (!plugin.isConnected()){
            player.sendMessage("§cOops, the bot connection failed! You cannot do this now.");
        }

        else if (!provider.playerExits(player)) {
            player.sendMessage("§cYou cannot do this yet...");
        }

        else if (!manager.isInCheck(player)) {
            player.sendMessage(ConfigUtil.getString("messages.auth-command.already-verified-message"));
        }

        else if (args.length == 0) {
            player.sendMessage(ConfigUtil.getString("messages.auth-command.help-message"));
        }

        else if (args.length == 1) {

            final String enteredCode = args[0];
            final PlayerData playerData = manager.getPlayerData(player);

            final boolean isBackup = provider.isBackupCode(player, enteredCode);

            if (!enteredCode.equals(playerData.getCheckCode()) && !isBackup) {

                if (playerData.getLeftRights() > 1)
                    manager.failPlayer(player, 1);
                 else
                    manager.failPlayer(player);

                return true;
            }

            CompletableFuture.runAsync(() -> {
                if (isBackup) {
                    provider.removeBackupCode(player, enteredCode);
                    player.sendMessage(ConfigUtil.getString("messages.auth-command.backup-code-used"));
                }

                provider.authPlayer(player);
            }).whenComplete((future, err) -> {
                if (err == null) {
                    player.sendMessage(ConfigUtil.getString("messages.auth-command.auth-success-message"));
                } else {
                    player.sendMessage("§cAn error occurred while authenticating!");
                    err.printStackTrace();
                }
            });
        }
        return true;
    }

}
