package io.github.eylexlive.discord2fa.database;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 2.3
 */

public class YmlDb {
    private File databaseFile;
    @Getter private FileConfiguration databaseConfiguration;
    public YmlDb() {
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
}
