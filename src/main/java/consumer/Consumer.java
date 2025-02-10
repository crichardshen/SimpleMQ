package consumer;

import message.Message;
import java.util.concurrent.atomic.AtomicInteger;

public interface Consumer {
    void onMessage(Message message);
    String getTopic();
    String getConsumerId();
    int getPriority();
    void setPriority(int priority);
    int getMessageCount();
    void incrementMessageCount();
} 