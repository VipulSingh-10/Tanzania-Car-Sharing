package com.singhv.CarSharingTZ.controller;

import com.singhv.CarSharingTZ.dto.WebSocketMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/ride.join")
    @SendTo("/topic/rides")
    public WebSocketMessage joinRide(@Payload WebSocketMessage message,
                                     SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("userId", message.getSenderId());
        headerAccessor.getSessionAttributes().put("tripId", message.getTripId());
        return message;
    }

    @MessageMapping("/ride.status")
    @SendTo("/topic/rides")
    public WebSocketMessage updateRideStatus(@Payload WebSocketMessage message) {
        return message;
    }
}