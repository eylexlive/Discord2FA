package io.github.eylexlive.discord2fa.command;

import io.github.eylexlive.discord2fa.Main;
import io.github.eylexlive.discord2fa.util.Color;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 2.8
 */

public class Discord2FACommand implements CommandExecutor {
    private final Main plugin;
    private final String[] mainMessage;
    public Discord2FACommand(Main plugin) {
        this.plugin = plugin;
        this.mainMessage = new String[]
                {
                "§6§lDiscord2FA running on the server. §f§lVersion: §6§lv"+
                        this.plugin.getDescription().getVersion(),
                "§fMade by:§6§l EylexLive §fDiscord: §6§lUmut Erarslan#8378",
                        "",
                        "https://www.spigotmc.org/resources/75451"
        };
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!sender.hasPermission("discord2fa.admin")) {
            sender.sendMessage(mainMessage);
            return true;
        }
        final String player, discord;
        final String prefix = "messages.discord2fa-command.";
        switch (args.length) {
            case 0:
                sender.sendMessage(mainMessage);
                sender.sendMessage(
                        "§fBot status: §"+ (this.plugin.getConnectStatus() ? "aConnected." : "cConnect failed!")
                );
                String helpMessage = this.plugin.getConfig().getString(prefix + "help-message");
                helpMessage = Color.translate(helpMessage);
                sender.sendMessage(helpMessage.split("%nl%"));
                break;
            case 1:
                if (args[0].equalsIgnoreCase("verifylist")) {
                    if (this.plugin.isMySQLEnabled()) {
                        sender.sendMessage("§cThis mode is turned off while mysql is enabled right now.");
                        return true;
                    }
                    final StringBuilder builder = new StringBuilder();
                    this.plugin.getYamlDatabase().getDatabaseConfiguration()
                            .getConfigurationSection("verify")
                            .getKeys(false)
                            .forEach(key -> {
                                if(builder.length() > 0)
                                    builder.append(", ");
                                builder.append(key + "/" + this.plugin.getYamlDatabase().getDatabaseConfiguration().getString("verify."+ key +".discord"));
                    });
                    String verifyListMessage = this.plugin.getConfig().getString(prefix + "verifyList-message");
                    verifyListMessage = Color.translate(verifyListMessage)
                            .replace("%list%", builder.toString());
                    sender.sendMessage(verifyListMessage.split("%nl%"));
                } else if (args[0].equalsIgnoreCase("reloadconfig")) {
                    this.plugin.reloadConfig();
                    sender.sendMessage(Color.translate(this.plugin.getConfig().getString(prefix + "reload-success")));
                }
                break;
            case 2:
                player = args[1];
                if (args[0].equalsIgnoreCase("removefromcheck")) {
                    if (Bukkit.getPlayer(player) == null)
                        return true;
                    final Player serverPlayer = Bukkit.getPlayer(player);
                    this.plugin.getDiscord2FAManager().removePlayerFromCheck(serverPlayer);
                    this.plugin.getSitManager().unSitPlayer(serverPlayer);
                } else if (args[0].equalsIgnoreCase("generatebackupcodes")) {
                    final List<String> codes = this.plugin.getDiscord2FAManager().generateBackupCodes(player);
                    String message = this.plugin.getConfig().getString(prefix + "backup-codes-generated");
                    message = message.replace("%player%",player);
                    message = message.replace("%codes%",codes.toString());
                    sender.sendMessage(Color.translate(message));
                }
                break;
            case 3:
                player = args[1]; discord = args[2];
                if (args[0].equalsIgnoreCase("addtoverifylist")) {
                    this.plugin.getDiscord2FAManager().addPlayerToVerifyList(player, discord);
                    String message = this.plugin.getConfig().getString(prefix + "added-to-verifyList-message");
                    message = message.replace("%player%",player);
                    message = message.replace("%id%",discord);
                    sender.sendMessage(Color.translate(message));
                } else if (args[0].equalsIgnoreCase("removefromverifylist")) {
                    this.plugin.getDiscord2FAManager().removePlayerFromVerifyList(player);
                    String message = this.plugin.getConfig().getString(prefix + "removed-from-verifyList-message");
                    message = message.replace("%player%",player);
                    message = message.replace("%id%",discord);
                    sender.sendMessage(Color.translate(message));
                }
                break;
        }
        return true;
    }
}