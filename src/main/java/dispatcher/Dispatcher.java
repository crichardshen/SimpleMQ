package dispatcher;

import message.Message;
import consumer.Consumer;
import java.util.List;

public interface Dispatcher {
    /**
     * 分发消息给指定的消费者列表
     * @param message 要分发的消息
     * @param consumers 消费者列表
     */
    void dispatch(Message message, List<Consumer> consumers);
    
    /**
     * 注册新的消费者
     * @param consumer 要注册的消费者
     */
    void registerConsumer(Consumer consumer);
    
    /**
     * 注销消费者
     * @param consumer 要注销的消费者
     */
    void unregisterConsumer(Consumer consumer);
} 