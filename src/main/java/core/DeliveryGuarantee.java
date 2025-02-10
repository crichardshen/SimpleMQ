package core;
import message.Message;

public interface DeliveryGuarantee {
    void acknowledge(String messageId);
    void retry(Message message);
    boolean isDelivered(String messageId);
} 