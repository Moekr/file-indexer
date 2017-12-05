package com.moekr.indexer.logic;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "indexer")
public class LogicConfig {
    private String directory;
    private int interval = 1000;
    private boolean syncBeforeStart = true;
    private Icon icon = new Icon();

    @Data
    public static class Icon{
        private String suffixDefine;
        private String typeDefine;
        private String iconDirectory;
    }
}
