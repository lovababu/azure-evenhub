package org.avol.azure.eh.listner;

import com.microsoft.azure.eventhubs.ConnectionStringBuilder;
import com.microsoft.azure.eventprocessorhost.EventProcessorHost;
import com.microsoft.azure.eventprocessorhost.EventProcessorOptions;
import org.avol.azure.eh.EventHubConfig;

import java.util.concurrent.ExecutionException;

public class EventListener implements Runnable{

    private final EventHubConfig eventHubConfig;

    public EventListener(EventHubConfig eventHubConfig) {
        this.eventHubConfig = eventHubConfig;
    }

    private void subscribe() throws ExecutionException, InterruptedException {
        final ConnectionStringBuilder connectionStringBuilder =
                new ConnectionStringBuilder().setEventHubName(eventHubConfig.getName())
                        .setNamespaceName(eventHubConfig.getNameSpace())
                        .setSasKeyName(eventHubConfig.getListener().getKeyName())
                        .setSasKey(eventHubConfig.getListener().getKey());

        //hostname must be unique
        EventProcessorHost host = new EventProcessorHost(
                EventProcessorHost.createHostName("Avol"),
                eventHubConfig.getName(),
                "$Default",
                connectionStringBuilder.toString(),
                eventHubConfig.getStorageConnectionString(),
                eventHubConfig.getStorageContainerName()
        );

        System.out.println("Registering host named " + host.getHostName());
        EventProcessorOptions options = new EventProcessorOptions();
        options.setExceptionNotification(new ErrorNotificationHandler());

        host.registerEventProcessor(EventProcessor.class, options)
                .whenComplete((unused, e) ->
                {
                    if (e != null)
                    {
                        System.out.println("Failure while registering: " + e.toString());
                        if (e.getCause() != null)
                        {
                            System.out.println("Inner exception: " + e.getCause().toString());
                        }
                    }
                })
                .thenAccept((unused) ->
                {
                    System.out.println("Press enter to stop.");
                    try
                    {
                        System.in.read();
                    }
                    catch (Exception e)
                    {
                        System.out.println("Keyboard read failed: " + e.toString());
                    }
                })
                .thenCompose((unused) ->
                        host.unregisterEventProcessor())
                .exceptionally((e) ->
                {
                    System.out.println("Failure while unregistering: " + e.toString());
                    if (e.getCause() != null)
                    {
                        System.out.println("Inner exception: " + e.getCause().toString());
                    }
                    return null;
                })
                .get(); // Wait for everything to finish before exiting main!

        System.out.println("End of sample");
    }

    @Override
    public void run() {
        try {
            this.subscribe();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}