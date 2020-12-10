package io.github.eylexlive.discord2fa.bot;

import io.github.eylexlive.discord2fa.Main;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.1
 */

public class Bot {

    private final Main plugin;

    private final String token;

    public static JDA jda = null;

    public Bot(String token, Main plugin) {
        this.token = token;
        this.plugin = plugin;
    }

    public void login() {
        try {
            if (token != null && token.equals("Your token here.")) {
                plugin.getLogger().warning("Please put your bot's token in config.");
                return;
            }
            jda = new JDABuilder(AccountType.BOT)
                    .setToken(token)
                    .setAutoReconnect(true)
                    .build();
        } catch (LoginException loginException) {
            plugin.getLogger().severe("Bot failed to connect..!");
            plugin.getLogger().severe("Error cause: " + loginException.getLocalizedMessage());
        }
    }
}
