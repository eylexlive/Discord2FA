package io.github.eylexlive.discord2fa.bot;

import io.github.eylexlive.discord2fa.Main;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 2.3
 */

public class Bot {
    private Main plugin;
    public static JDA jda = null;
    private String token;
    public Bot(String token, Main plugin) {
        this.plugin = Main.getInstance();
        this.token = token;
        this.plugin = plugin;
    }
    public void login() {
        try {
            if (this.token != null && this.token.equals("Your token here.")) {
                this.plugin.getLogger().warning("Please put your bot's token in config.");
                return;
            }
            jda = new JDABuilder(this.token)
                    .build();
        } catch (LoginException e) {
            this.plugin.getLogger().severe("Bot failed to connect!");
            this.plugin.getLogger().severe("Error cause: "+e.getLocalizedMessage());
        }
    }
}
