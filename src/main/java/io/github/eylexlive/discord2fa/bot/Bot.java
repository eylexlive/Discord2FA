package io.github.eylexlive.discord2fa.bot;

import io.github.eylexlive.discord2fa.Main;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 2.7
 */

public class Bot {
    private final Main plugin;
    private final String token;
    @Getter private static Bot instance;
    @Getter public JDA jda = null;
    public Bot(String token, Main plugin) {
        instance = this;
        this.token = token;
        this.plugin = plugin;
    }
    public void login() {
        try {
            if (this.token != null && this.token.equals("Your token here.")) {
                this.plugin.getLogger().warning("Please put your bot's token in config.");
                return;
            }
            this.jda = new JDABuilder(this.token)
                    .build();
        } catch (LoginException e) {
            this.plugin.getLogger().severe("Bot failed to connect!");
            this.plugin.getLogger().severe("Error cause: " + e.getLocalizedMessage());
        }
    }
}
