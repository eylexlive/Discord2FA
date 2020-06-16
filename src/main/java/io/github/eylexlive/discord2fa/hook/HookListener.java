package io.github.eylexlive.discord2fa.hook;

import org.bukkit.event.Listener;

public class HookListener {
    private String className;
    public HookListener(String className) {
        this.className = className;
    }
    public Listener getListener() {
        Class<Listener> listenerClass;
        try {
            listenerClass = (Class<Listener>) Class.forName(this.className);
        } catch (Exception ignored) {
            return null;
        }
        Listener eventListener;
        try {
            eventListener = listenerClass.newInstance();
        } catch (Exception ignored) {
            return null;
        }
        return eventListener;
    }
}
