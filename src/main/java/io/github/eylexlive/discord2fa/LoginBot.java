package io.github.eylexlive.discord2fa;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 2.1
 */

public class LoginBot {
    private Main plugin;
    public static JDA jda;
    public LoginBot() {
        this.plugin = Main.getInstance();
        this.login();
    }
    private void login() {
        try {
                jda = new JDABuilder(this.plugin.getConfig().getString("bot-token"))
                        .build();
            this.plugin.getLogger().info("Discord2FA plugin enabled!");
            this.plugin.getLogger().info("The plugin made by EylexLive");
            this.plugin.getLogger().info("Version: v"+this.plugin.getDescription().getVersion());
        } catch (LoginException e) {
            e.printStackTrace();
            this.plugin.getLogger().info("Bot not connected!");
            this.plugin.getLogger().info("Please check upper error.");
            this.plugin.getLogger().info("Disabling plugin!");
            this.plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
    }
}
