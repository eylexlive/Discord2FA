package io.github.eylexlive.discord2fa.manager;

import io.github.eylexlive.discord2fa.Main;

import java.util.Objects;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 2.6
 */

public class LogManager {
    private final Main plugin;
    public LogManager(Main plugin) {
        this.plugin = plugin;
    }
    public void sendLog(String path) {
        this.plugin.getConfig().getStringList("logs.admin-ids")
                .forEach(id -> Objects.requireNonNull(this.plugin.getBot().getUserById(id))
                .openPrivateChannel()
                .complete()
                .sendMessage(path)
                .queue());
    }
}
