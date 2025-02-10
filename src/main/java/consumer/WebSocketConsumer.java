public class WebSocketConsumer implements Consumer {
    private final String topic;
    private final String consumerId;
    private final WebSocketSession session;
    private int priority = 0;
    private final AtomicInteger messageCount = new AtomicInteger(0);  // 添加消息计数器
    
    public WebSocketConsumer(String topic, String consumerId, WebSocketSession session) {
        this.topic = topic;
        this.consumerId = consumerId;
        this.session = session;
    }
    
    @Override
    public void onMessage(Message message) {
        // 处理消息
        messageCount.incrementAndGet();  // 增加消息计数
    }
    
    @Override
    public String getTopic() {
        return topic;
    }
    
    @Override
    public String getConsumerId() {
        return consumerId;
    }
    
    @Override
    public int getPriority() {
        return priority;
    }
    
    @Override
    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    @Override
    public int getMessageCount() {
        return messageCount.get();
    }
    
    @Override
    public void incrementMessageCount() {
        messageCount.incrementAndGet();
    }
    
    public WebSocketSession getSession() {
        return session;
    }
} 