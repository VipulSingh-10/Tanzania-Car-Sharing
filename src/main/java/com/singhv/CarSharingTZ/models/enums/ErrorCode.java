package com.singhv.CarSharingTZ.models.enums;

public enum ErrorCode {
    UNSPECIFIED("Sorry, Something Went Wrong!"),
    NO_RIDE_AVAILABLE("Sorry, Right now there are no commuters available!");

    private final String errorMessage;

    ErrorCode(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}