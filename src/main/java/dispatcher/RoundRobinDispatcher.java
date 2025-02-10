package dispatcher;

import message.Message;
import consumer.Consumer;
import core.Dispatcher;
import message.MessageStatus;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RoundRobinDispatcher implements Dispatcher {
    private final Map<String, Queue<Consumer>> topicConsumers = new ConcurrentHashMap<>();
    
    @Override
    public void dispatch(Message message, List<Consumer> consumers) {
        if (consumers.isEmpty()) {
            return;
        }
        
        Queue<Consumer> queue = topicConsumers.get(message.getTopic());
        if (queue != null && !queue.isEmpty()) {
            Consumer consumer = queue.poll();
            message.setStatus(MessageStatus.DELIVERING);
            consumer.onMessage(message);
            queue.offer(consumer); // 重新加入队列
        }
    }
    
    @Override
    public void registerConsumer(Consumer consumer) {
        topicConsumers.computeIfAbsent(consumer.getTopic(), k -> new ConcurrentLinkedQueue<>())
                      .offer(consumer);
    }
    
    @Override
    public void unregisterConsumer(Consumer consumer) {
        Queue<Consumer> queue = topicConsumers.get(consumer.getTopic());
        if (queue != null) {
            queue.remove(consumer);
        }
    }

    @Override
    public void dispatch(Message message) {

    }
} 