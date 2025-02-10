package broker;

import core.Storage;
import core.Dispatcher;
import message.Message;
import consumer.Consumer;
import java.util.*;
import java.util.concurrent.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import core.MessageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleMQBroker {
    private static final Logger log = LoggerFactory.getLogger(SimpleMQBroker.class);
    private final Storage storage;
    private final Dispatcher dispatcher;
    private final MessageProcessor messageProcessor;
    //the object to control the thread pool to dispatch messages
    private final ExecutorService executorService;
    //the object to control the topic and consumers
    private final Map<String, List<Consumer>> topicConsumers;
    //for the message dispatch  
    private final ExecutorService dispatcherExecutor = Executors.newSingleThreadExecutor();
    //for the message processing
    private final ExecutorService workExecutor = new ThreadPoolExecutor(
        20,                 // 核心线程数
        50,                 // 最大线程数
        60L,               // 空闲线程存活时间
        TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(1000),  // 工作队列
        new ThreadPoolExecutor.CallerRunsPolicy()  // 拒绝策略
    );
    //for the message persistence
    private final ExecutorService persistenceExecutor = Executors.newSingleThreadExecutor();
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    private final int BATCH_SIZE = 100;
    private final BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>(1000);
    
    private volatile boolean active = false;
    private long activeTime;  // 添加激活时间字段
    
    public SimpleMQBroker(Storage storage, Dispatcher dispatcher, MessageProcessor messageProcessor) {
        this.storage = storage;
        this.dispatcher = dispatcher;
        this.messageProcessor = messageProcessor;
        this.executorService = Executors.newFixedThreadPool(10);
        this.topicConsumers = new ConcurrentHashMap<>();
    }
    
    public void activate() {
        active = true;
        activeTime = System.currentTimeMillis();  // 记录激活时间
        log.info("Broker activated as master at {}", activeTime);
    }
    
    public void deactivate() {
        active = false;
        activeTime = 0;  // 重置激活时间
        log.info("Broker deactivated to slave");
    }
    
    public long getActiveTime() {
        return activeTime;
    }
    
    @Override
    public void publish(Message message) {
        // 只有激活状态才处理消息
        if (!active) {
            log.warn("Message rejected - broker is not active");
            return;
        }
        
        executorService.submit(() -> {
            storage.store(message);
            // 通过 WebSocket 推送消息给所有订阅这个主题的消费者<-这个行为是广播主题
            //messagingTemplate.convertAndSend("/topic/" + message.getTopic(), message);
            
            List<Consumer> consumers = topicConsumers.getOrDefault(
                message.getTopic(), Collections.emptyList());
            dispatcher.dispatch(message, consumers);
        });
    }
    
    public void subscribe(Consumer consumer) {
        topicConsumers.computeIfAbsent(consumer.getTopic(), k -> new CopyOnWriteArrayList<>())
                      .add(consumer);
        dispatcher.registerConsumer(consumer);
    }
    
    public void unsubscribe(Consumer consumer) {
        List<Consumer> consumers = topicConsumers.get(consumer.getTopic());
        if (consumers != null) {
            consumers.remove(consumer);
            dispatcher.unregisterConsumer(consumer);
        }
    }
    
    public void shutdown() {
        executorService.shutdown();
    }
    
    public void dispatch(Message message) {
        // 使用单线程确保消息顺序
        dispatcherExecutor.execute(() -> {
            // 消息分发逻辑
            dispatcher.dispatch(message);
        });
    }
    
    public void processMessage(Message message) {
        // 使用工作线程池处理具体的消息处理任务
        workExecutor.execute(() -> {
            messageProcessor.process(message);
        });
    }
    
    public void persistMessage(Message message) {
        // 使用独立线程处理持久化
        persistenceExecutor.execute(() -> {
            storage.store(message);
        });
    }
    
    private void processBatch() {
        List<Message> batch = new ArrayList<>(BATCH_SIZE);
        messageQueue.drainTo(batch, BATCH_SIZE);
        if (!batch.isEmpty()) {
            storage.storeBatch(batch);
        }
    }
} 