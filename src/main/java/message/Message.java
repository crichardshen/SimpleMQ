package message;

import java.io.Serializable;
import java.util.UUID;
import java.time.Instant;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Message implements Serializable {
    private final String messageId;
    private final String topic;
    private final byte[] payload;
    private final Instant timestamp;
    private MessageStatus status = MessageStatus.PENDING;
    
    @NotNull
    private MessageHeader header;        // 消息头
    
    @NotNull
    private byte[] body;      // 改为 byte[] 类型，可以支持任何格式的消息
    
    @NotNull
    private ProducerInfo producerInfo;   // 生产者信息
    
    public Message(String topic, byte[] payload) {
        this.messageId = UUID.randomUUID().toString();
        this.topic = topic;
        this.payload = payload;
        this.timestamp = Instant.now();
    }
    
    // Getters
    public byte[] getPayload() { return payload; }
    public Instant getTimestamp() { return timestamp; }
    public MessageStatus getStatus() { return status; }
    
    // Status management
    public void setStatus(MessageStatus status) {
        this.status = status;
    }
    
    // 便捷方法：获取主题
    @JsonIgnore
    public String getTopic() {
        return header.getTopic();
    }
    
    // 便捷方法：获取消息ID
    @JsonIgnore
    public String getMessageId() {
        return header.getMessageId();
    }
    
    // 添加一个便捷方法来获取消息长度
    @JsonIgnore
    public int getBodyLength() {
        return body != null ? body.length : 0;
    }
    
    // 修改验证方法
    @JsonIgnore
    public boolean isValid(int maxLength) {
        return body != null && body.length <= maxLength;
    }
} 