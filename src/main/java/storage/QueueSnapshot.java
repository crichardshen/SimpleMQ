package storage;

import message.Message;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class QueueSnapshot {
    private LocalDateTime snapshotTime;
    private List<Message> pendingMessages;    // 待处理的消息
    private List<Message> processingMessages; // 处理中的消息
    private List<Message> completedMessages;  // 已完成的消息
    private Map<String, Integer> topicStats;  // 各主题的消息统计
} 