package websocket;

import org.springframework.web.socket.*;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import message.Message;
import broker.SimpleMQBroker;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import model.ConsumerInfo;
import model.ProducerInfo;

@Component
public class MessageWebSocketHandler implements WebSocketHandler {
    
    private final SimpleMQBroker broker;
    private final ObjectMapper objectMapper;
    private final Map<WebSocketSession, ConsumerInfo> consumerSessions = new ConcurrentHashMap<>();
    private final Map<WebSocketSession, ProducerInfo> producerSessions = new ConcurrentHashMap<>();
    
    public MessageWebSocketHandler(SimpleMQBroker broker) {
        this.broker = broker;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String token = getTokenFromSession(session);
        String role = getRoleFromToken(token);
        
        if ("producer".equals(role)) {
            ProducerInfo producerInfo = validateProducerToken(token);
            if (producerInfo != null) {
                producerSessions.put(session, producerInfo);
            }
        } else if ("consumer".equals(role)) {
            ConsumerInfo consumerInfo = validateConsumerToken(token);
            if (consumerInfo != null) {
                consumerSessions.put(session, consumerInfo);
            }
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        try {
            // 检查是否是生产者会话
            if (producerSessions.containsKey(session)) {
                if (message instanceof TextMessage) {
                    // 处理 JSON 格式的消息
                    Message mqMessage = objectMapper.readValue(
                        message.getPayload().toString(), 
                        Message.class
                    );
                    broker.publish(mqMessage);
                } 
                else if (message instanceof BinaryMessage) {
                    // 处理二进制消息
                    BinaryMessage binaryMessage = (BinaryMessage) message;
                    byte[] data = binaryMessage.getPayload().array();
                    
                    // 从 session 属性中获取主题信息
                    String topic = (String) session.getAttributes().get("topic");
                    
                    Message mqMessage = new Message();
                    mqMessage.getHeader().setTopic(topic);
                    mqMessage.setBody(data);
                    
                    broker.publish(mqMessage);
                }
            }
            // 消费者会话不处理消息
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {

    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private String getTokenFromSession(WebSocketSession session) {
        String query = session.getUri().getQuery();
        // 从query string中解析token
        return parseToken(query);
    }

    private String getRoleFromToken(String token) {
        // Implementation of getRoleFromToken method
        return "consumer"; // Placeholder return, actual implementation needed
    }

    private ProducerInfo validateProducerToken(String token) {
        // Implementation of validateProducerToken method
        return null; // Placeholder return, actual implementation needed
    }

    private ConsumerInfo validateConsumerToken(String token) {
        // Implementation of validateConsumerToken method
        return null; // Placeholder return, actual implementation needed
    }

    // 其他必要的WebSocketHandler方法实现...
} 