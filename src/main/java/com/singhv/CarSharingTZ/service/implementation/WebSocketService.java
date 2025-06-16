package com.singhv.CarSharingTZ.service.implementation;

import com.singhv.CarSharingTZ.dto.WebSocketMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class WebSocketService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void notifyRideUpdate(String tripId, String updateType, String content) {
        WebSocketMessage message = new WebSocketMessage();
        message.setType(updateType);
        message.setContent(content);
        message.setTripId(tripId);
        message.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));

        messagingTemplate.convertAndSend("/topic/trip." + tripId, message);
    }
}