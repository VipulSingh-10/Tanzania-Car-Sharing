package com.singhv.CarSharingTZ.models;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Points {
    private double latitude;
    private double longitude;
    private String placeId = "";
    private String placeAddress = null;
}
