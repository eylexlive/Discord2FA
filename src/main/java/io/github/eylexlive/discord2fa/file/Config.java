package io.github.eylexlive.discord2fa.file;

import io.github.eylexlive.discord2fa.Main;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.1
 */

public class Config extends YamlConfiguration {

    private final File file;

    public Config(String path) {
        file = new File(Main.getInstance().getDataFolder(), (path.endsWith(".yml") ? path : path + ".yml"));
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            saveDefaults(path);
        }
        reload();
    }

    private void saveDefaults(String path) {
        Main.getInstance().saveResource((path.endsWith(".yml") ? path : path + ".yml"), false);
    }

    public void reload() {
        try {
            super.load(this.file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            super.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
