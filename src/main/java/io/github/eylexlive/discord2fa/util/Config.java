package io.github.eylexlive.discord2fa.util;

import io.github.eylexlive.discord2fa.Discord2FA;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 3.3
 */

public class Config extends YamlConfiguration {

    private final Discord2FA plugin = Discord2FA.getInstance();

    private final File file;

    public Config(String path) {
        final String str = path.endsWith(".yml") ? path : path + ".yml";
        file = new File(plugin.getDataFolder(), str);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.saveResource(str, false);
        }
        copyDefaults();
        reload();
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

    private void copyDefaults() {
        final InputStream inputStream = plugin.getResource(file.getName());

        if (inputStream == null) return;

        final InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        final YamlConfiguration[] cfg = { YamlConfiguration.loadConfiguration(file), YamlConfiguration.loadConfiguration(reader) };

        final Set<String> keys = cfg[1].getConfigurationSection("").getKeys(true);
        final boolean hasUpdate = keys.stream().anyMatch(key -> !cfg[0].contains(key));

        if (!hasUpdate) return;

        keys.stream().filter(key -> !cfg[0].contains(key)).forEach(key -> cfg[0].set(key, cfg[1].get(key)));
        try { cfg[0].save(file); } catch (IOException ignored) { }
    }
}
