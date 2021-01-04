package io.github.eylexlive.discord2fa.bot;

import io.github.eylexlive.discord2fa.Discord2FA;
import io.github.eylexlive.discord2fa.util.ConfigUtil;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.util.concurrent.CompletableFuture;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.4
 */

public class Bot {

    private final Discord2FA plugin;

    private JDA jda = null;

    public Bot(Discord2FA plugin) {
        this.plugin = plugin;
    }

    public void login() {
        final String token = ConfigUtil.getString("bot-token");
        if (token.equals("Your token here.")) {
            plugin.getLogger().warning("Please put your bot's token in config.");
            return;
        }

        CompletableFuture.runAsync(() -> {

            if (jda != null)
                jda.shutdown();

            try {
                final ActivityEntry activityEntry = new ActivityEntry();
                jda = new JDABuilder(AccountType.BOT)
                        .setToken(token)
                        .setAutoReconnect(true)
                        .build();

                if (activityEntry.isEnabled())
                    jda.getPresence().setActivity(Activity.of(activityEntry.getType(), activityEntry.getValue()));

            } catch (LoginException e) {
                plugin.getLogger().severe("Bot failed to connect..!");
                plugin.getLogger().severe("Error cause: " + e.getLocalizedMessage());
            }
        });

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

    private class ActivityEntry {

        public Activity.ActivityType getType() {
            final Activity.ActivityType activityType;
            final String activity = ConfigUtil.getString("bot-activity.type");
            try {
                activityType = Activity.ActivityType.valueOf(activity);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid activity type '" + activity + "' Types: [DEFAULT, LISTENING, WATCHING]");
                return Activity.ActivityType.DEFAULT;
            }
            return activityType;
        }

        public String getValue() {
            return ConfigUtil.getString("bot-activity.value");
        }

        public boolean isEnabled() {
            return ConfigUtil.getBoolean("bot-activity.enabled");
        }
    }
}
