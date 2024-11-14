package org.system.bank.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "app.indexing")
@Component
@Data
public class IndexingProperties {
    private boolean enabled = true;
    private int batchSize = 100;
    private long delayMillis = 100;
    private boolean autoIndexOnStartup = true;
    private int maxRetries = 3;
    private long retryDelayMillis = 1000;
}