package io.github.eylexlive.discord2fa.database;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 2.2
 */

public class YAMLDatabase {
    private File databaseFile;
    private FileConfiguration databaseConfiguration;
    public YAMLDatabase() {
        this.loadDatabaseConfiguration();
    }
    private void loadDatabaseConfiguration() {
        databaseFile = new File("plugins/Discord2FA/database.yml");
        databaseConfiguration = YamlConfiguration.loadConfiguration(databaseFile);
    }
    public void saveDatabaseConfiguration() {
        try {
            databaseConfiguration.save(databaseFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public FileConfiguration getDatabaseConfiguration() {
        return this.databaseConfiguration;
    }
}
