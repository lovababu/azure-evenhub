package org.avol.azure.eh.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Equipment {
    private String id;
    private int engineSpeed;
    private float fuelLevel;
    private long runningTime;
}
