package com.SimpleMQ.Handler;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class MyWebSocketHandler extends TextWebSocketHandler {

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("服务端收到: " + message.getPayload());
        session.sendMessage(new TextMessage("ECHO: " + message.getPayload()));
    }
}
