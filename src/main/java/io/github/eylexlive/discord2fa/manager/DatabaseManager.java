package io.github.eylexlive.discord2fa.manager;

import io.github.eylexlive.discord2fa.Main;
import io.github.eylexlive.discord2fa.database.MysqlDatabase;
import io.github.eylexlive.discord2fa.database.YmlDatabase;

/*
 *	Created by EylexLive on Feb 23, 2020.
 *	Currently version: 2.6
 */

public class DatabaseManager {
    private final Main plugin;
    public DatabaseManager(Main plugin) {
        this.plugin = plugin;
    }
    public void setup() {
        if (this.plugin.isMySQLEnabled()) {
            this.plugin.mySQLDatabase = new MysqlDatabase(this.plugin);
        } else {
            this.plugin.yamlDatabase = new YmlDatabase();
        }
    }
}
