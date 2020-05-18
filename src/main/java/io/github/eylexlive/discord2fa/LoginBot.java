package io.github.eylexlive.discord2fa;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 2.2
 */

public class LoginBot {
    private Main plugin;
    public static JDA jda = null;
    public LoginBot() {
        this.plugin = Main.getInstance();
        this.login();
    }
    private void login() {
        try {
            String token = this.plugin.getConfig().getString("bot-token");
            if (token != null && token.equals("Your token here.")) {
                this.plugin.getLogger().warning("Please put your bot's token in config.");
                return;
            }
                jda = new JDABuilder(token)
                        .build();
        } catch (LoginException e) {
            this.plugin.getLogger().severe("Bot failed to connect!");
            this.plugin.getLogger().severe("Error cause: "+e.getLocalizedMessage());
        }
    }
}
