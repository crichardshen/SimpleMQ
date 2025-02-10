package dispatcher;

import message.Message;
import consumer.Consumer;
import org.springframework.web.socket.WebSocketSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.io.IOException;
import org.springframework.web.socket.TextMessage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import config.DispatcherProperties;
import org.springframework.stereotype.Component;

@Component
public class OrderedDispatcher implements Dispatcher {
    
    private final ConcurrentHashMap<String, List<Consumer>> topicConsumers;  // 按主题组织消费者
    private final ConcurrentHashMap<String, LinkedBlockingQueue<Message>> topicQueues;  // 每个主题一个队列
    private final ConcurrentHashMap<String, WebSocketSession> consumerSessions;
    private final ExecutorService deliveryExecutor;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DispatcherProperties properties;
    private int currentIndex = 0;  // 用于轮询
    
    public OrderedDispatcher(DispatcherProperties properties) {
        this.properties = properties;
        this.topicConsumers = new ConcurrentHashMap<>();
        this.topicQueues = new ConcurrentHashMap<>();
        this.consumerSessions = new ConcurrentHashMap<>();
        this.deliveryExecutor = Executors.newFixedThreadPool(10);
        startDeliveryThreads();
    }
    
    @Override
    public void dispatch(Message message) {
        // 当前的实现只是简单的消息分发
        // 没有实现消息确认机制
    }
    
    private void startDeliveryThreads() {
        deliveryExecutor.execute(() -> {
            while (true) {
                try {
                    // 遍历每个主题的队列
                    for (Map.Entry<String, LinkedBlockingQueue<Message>> entry : topicQueues.entrySet()) {
                        String topic = entry.getKey();
                        LinkedBlockingQueue<Message> queue = entry.getValue();
                        
                        if (!queue.isEmpty()) {
                            // 从队列中获取消息
                            Message message = queue.peek();  
                            
                            // 获取该主题的所有消费者
                            List<Consumer> consumers = topicConsumers.get(topic);
                            if (consumers != null && !consumers.isEmpty()) {
                                // 选择一个可用的消费者（按照指定的分发策略）
                                Consumer selectedConsumer = selectAvailableConsumer(consumers);
                                
                                // 是如果 consumer 不可用或者和其链接的 websocket 断了
                                // 程序会遍历下一个主题，完全遍历之后沉睡 1 毫秒，只要当该主题下有新的消费者，还是会发送成功的
                                // 相反，如果有可用的消费者
                                if (selectedConsumer != null) {
                                    WebSocketSession session = consumerSessions.get(selectedConsumer.getId());
                                    // 消费者的 websocket 也还在
                                    if (session != null && session.isOpen()) {
                                        try {
                                            // 发送消息
                                            // 文本类型消息
                                            // String messageJson = objectMapper.writeValueAsString(message);
                                            // session.sendMessage(new TextMessage(messageJson));
                                            
                                            //byte类型的消息
                                            session.sendMessage(new BinaryMessage(message.getBody()));
                                            
                                            // 发送成功后才从队列中移除
                                            queue.poll();
                                            // 增加消息计数
                                            selectedConsumer.incrementMessageCount();  
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                            }
                        }

                    }
                    Thread.sleep(100);  // 避免空转
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    private Consumer selectAvailableConsumer(List<Consumer> consumers) {
        switch (properties.getConsumerSelectStrategy()) {
            case FIRST_AVAILABLE:
                return selectFirstAvailable(consumers);
            case ROUND_ROBIN:
                return selectByRoundRobin(consumers);
            case PRIORITY:
                return selectByPriority(consumers);
            case LOAD_BALANCE:
                return selectByLoadBalance(consumers);
            default:
                return selectFirstAvailable(consumers);
        }
    }
    
    // 策略1：第一个可用
    private Consumer selectFirstAvailable(List<Consumer> consumers) {
        for (Consumer consumer : consumers) {
            WebSocketSession session = consumerSessions.get(consumer.getId());
            if (session != null && session.isOpen()) {
                return consumer;
            }
        }
        return null;
    }
    
    // 策略2：轮询
    private Consumer selectByRoundRobin(List<Consumer> consumers) {
        if (consumers.isEmpty()) return null;
        
        for (int i = 0; i < consumers.size(); i++) {
            int index = (currentIndex + i) % consumers.size();
            Consumer consumer = consumers.get(index);
            WebSocketSession session = consumerSessions.get(consumer.getId());
            
            if (session != null && session.isOpen()) {
                currentIndex = (index + 1) % consumers.size();
                return consumer;
            }
        }
        return null;
    }
    
    // 策略3：优先级
    private Consumer selectByPriority(List<Consumer> consumers) {
        return consumers.stream()
            .sorted((c1, c2) -> Integer.compare(c2.getPriority(), c1.getPriority()))
            .filter(consumer -> {
                WebSocketSession session = consumerSessions.get(consumer.getId());
                return session != null && session.isOpen();
            })
            .findFirst()
            .orElse(null);
    }
    
    // 策略4：负载均衡
    private Consumer selectByLoadBalance(List<Consumer> consumers) {
        return consumers.stream()
            .filter(consumer -> {
                WebSocketSession session = consumerSessions.get(consumer.getId());
                return session != null && session.isOpen();
            })
            .min((c1, c2) -> Integer.compare(
                getConsumerLoad(c1), 
                getConsumerLoad(c2)
            ))
            .orElse(null);
    }
    
    // 获取消费者负载
    private int getConsumerLoad(Consumer consumer) {
        // 这里可以实现具体的负载计算逻辑
        // 例如：处理消息数、响应时间等
        return consumer.getMessageCount();  // 需要在Consumer类中添加此方法
    }
    
    @Override
    public void registerConsumer(Consumer consumer) {
        // 将消费者添加到对应主题的消费者列表
        topicConsumers.computeIfAbsent(consumer.getTopic(), k -> new CopyOnWriteArrayList<>())
                     .add(consumer);
        
        if (consumer.getSession() != null) {
            consumerSessions.put(consumer.getId(), consumer.getSession());
        }
    }
    
    @Override
    public void unregisterConsumer(Consumer consumer) {
        // 移除消费者的消息队列
        topicConsumers.remove(consumer.getTopic());
        // 移除WebSocket会话
        WebSocketSession session = consumerSessions.remove(consumer.getId());
        if (session != null && session.isOpen()) {
            try {
                session.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
} 

