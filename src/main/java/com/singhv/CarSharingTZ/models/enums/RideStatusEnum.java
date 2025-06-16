package com.singhv.CarSharingTZ.models.enums;

public enum RideStatusEnum {
    ALLOTTED("ALLOTTED"),
    CANCELLED("CANCELLED"),
    COMPLETED("COMPLETED"),
    REQUESTED("REQUESTED");

    private final String status;

    RideStatusEnum(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    // For backward compatibility with code that uses the constants
    public static final String ALLOTTED_STATUS = "ALLOTTED";
    public static final String CANCELLED_STATUS = "CANCELLED";
    public static final String COMPLETED_STATUS = "COMPLETED";
    public static final String REQUESTED_STATUS = "REQUESTED";
}