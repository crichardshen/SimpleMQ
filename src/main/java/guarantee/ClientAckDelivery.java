package guarantee;

import core.DeliveryGuarantee;
import message.Message;

import java.util.Map;

public class ClientAckDelivery<DeliveryStatus> implements DeliveryGuarantee {
    private final Map<String, DeliveryStatus> messageStatus;

    public ClientAckDelivery(Map<String, DeliveryStatus> messageStatus) {
        this.messageStatus = messageStatus;
    }

    @Override
    public void acknowledge(String messageId) {

    }

    @Override
    public void retry(Message message) {

    }

    @Override
    public boolean isDelivered(String messageId) {
        return false;
    }

    // 客户端确认实现
} 