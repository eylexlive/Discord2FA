package io.github.eylexlive.discord2fa.command;

import io.github.eylexlive.discord2fa.Main;
import io.github.eylexlive.discord2fa.manager.Discord2FAManager;
import io.github.eylexlive.discord2fa.provider.Provider;
import io.github.eylexlive.discord2fa.util.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.2
 */

public class AuthCommand implements CommandExecutor {

    private final Main plugin;

    public AuthCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player))
            return true;
        final Discord2FAManager discord2FAManager = plugin.getDiscord2FAManager();
        final Provider provider = plugin.getProvider();

        final Player player = (Player) sender;
        if (!provider.playerExits(player)) {
            player.sendMessage("§cYou cannot do this yet...");
            return true;
        }
        if (!discord2FAManager.isInCheck(player)) {
            player.sendMessage(Color.translate(plugin.getConfig().getString("messages.auth-command.already-verified-message")));
            return true;
        }
        if (args.length == 0) {
            player.sendMessage(Color.translate(plugin.getConfig().getString("messages.auth-command.help-message")));
        }
        else if (args.length == 1) {
            if (!plugin.getConnectStatus()){
                player.sendMessage("§cOps, the bot connection failed. You cannot do this now.");
                return true;
            }
            else if (!discord2FAManager.isInCheck(player)) {
                player.sendMessage(Color.translate(plugin.getConfig().getString("messages.auth-command.already-verified-message")));
                return true;
            }
            final UUID uuid = player.getUniqueId();
            if (!args[0].equalsIgnoreCase(discord2FAManager.getCheckCode().get(uuid)) &&
                    !provider.isBackupCode(player, args[0])) {
                if (discord2FAManager.getLeftRights().get(uuid) > 1) {
                    discord2FAManager.failPlayer(player, 1);
                } else {
                    discord2FAManager.failPlayer(player);
                }
                return true;
            }
            if (provider.isBackupCode(player, args[0])) {
                provider.removeBackupCode(player, args[0]);
                player.sendMessage(Color.translate(plugin.getConfig().getString("messages.auth-command.backup-code-used")));
            }
            provider.authPlayer(player);
            player.sendMessage(Color.translate(plugin.getConfig().getString("messages.auth-command.auth-success-message")));
        }
        return true;
    }
}
