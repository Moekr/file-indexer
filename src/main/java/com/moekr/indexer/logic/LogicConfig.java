package com.moekr.indexer.logic;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "indexer")
public class LogicConfig {
    private String directory;
    private int interval = 1000;
    private boolean syncBeforeStart = true;

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public boolean isSyncBeforeStart() {
        return syncBeforeStart;
    }

    public void setSyncBeforeStart(boolean syncBeforeStart) {
        this.syncBeforeStart = syncBeforeStart;
    }
}
