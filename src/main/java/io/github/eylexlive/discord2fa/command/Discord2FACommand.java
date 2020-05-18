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
 *	Currently version: 2.2
 */

public class Discord2FACommand implements CommandExecutor {
    private Main plugin;
    private String[] mainMessage;
    public Discord2FACommand() {
        this.plugin = Main.getInstance();
        this.mainMessage = new String[]
                {
                "§6§lDiscord2FA running on the server. §f§lVersion: §6§lv"+
                        this.plugin.getDescription().getVersion(),
                "§fMade by:§6§l EylexLive"
        };
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (cmd.getName().equalsIgnoreCase("discord2fa")) {
            if (!sender.hasPermission("discord2fa.admin")) {
                sender.sendMessage(mainMessage);
                return false;
            }
            String player,discord;
            String prefix = "messages.discord2fa-command.";
            if (args.length == 0) {
                sender.sendMessage(mainMessage);
                sender.sendMessage("§fBot status: §"+(this.plugin.getConnectStatus() ? "aConnected." : "cConnect failed!"));
                String help_message = this.plugin.getConfig().getString(prefix+"help-message");
                help_message = help_message.replace("&","§");
                sender.sendMessage(help_message.split("%nl%"));
            }else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("verifyList")) {
                    if (this.plugin.isMySQLEnabled()) {
                        sender.sendMessage("§cThis mode is turned off while mysql is enabled right now.");
                        return false;
                    }
                    StringBuilder builder = new StringBuilder();
                    this.plugin.getYamlDatabase().getDatabaseConfiguration().getConfigurationSection("verify").getKeys(false).forEach(player2 -> {
                        if(builder.length() > 0) {
                            builder.append(", ");
                        }
                        builder.append(player2+"/"+this.plugin.getYamlDatabase().getDatabaseConfiguration().getString("verify."+player2+".discord"));
                    });
                    String verifyList_message = this.plugin.getConfig().getString(prefix+"verifyList-message");
                    verifyList_message = verifyList_message.replace("&","§")
                            .replace("%list%",builder.toString());
                    sender.sendMessage(verifyList_message.split("%nl%"));
                }else if (args[0].equalsIgnoreCase("reloadConfig")) {
                    this.plugin.reloadConfig();
                    sender.sendMessage(Color.translate(this.plugin.getConfig().getString(prefix+"reload-success")));
                }
            }else if (args.length == 3) {
                player = args[1];
                discord = args[2];
                if (args[0].equalsIgnoreCase("addToVerifyList")) {
                    this.plugin.getDiscord2FAManager().addPlayerToVerifyList(player, discord);
                    String message = this.plugin.getConfig().getString(prefix+"added-to-verifyList-message");
                    message = message.replace("%player%",player);
                    message = message.replace("%id%",discord);
                    sender.sendMessage(Color.translate(message));

                } else if (args[0].equalsIgnoreCase("removeFromVerifyList")) {
                    this.plugin.getDiscord2FAManager().removePlayerFromVerifyList(player);
                    String message = this.plugin.getConfig().getString(prefix+"removed-from-verifyList-message");
                    message = message.replace("%player%",player);
                    message = message.replace("%id%",discord);
                    sender.sendMessage(Color.translate(message));
                }
            }else if (args.length == 2) {
                player = args[1];
                if (args[0].equalsIgnoreCase("removeFromCheck")) {
                    assert Bukkit.getPlayer(player) != null;
                    Player bukkitPlayer = Bukkit.getPlayer(player);
                    this.plugin.getDiscord2FAManager().removePlayerFromCheck(bukkitPlayer);
                    this.plugin.getSitManager().unSitPlayer(bukkitPlayer);

                }else if (args[0].equalsIgnoreCase("generateBackupCodes")) {
                    List<String> codes = this.plugin.getDiscord2FAManager().generateBackupCodes(player);
                    String message = this.plugin.getConfig().getString(prefix+"backup-codes-generated");
                    message = message.replace("%player%",player);
                    message = message.replace("%codes%",codes.toString());
                    sender.sendMessage(Color.translate(message));
                }
            }
        }
        return false;
    }
}