package io.github.eylexlive.discord2fa.manager;

import io.github.eylexlive.discord2fa.Main;
import net.dv8tion.jda.api.entities.User;

import java.util.List;
import java.util.Objects;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 2.9
 */

public class LogManager {
    private final Main plugin;
    public LogManager(Main plugin) {
        this.plugin = plugin;
    }
    public void sendLog(List<String> stringList, String path) {
       stringList.forEach(id ->  {
                    final User user = this.plugin.getBot().getUserById(id);
                    if (user == null)
                        return;
                    Objects.requireNonNull(user)
                            .openPrivateChannel()
                            .complete()
                            .sendMessage(path)
                            .queue();
                });
    }
}
