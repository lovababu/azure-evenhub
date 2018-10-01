package org.avol.azure.eh.sender;

import com.google.gson.Gson;
import com.microsoft.azure.eventhubs.ConnectionStringBuilder;
import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.eventhubs.EventHubException;
import com.microsoft.azure.eventhubs.RetryPolicy;
import org.avol.azure.eh.EventHubConfig;
import org.avol.azure.eh.model.Equipment;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class EventPublisher implements Runnable{

    private final EventHubConfig eventHubConfig;

    public EventPublisher(EventHubConfig eventHubConfig) {
        this.eventHubConfig = eventHubConfig;
    }

    private void publish(String message) {
        final ConnectionStringBuilder connectionStringBuilder =
                new ConnectionStringBuilder().setEventHubName(eventHubConfig.getName())
                        .setNamespaceName(eventHubConfig.getNameSpace())
                        .setSasKeyName(eventHubConfig.getSender().getKeyName())
                        .setSasKey(eventHubConfig.getSender().getKey());
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            final EventHubClient eventHubClient = EventHubClient.createSync(connectionStringBuilder.toString(),
                    RetryPolicy.getDefault(), executorService);

            final Gson gson = new Gson();
            IntStream.range(1, 10).forEach(i -> {
                String payLoad = gson.toJson(Equipment.builder()
                        .id(message)
                        .engineSpeed(100 * i)
                        .fuelLevel(i)
                        .runningTime(System.currentTimeMillis() * i)
                        .build()
                );

                EventData eventData = EventData.create(payLoad.getBytes());
                try {
                    eventHubClient.sendSync(eventData);
                    System.out.println("-> publish message completed, " + i);
                    TimeUnit.MILLISECONDS.sleep(10000);
                } catch (EventHubException | InterruptedException e) {
                    e.printStackTrace();
                }
            });

            eventHubClient.closeSync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }

    @Override
    public void run() {
        this.publish("Avol:" + new Random(100).nextInt());
    }
}
