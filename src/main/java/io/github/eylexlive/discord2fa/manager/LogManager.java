package io.github.eylexlive.discord2fa.manager;

import io.github.eylexlive.discord2fa.Main;

import java.util.Objects;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 2.4
 */

public class LogManager {
    private Main plugin;
    public LogManager(Main plugin) {
        this.plugin = plugin;
    }
    public void sendLog(String path) {
        this.plugin.getConfig().getStringList("logs.admin-ids").forEach(id-> {
            try {
                Objects.requireNonNull(this.plugin.getBot().getUserById(id)).openPrivateChannel().complete().sendMessage(path).queue();
            }catch (Exception e) {
                this.plugin.getLogger().warning("Could not find discord user with id " + id);
            }
        });
    }
}
