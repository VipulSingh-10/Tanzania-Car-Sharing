package com.singhv.CarSharingTZ.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessage {
    private String type;
    private String content;
    private String senderId;
    private String recipientId;
    private String tripId;
    private String timestamp;
}