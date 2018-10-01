package org.avol.azure.eh;

import com.google.gson.Gson;
import com.microsoft.azure.eventhubs.EventHubException;
import org.avol.azure.eh.listner.EventListener;
import org.avol.azure.eh.sender.EventPublisher;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException, EventHubException {
        Gson gson = new Gson();
        EventHubConfig config = gson.fromJson(new FileReader(Paths.get("src/main/resources/eventhub-config.json").toFile()),
                EventHubConfig.class);

        Thread pub = new Thread(new EventPublisher(config));
        Thread sub = new Thread(new EventListener(config));
        pub.start();
        sub.start();
    }
}
