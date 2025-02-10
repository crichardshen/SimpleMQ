package service;

import consumer.Consumer;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import broker.SimpleMQBroker;
import message.Message;

@Service
public class MessageService {
    
    @Autowired
    private SimpleMQBroker broker;
    
    public void publishMessage(Message message) {
        broker.publish(message);
    }
    
    public void subscribeToTopic(String topic, Consumer consumer) {
        broker.subscribe(consumer);
    }
    
    public void unsubscribeFromTopic(String topic, Consumer consumer) {
        broker.unsubscribe(consumer);
    }
} 