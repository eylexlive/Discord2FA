package io.github.eylexlive.discord2fa.database;

import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 2.8
 */

public class YmlDatabase {
    private File databaseFile;
    @Getter private FileConfiguration databaseConfiguration;
    public YmlDatabase() {
        this.loadDatabaseConfiguration();
    }
    private void loadDatabaseConfiguration() {
        databaseFile = new File("plugins/Discord2FA/database.yml");
        databaseConfiguration = YamlConfiguration.loadConfiguration(databaseFile);
    }
    @SneakyThrows
    public void saveDatabaseConfiguration() {
        databaseConfiguration.save(databaseFile);
    }
}
