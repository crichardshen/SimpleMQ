package message;

import java.time.LocalDateTime;

public class MessageHeader {
    private String messageId;        // 消息唯一标识
    private String topic;            // 消息主题
    private LocalDateTime timestamp; // 消息生成时间
    private int priority;           // 消息优先级
    private String contentType;     // 消息内容类型（如：text/plain, application/json）
    
    public MessageHeader() {
        this.timestamp = LocalDateTime.now();
    }
    
    // getters and setters
} 