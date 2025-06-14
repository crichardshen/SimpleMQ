package com.SimpleMQ.Sender;

import com.SimpleMQ.Session.WebSocketSessionManager;
import com.SimpleMQ.Message.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

//@Component
public class WebSocketResponseSender implements ResponseSender {
    private final WebSocketSessionManager sessionManager;

    //@Autowired
    public WebSocketResponseSender(WebSocketSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void send(ResponseMessage response) {
        WebSocketSession session = sessionManager.getSession(response.getSessionID());
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(new String(response.getResponseMessage())));
            } catch (IOException e) {
                System.out.println("Failed to send WebSocket Message to session: " + response.getSessionID());
                sessionManager.removeSession(response.getSessionID());
            }
        } else {
            System.out.println("WebSocket session not found or closed: {}" + response.getSessionID());
        }
    }
}
