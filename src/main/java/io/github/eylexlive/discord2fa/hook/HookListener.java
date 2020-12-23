package io.github.eylexlive.discord2fa.hook;

import org.bukkit.event.Listener;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.2
 */

public class HookListener {

    private final String className;

    public HookListener(String className) {
        this.className = className;
    }

    public Listener getListener() {
        final Class<Listener> listenerClass;
        try {
            listenerClass = (Class<Listener>) Class.forName(className);
            return listenerClass.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
