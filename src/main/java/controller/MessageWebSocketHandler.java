package com.SimpleMQ.Controller;

import com.SimpleMQ.Session.WebSocketSessionManager;
import com.SimpleMQ.Broker.MQBroker;
import com.SimpleMQ.Message.RawMessageProtocolType;
import com.SimpleMQ.Message.RawMessage;
import org.springframework.web.socket.*;

import java.util.UUID;

//@Slf4j
//@Component
public class MessageWebSocketHandler implements WebSocketHandler {
    private final MQBroker messageHub;
    private final WebSocketSessionManager sessionManager;

    public MessageWebSocketHandler(MQBroker messageHub, WebSocketSessionManager sessionManager) {
        this.messageHub = messageHub;
        this.sessionManager = sessionManager;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String clientId = (String) session.getAttributes().get("clientId");
        String sessionId = UUID.randomUUID().toString();

        sessionManager.addSession(sessionId, session);
        System.out.println("WebSocket connection established: clientId={}, sessionId" + clientId + sessionId);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String clientId = (String) session.getAttributes().get("clientId");
        String sessionId = sessionManager.getSessionId(session);

        if (message instanceof BinaryMessage) {
            BinaryMessage binaryMessage = (BinaryMessage) message;
            RawMessage rawMessage = new RawMessage();
            rawMessage.setClientID(clientId);
            rawMessage.setRawMessageProtocolType(RawMessageProtocolType.WEBSOCKET);
            rawMessage.setRequestData(binaryMessage.getPayload().array());
            rawMessage.setTimestamp(System.currentTimeMillis());
            rawMessage.setSessionID(sessionId);
            messageHub.AddToRequestMessageQueue(rawMessage);
            System.out.println("DEBUG : Received WebSocket binary Message from Client: "+clientId);
        } else if (message instanceof TextMessage) {
            // 文本消息也转为二进制处理
            TextMessage textMessage = (TextMessage) message;
            RawMessage rawMessage = new RawMessage();
            rawMessage.setClientID(clientId);
            rawMessage.setRawMessageProtocolType(RawMessageProtocolType.WEBSOCKET);
            rawMessage.setRequestData(textMessage.asBytes());
            rawMessage.setTimestamp(System.currentTimeMillis());
            rawMessage.setSessionID(sessionId);
            messageHub.AddToRequestMessageQueue(rawMessage);
            System.out.println("Received WebSocket text Message from Client: " + clientId);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.out.println("ERRPR : WebSocket transport error: "
                + session.getId() + exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        String sessionId = sessionManager.getSessionId(session);
        sessionManager.removeSession(sessionId);
        System.out.println("INFOR : WebSocket connection closed: "+sessionId+", status="+closeStatus);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
