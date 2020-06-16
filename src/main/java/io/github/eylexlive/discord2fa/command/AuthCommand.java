package io.github.eylexlive.discord2fa.command;

import io.github.eylexlive.discord2fa.Main;
import io.github.eylexlive.discord2fa.util.Color;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 2.3
 */

public class AuthCommand implements CommandExecutor {
    private Main plugin;
    public AuthCommand() {
        this.plugin = Main.getInstance();
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (cmd.getName().equalsIgnoreCase("auth")){
            if (!(sender instanceof Player))
                return true;
            Player player = (Player) sender;
            String prefix = "messages.auth-command.";
            if (args.length == 0) {
                if (this.plugin.getDiscord2FAManager().isInCheck(player)) {
                    player.sendMessage(Color.translate(this.plugin.getConfig().getString(prefix + "help-message")));
                }else {
                    player.sendMessage("§cYou cannot do this yet...");
                }
                return true;
            } else if (args.length == 1) {
                if (!this.plugin.getConnectStatus()){
                    player.sendMessage("§cOps, the bot connection failed. You cannot do this now.");
                    return true;
                }
                else if (!this.plugin.getDiscord2FAManager().isInCheck(player)) {
                    player.sendMessage(Color.translate(this.plugin.getConfig().getString(prefix +"already-verified-message")));
                    return true;
                }
                UUID uuid = player.getUniqueId();
                if (!args[0].equalsIgnoreCase(this.plugin.getDiscord2FAManager().getCheckCode().get(uuid)) &&
                        !this.plugin.getDiscord2FAManager().isBackupCode(player,args[0])) {
                    if (this.plugin.getDiscord2FAManager().getLeftRights().get(uuid) > 1) {
                        this.plugin.getDiscord2FAManager().getLeftRights().put(uuid,this.plugin.getDiscord2FAManager().getLeftRights().get(uuid)-1);
                        String message = this.plugin.getConfig().getString(prefix +"invalid-code-message");
                        message = message.replace("%rights%",String.valueOf(this.plugin.getDiscord2FAManager().getLeftRights().get(uuid)));
                        player.sendMessage(Color.translate(message));
                        if (!this.plugin.getConfig().getBoolean("logs.enabled")) return true;
                        this.plugin.getConfig().getStringList("logs.admin-ids").forEach(id-> {
                            try {
                                Objects.requireNonNull(this.plugin.getBot().getUserById(id)).openPrivateChannel().complete().sendMessage(this.plugin.getConfig().getString("logs.player-entered-wrong-code").replace("%player%",player.getName()).replace("%left%",this.plugin.getDiscord2FAManager().getLeftRights().get(uuid)+"")).queue();
                            }catch (Exception e) {
                                this.plugin.getLogger().warning("Could not find discord user with id "+id);
                            }
                        });
                    } else {
                        this.plugin.getDiscord2FAManager().getLeftRights().put(uuid,Integer.parseInt(this.plugin.getConfig().getString("number-of-rights")));
                        String command = this.plugin.getConfig().getString("rights-reached-console-command");
                        command = command.replace("%player%",player.getName());
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),command);
                        if (!this.plugin.getConfig().getBoolean("logs.enabled")) return true;
                        this.plugin.getConfig().getStringList("logs.admin-ids").forEach(id-> {
                            try {
                                Objects.requireNonNull(this.plugin.getBot().getUserById(id)).openPrivateChannel().complete().sendMessage(this.plugin.getConfig().getString("logs.player-reached-limit").replace("%player%",player.getName())).queue();
                            }catch (Exception e) {
                                this.plugin.getLogger().warning("Could not find discord user with id "+id);
                            }
                        });
                    }
                    return true;
                }
                if (this.plugin.getDiscord2FAManager().isBackupCode(player,args[0])) {
                    this.plugin.getDiscord2FAManager().removeBackupCode(player,args[0]);
                    player.sendMessage(Color.translate(this.plugin.getConfig().getString(prefix+"backup-code-used")));
                }
                this.plugin.getDiscord2FAManager().auth(player,player.getAddress().getAddress().getHostAddress()+"");
                player.sendMessage(Color.translate(this.plugin.getConfig().getString(prefix +"auth-success-message")));
                this.plugin.getSitManager().unSitPlayer(player);
                if (!this.plugin.getConfig().getBoolean("logs.enabled")) return true;
                this.plugin.getConfig().getStringList("logs.admin-ids").forEach(id-> {
                    try {
                        Objects.requireNonNull(this.plugin.getBot().getUserById(id)).openPrivateChannel().complete().sendMessage(this.plugin.getConfig().getString("logs.player-authenticated").replace("%player%",player.getName())).queue();
                    }catch (Exception e) {
                        this.plugin.getLogger().warning("Could not find discord user with id "+id);
                    }
                });
            }
        }
        return true;
    }
}
