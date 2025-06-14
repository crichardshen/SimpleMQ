package com.SimpleMQ.Session;

import com.SimpleMQ.Entity.MQSocket;
import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SocketSessionManager {

    @Getter
    private final Map<String, MQSocket> sockets = new ConcurrentHashMap<>();
    private final Map<MQSocket, String> socketIds = new ConcurrentHashMap<>();
    private final Map<String, String> clientToSocket = new ConcurrentHashMap<>();

    public void addSocket(String socketId, MQSocket socket) {
        sockets.put(socketId, socket);
        socketIds.put(socket, socketId);
    }

    public void removeSocket(String socketId) {
        MQSocket socket = sockets.remove(socketId);
        if (socket != null) {
            socketIds.remove(socket);
            clientToSocket.values().remove(socketId);
        }
    }

    public MQSocket getSocket(String socketId) {
        return sockets.get(socketId);
    }

    public void registerClientId(String socketId, String clientId) {
        clientToSocket.put(clientId, socketId);
    }

    public String getSocketIdByClient(String clientId) {
        return clientToSocket.get(clientId);
    }
}
