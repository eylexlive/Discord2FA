package io.github.eylexlive.discord2fa.hook;

import lombok.SneakyThrows;
import org.bukkit.event.Listener;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 2.8
 */

public class HookListener {
    private final String className;
    public HookListener(String className) {
        this.className = className;
    }
    @SneakyThrows
    public Listener getListener() {
        final Class<Listener> listenerClass = (Class<Listener>) Class.forName(this.className);
        return listenerClass.newInstance();
    }
}
