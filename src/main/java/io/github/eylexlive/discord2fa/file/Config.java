package io.github.eylexlive.discord2fa.file;

import io.github.eylexlive.discord2fa.Main;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.0
 */

public class Config extends YamlConfiguration {

    private final File file;

    public Config(String path) {
        file = new File(Main.getInstance().getDataFolder(), path + ".yml");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            saveDefaults(path);
        }
        reload();
    }

    private void saveDefaults(String path) {
        Main.getInstance().saveResource(path + ".yml", false);
    }

    public void reload() {
        try {
            super.load(this.file);
        } catch (Exception ignored) {}
    }

    public void save() {
        try {
            super.save(this.file);
        } catch (Exception ignored) {}
    }

    public File getFile() {
        return file;
    }
}
