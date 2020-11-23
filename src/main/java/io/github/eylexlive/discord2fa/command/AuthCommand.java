package io.github.eylexlive.discord2fa.command;

import io.github.eylexlive.discord2fa.Main;
import io.github.eylexlive.discord2fa.manager.Discord2FAManager;
import io.github.eylexlive.discord2fa.util.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 2.9
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
        final Player player = (Player) sender;
        final Discord2FAManager discord2FAManager = this.plugin.getDiscord2FAManager();
        if (!discord2FAManager.isAddedToVerifyList(player.getName())) {
            player.sendMessage("§cYou cannot do this yet...");
            return true;
        } else if (!discord2FAManager.isInCheck(player)) {
            player.sendMessage(Color.translate(this.plugin.getConfig().getString("messages.auth-command.already-verified-message")));
            return true;
        }
        if (args.length == 0) {
            player.sendMessage(Color.translate(this.plugin.getConfig().getString("messages.auth-command.help-message")));
        } else if (args.length == 1) {
            if (!this.plugin.getConnectStatus()){
                player.sendMessage("§cOps, the bot connection failed. You cannot do this now.");
                return true;
            }
            else if (!discord2FAManager.isInCheck(player)) {
                player.sendMessage(Color.translate(this.plugin.getConfig().getString("messages.auth-command.already-verified-message")));
                return true;
            }
            final UUID uuid = player.getUniqueId();
            final List<String> adminIds = this.plugin.getConfig().getStringList("logs.admin-ids");
            if (!args[0].equalsIgnoreCase(discord2FAManager.getCheckCode().get(uuid)) &&
                    !discord2FAManager.isBackupCode(player,args[0])) {
                if (discord2FAManager.getLeftRights().get(uuid) > 1) {
                    discord2FAManager.getLeftRights().put(uuid, discord2FAManager.getLeftRights().get(uuid) - 1);
                    String message = this.plugin.getConfig().getString("messages.auth-command.invalid-code-message");
                    message = message.replace("%rights%", String.valueOf(discord2FAManager.getLeftRights().get(uuid)));
                    player.sendMessage(Color.translate(message));
                    if (!this.plugin.getConfig().getBoolean("logs.enabled"))
                        return true;
                    this.plugin.getLogManager().sendLog(adminIds, this.plugin.getConfig().getString("logs.player-entered-wrong-code").replace("%player%",player.getName()).replace("%left%",discord2FAManager.getLeftRights().get(uuid)+""));
                } else {
                    discord2FAManager.getLeftRights().put(uuid, this.plugin.getConfig().getInt("number-of-rights"));
                    String command = this.plugin.getConfig().getString("rights-reached-console-command");
                    command = command.replace("%player%",player.getName());
                    this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), command);
                    if (!this.plugin.getConfig().getBoolean("logs.enabled"))
                        return true;
                    this.plugin.getLogManager().sendLog(adminIds, this.plugin.getConfig().getString("logs.player-reached-limit").replace("%player%",player.getName()));
                }
                return true;
            }
            if (discord2FAManager.isBackupCode(player,args[0])) {
                discord2FAManager.removeBackupCode(player,args[0]);
                player.sendMessage(Color.translate(this.plugin.getConfig().getString("messages.auth-command.backup-code-used")));
            }
            discord2FAManager.auth(player,player.getAddress().getAddress().getHostAddress() + "");
            player.sendMessage(Color.translate(this.plugin.getConfig().getString("messages.auth-command.auth-success-message")));
            this.plugin.getSitManager().unSitPlayer(player);
            if (!this.plugin.getConfig().getBoolean("logs.enabled"))
                return true;
            this.plugin.getLogManager().sendLog(adminIds, this.plugin.getConfig().getString("logs.player-authenticated")
                    .replace("%player%",player.getName()));
        }
        return true;
    }
}
