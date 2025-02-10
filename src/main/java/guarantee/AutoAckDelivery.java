package guarantee;

import core.DeliveryGuarantee;
import message.Message;

public class AutoAckDelivery implements DeliveryGuarantee {
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
    // 自动确认实现
} 