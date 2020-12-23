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

import java.util.List;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.2
 */

public class Discord2FACommand implements CommandExecutor {

    private final Main plugin;
    private final String[] mainMessage;

    public Discord2FACommand(Main plugin) {
        this.plugin = plugin;
        mainMessage = new String[]
                {
                "§6§lDiscord2FA running on the server. §f§lVersion: §6§lv"+
                        plugin.getDescription().getVersion(),
                "§fMade by:§6§l EylexLive §fDiscord: §6§lUmut Erarslan#8378",
                        "",
                        "https://www.spigotmc.org/resources/75451"
        };
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        final Discord2FAManager discord2FAManager = plugin.getDiscord2FAManager();
        final Provider provider = plugin.getProvider();

        Player onlinePlayer = null;
        if (args.length >= 2) {
            onlinePlayer = plugin.getServer().getPlayer(args[1]);
            if (onlinePlayer == null)
                return true;
        }

        if (args.length == 0) {
            sender.sendMessage(mainMessage);
            if (sender.hasPermission("discord2fa.admin")) {
                sender.sendMessage(
                        "§fBot status: §"+ (plugin.getConnectStatus() ? "aConnected." : "cConnect failed!")
                );
                String helpMessage = plugin.getConfig().getString("messages.discord2fa-command.help-message");
                helpMessage = Color.translate(helpMessage);
                sender.sendMessage(helpMessage.split("%nl%"));
            }
        }
        else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("verifylist")) {
                if (!sender.hasPermission("discord2fa.admin")) {
                    sender.sendMessage("§cYou do not have permission to use that command.");
                    return true;
                }
                String verifyListMessage = plugin.getConfig().getString("messages.discord2fa-command.verifyList-message");
                verifyListMessage = Color.translate(verifyListMessage)
                        .replace("%list%", provider.getListMessage());
                sender.sendMessage(verifyListMessage.split("%nl%"));
            }
            else if (args[0].equalsIgnoreCase("reloadconfig")) {
                if (!sender.hasPermission("discord2fa.admin")) {
                    sender.sendMessage("§cYou do not have permission to use that command.");
                    return true;
                }
                plugin.reloadConfig();
                sender.sendMessage(Color.translate(plugin.getConfig().getString("messages.discord2fa-command.reload-success")));
            }
            else if (args[0].equalsIgnoreCase("enable")) {
                if (!plugin.getConfig().getBoolean("authentication-for-players.enabled")) {
                    sender.sendMessage("§cYou can do it when 2FA enabled for players.");
                    return true;
                }
                discord2FAManager.sendEnabling2FARequest((Player) sender);
            }
            else if (args[0].equalsIgnoreCase("disable")) {
                if (!plugin.getConfig().getBoolean("authentication-for-players.enabled")) {
                    sender.sendMessage("§cYou can do it when 2FA enabled for players.");
                    return true;
                }
                discord2FAManager.disable2FA((Player) sender);
            }
        }
        else if (args.length == 2) {
            if (!sender.hasPermission("discord2fa.admin")) {
                sender.sendMessage("§cYou do not have permission to use that command.");
                return true;
            }
            if (args[0].equalsIgnoreCase("removefromcheck")) {
                discord2FAManager.removePlayerFromCheck(onlinePlayer);
            }
            else if (args[0].equalsIgnoreCase("generatebackupcodes")) {
                final List<String> codes = provider.generateBackupCodes(onlinePlayer);
                String message = plugin.getConfig().getString("messages.discord2fa-command.backup-codes-generated");
                message = message.replace("%player%", onlinePlayer.getName());
                message = message.replace("%codes%", codes.toString());
                sender.sendMessage(Color.translate(message));
            }
        }
        else if (args.length == 3) {
            if (!sender.hasPermission("discord2fa.admin")) {
                sender.sendMessage("§cYou do not have permission to use that command.");
                return true;
            }
            final String discord = args[2];
            if (args[0].equalsIgnoreCase("addtoverifylist")) {
                if (discord.length() != 18) {
                    sender.sendMessage(Color.translate(plugin.getConfig().getString("messages.discord2fa-command.invalid-discord-id")));
                    return true;
                }
                provider.addToVerifyList(onlinePlayer, discord);
                String message = plugin.getConfig().getString("messages.discord2fa-command.added-to-verifyList-message");
                message = message.replace("%player%", onlinePlayer.getName());
                message = message.replace("%id%", discord);
                sender.sendMessage(Color.translate(message));
            }
            else if (args[0].equalsIgnoreCase("removefromverifylist")) {
                provider.removeFromVerifyList(onlinePlayer);
                String message = plugin.getConfig().getString("messages.discord2fa-command.removed-from-verifyList-message");
                message = message.replace("%player%", onlinePlayer.getName());
                message = message.replace("%id%", discord);
                sender.sendMessage(Color.translate(message));
            }
        }
        return true;
    }
}