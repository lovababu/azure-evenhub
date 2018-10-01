package org.avol.azure.eh.listner;

import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventprocessorhost.CloseReason;
import com.microsoft.azure.eventprocessorhost.IEventProcessor;
import com.microsoft.azure.eventprocessorhost.PartitionContext;

import java.nio.charset.StandardCharsets;

public class EventProcessor implements IEventProcessor {
    private int checkpointBatchingCount = 0;

    @Override
    public void onOpen(PartitionContext partitionContext) throws Exception {
        System.out.println("Partition " + partitionContext.getPartitionId() + " is open.");
    }

    @Override
    public void onClose(PartitionContext partitionContext, CloseReason closeReason) throws Exception {
        System.out.println("Partition " + partitionContext.getPartitionId() + " is closed, due to " + closeReason.toString());
    }

    @Override
    public void onEvents(PartitionContext partitionContext, Iterable<EventData> events) throws Exception {
        System.out.println("----------------- Event Data -----------------");
        int eventCount = 0;
        for (EventData data : events) {
            try {
                eventCount++;
                System.out.println("Event Info -> PartitionID: " + partitionContext.getPartitionId() +
                        ", Offset: " + data.getSystemProperties().getOffset());
                System.out.println("Event Payload: " + new String(data.getBytes(), StandardCharsets.UTF_8));
                this.checkpointBatchingCount++;
                if ((checkpointBatchingCount % 5) == 0) {
                    partitionContext.checkpoint(data).get();
                }
            } catch (Exception e) {
                System.out.println("Exception during process onEvent: " + e.getMessage());
            }
        }
        System.out.println("Batch Size : " + eventCount + " for owner: " + partitionContext.getOwner());
    }

    @Override
    public void onError(PartitionContext partitionContext, Throwable throwable) {
        System.out.println("Error onError: " + throwable.toString());
    }
}
