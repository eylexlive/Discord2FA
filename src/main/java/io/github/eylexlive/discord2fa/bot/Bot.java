package io.github.eylexlive.discord2fa.bot;

import io.github.eylexlive.discord2fa.Main;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.util.concurrent.CompletableFuture;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.2
 */

public class Bot {

    private final Main plugin;

    private final String token;

    private JDA jda = null;

    public Bot(String token, Main plugin) {
        this.token = token;
        this.plugin = plugin;
    }

    public Bot login() {
        if (token != null && token.equals("Your token here.")) {
            plugin.getLogger().warning("Please put your bot's token in config.");
            return this;
        }

        if (jda != null)
            jda.shutdown();

        try {
            jda = new JDABuilder(AccountType.BOT)
                    .setToken(token)
                    .setAutoReconnect(true)
                    .build();
            try {
                jda.awaitReady();
            } catch (InterruptedException e) {
                plugin.getLogger().warning("Connection failed! Please restart the server!");
            }
        } catch (LoginException e) {
            plugin.getLogger().severe("Bot failed to connect..!");
            plugin.getLogger().severe("Error cause: " + e.getLocalizedMessage());
        }
        return this;
    }

    public void logout() {
        if (jda != null) {
            final CompletableFuture<Void> future = new CompletableFuture<>();
            jda.addEventListener(new ListenerAdapter() {
                @Override
                public void onShutdown(@NotNull ShutdownEvent event) {
                    future.complete(null);
                }
            });
            jda.shutdownNow();
        }
    }

    public JDA getJDA() {
        return jda;
    }
}
