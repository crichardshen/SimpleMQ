package core;
import message.Message;
import consumer.Consumer;

import java.util.List;

public interface Dispatcher {
    void dispatch(Message message, List<Consumer> consumers);
    void registerConsumer(Consumer consumer);
    void unregisterConsumer(Consumer consumer);
    void dispatch(Message message);
}