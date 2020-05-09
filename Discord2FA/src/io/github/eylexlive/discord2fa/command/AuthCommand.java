package io.github.eylexlive.discord2fa.command;

import io.github.eylexlive.discord2fa.Main;
import io.github.eylexlive.discord2fa.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

/*
*	Created by EylexLive on Feb 23, 2020.
*	Currently version: 2.0
*/

public class AuthCommand implements CommandExecutor {
    private Main plugin;
    public AuthCommand() {
        this.plugin = Main.getInstance();
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (cmd.getName().equalsIgnoreCase("auth")){
            if (!(sender instanceof Player)) return false;
            Player player = (Player) sender;
            String prefix = "messages.auth-command.";
            if (args.length == 0) {
                if (this.plugin.getDiscord2FAManager().isInCheck(player)) {
                    player.sendMessage(Utils.translate(this.plugin.getConfig().getString(prefix +"help-message")));
                }else {
                    player.sendMessage("§cYou cannot do this yet...");
                }
                return false;
            }else if (args.length == 1) {
                if (!this.plugin.getDiscord2FAManager().isInCheck(player)) {
                    player.sendMessage(Utils.translate(this.plugin.getConfig().getString(prefix +"already-verified-message")));
                    return false;
                }
                if (!args[0].equalsIgnoreCase(this.plugin.getDiscord2FAManager().getCheckCode(player)) && !this.plugin.getDiscord2FAManager().isBackupCode(player,args[0])) {
                    if (this.plugin.getDiscord2FAManager().getLeftRights(player) > 1) {
                        this.plugin.getDiscord2FAManager().setLeftRights(player,this.plugin.getDiscord2FAManager().getLeftRights(player)-1);
                        String message = this.plugin.getConfig().getString(prefix +"invalid-code-message");
                        message = message.replace("%rights%",String.valueOf(this.plugin.getDiscord2FAManager().getLeftRights(player)));
                        player.sendMessage(Utils.translate(message));
                        if (!this.plugin.getConfig().getBoolean("logs.enabled")) return false;
                        this.plugin.getConfig().getStringList("logs.admin-ids").forEach(id-> {
                            try {
                                Objects.requireNonNull(this.plugin.getBot().getUserById(id)).openPrivateChannel().complete().sendMessage(this.plugin.getConfig().getString("logs.player-entered-wrong-code").replace("%player%",player.getName()).replace("%left%",this.plugin.getDiscord2FAManager().getLeftRights(player)+"")).queue();
                            }catch (Exception e) {
                                this.plugin.getLogger().warning("Could not find discord user with id "+id);
                            }
                        });
                    }else {
                        this.plugin.getDiscord2FAManager().setLeftRights(player,Integer.parseInt(this.plugin.getConfig().getString("number-of-rights")));
                        String command = this.plugin.getConfig().getString("rights-reached-console-command");
                        command = command.replace("%player%",player.getName());
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),command);
                        if (!this.plugin.getConfig().getBoolean("logs.enabled")) return false;
                        this.plugin.getConfig().getStringList("logs.admin-ids").forEach(id-> {
                            try {
                                Objects.requireNonNull(this.plugin.getBot().getUserById(id)).openPrivateChannel().complete().sendMessage(this.plugin.getConfig().getString("logs.player-reached-limit").replace("%player%",player.getName())).queue();
                            }catch (Exception e) {
                                this.plugin.getLogger().warning("Could not find discord user with id "+id);
                            }
                        });
                    }
                    return false;
                }
                if (this.plugin.getDiscord2FAManager().isBackupCode(player,args[0])) {
                    this.plugin.getDiscord2FAManager().removeBackupCode(player,args[0]);
                    player.sendMessage(Utils.translate(this.plugin.getConfig().getString(prefix+"backup-code-used")));
                }
                this.plugin.getDiscord2FAManager().auth(player,player.getAddress().getAddress().getHostAddress()+"");
                player.sendMessage(Utils.translate(this.plugin.getConfig().getString(prefix +"auth-success-message")));
                this.plugin.getNMS().getSitInterface().unsitPlayer(player);
                if (!this.plugin.getConfig().getBoolean("logs.enabled")) return false;
                this.plugin.getConfig().getStringList("logs.admin-ids").forEach(id-> {
                    try {
                        Objects.requireNonNull(this.plugin.getBot().getUserById(id)).openPrivateChannel().complete().sendMessage(this.plugin.getConfig().getString("logs.player-authenticated").replace("%player%",player.getName())).queue();
                    }catch (Exception e) {
                        this.plugin.getLogger().warning("Could not find discord user with id "+id);
                    }
                });
            }
        }
        return false;
    }
}