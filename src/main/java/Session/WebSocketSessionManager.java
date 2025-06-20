package com.SimpleMQ.Session;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//@Component
public class WebSocketSessionManager {
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<WebSocketSession, String> sessionIds = new ConcurrentHashMap<>();
    private final Map<String, String> clientToSession = new ConcurrentHashMap<>();

    public void addSession(String sessionId, WebSocketSession session) {
        sessions.put(sessionId, session);
        sessionIds.put(session, sessionId);
    }

    public void removeSession(String sessionId) {
        WebSocketSession session = sessions.remove(sessionId);
        if (session != null) {
            sessionIds.remove(session);
            clientToSession.values().remove(sessionId);
        }
    }

    public WebSocketSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    public String getSessionId(WebSocketSession session) {
        return sessionIds.get(session);
    }

    public void registerClientId(String sessionId, String clientId) {
        clientToSession.put(clientId, sessionId);
    }

    public String getSessionIdByClient(String clientId) {
        return clientToSession.get(clientId);
    }
}
