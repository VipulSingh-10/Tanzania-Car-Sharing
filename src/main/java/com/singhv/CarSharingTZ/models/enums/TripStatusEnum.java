package com.singhv.CarSharingTZ.models.enums;

public enum TripStatusEnum {
    ACTIVE("ACTIVE"),
    CANCELLED("CANCELLED"),
    COMPLETED("COMPLETED"),
    FILLED("FILLED"),
    STARTED("STARTED");

    private final String status;

    TripStatusEnum(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    // For backward compatibility with code that uses the constants
    public static final String ACTIVE_STATUS = "ACTIVE";
    public static final String CANCELLED_STATUS = "CANCELLED";
    public static final String COMPLETED_STATUS = "COMPLETED";
    public static final String FILLED_STATUS = "FILLED";
    public static final String STARTED_STATUS = "STARTED";
}