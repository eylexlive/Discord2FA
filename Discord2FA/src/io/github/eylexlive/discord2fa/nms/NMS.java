package io.github.eylexlive.discord2fa.nms;

import io.github.eylexlive.discord2fa.Main;
import io.github.eylexlive.discord2fa.SitInterface;
import io.github.eylexlive.discord2fa.nms.versions.OtherVersion;
import io.github.eylexlive.discord2fa.util.Utils;

/*
*	Created by EylexLive on Feb 23, 2020.
*	Currently version: 2.0
*/

public class NMS {
    private Main plugin;
    private SitInterface sitInterface;
    public NMS() {
        this.plugin = Main.getInstance();
        this.loadSitInterface();
    }
    public SitInterface getSitInterface() {
        return this.sitInterface;
    }
    private void loadSitInterface() {
        String version = Utils.getServerVersion();
        this.plugin.getLogger().info("Your server is running version " + version);
        Class versionClass;
        try {
            versionClass = Class.forName("io.github.eylexlive.discord2fa.nms.versions." + version);
        } catch (Exception ex) {
            this.plugin.getLogger().info("Sit interface is not loaded because your server version is "+version);
            this.sitInterface = new OtherVersion();
            return;
        }
        if (versionClass != null) {
            try {
                this.sitInterface = (SitInterface) versionClass.newInstance();
                this.plugin.getLogger().info("Loaded sit interface!");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
