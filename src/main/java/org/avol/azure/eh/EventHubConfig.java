package org.avol.azure.eh;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EventHubConfig {

    private String name;
    private String nameSpace;
    private String storageConnectionString;
    private String storageContainerName;
    private String consumerGroup;
    private SasKeys sender;
    private SasKeys listener;

    @Setter
    @Getter
    public static class SasKeys {
        private String keyName;
        private String key;
    }
}
