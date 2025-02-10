package storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import message.Message;
import org.springframework.stereotype.Component;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.WriteLock;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JsonFileMessageStorage implements MessageStorage {
    private static final String DATA_DIR = "data/messages";
    private static final String SNAPSHOT_DIR = "data/snapshots";
    private final ObjectMapper objectMapper;
    private final WriteLock writeLock = new ReentrantReadWriteLock().writeLock();
    
    public JsonFileMessageStorage() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        initDirectories();
    }
    
    private void initDirectories() {
        createDirectoryIfNotExists(DATA_DIR);
        createDirectoryIfNotExists(SNAPSHOT_DIR);
    }
    
    private void createDirectoryIfNotExists(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    @Override
    public void store(Message message) {
        String dateDir = getDateDirectory(message.getCreateTime());
        File messageFile = new File(dateDir, "messages.json");
        
        writeLock.lock();
        try {
            MessageBatch batch = readOrCreateBatch(messageFile);
            batch.getMessages().add(message);
            objectMapper.writeValue(messageFile, batch);
        } catch (Exception e) {
            log.error("Failed to store message", e);
        } finally {
            writeLock.unlock();
        }
    }
    
    @Override
    public void storeBatch(List<Message> messages) {
        Map<String, List<Message>> messagesByDate = messages.stream()
            .collect(Collectors.groupingBy(msg -> 
                getDateDirectory(msg.getCreateTime())));
            
        messagesByDate.forEach((dateDir, msgs) -> {
            File messageFile = new File(dateDir, "messages.json");
            writeLock.lock();
            try {
                MessageBatch batch = readOrCreateBatch(messageFile);
                batch.getMessages().addAll(msgs);
                objectMapper.writeValue(messageFile, batch);
            } catch (Exception e) {
                log.error("Failed to store message batch", e);
            } finally {
                writeLock.unlock();
            }
        });
    }
    
    private String getDateDirectory(LocalDateTime time) {
        return String.format("%s/%s", DATA_DIR, 
            time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }
    
    private MessageBatch readOrCreateBatch(File file) {
        if (file.exists()) {
            try {
                return objectMapper.readValue(file, MessageBatch.class);
            } catch (Exception e) {
                log.error("Failed to read message batch", e);
            }
        }
        return new MessageBatch();
    }
    
    public void createHourlySnapshot() {
        // 1. 获取当前时间作为快照标识
        LocalDateTime now = LocalDateTime.now();
        String snapshotFileName = String.format("%s/%s.json", 
            SNAPSHOT_DIR, 
            now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH")));

        // 2. 创建快照对象
        QueueSnapshot snapshot = new QueueSnapshot();
        snapshot.setSnapshotTime(now);

        writeLock.lock();
        try {
            // 3. 收集当前系统状态
            // 3.1 按状态分类消息
            Map<MessageStatus, List<Message>> messagesByStatus = 
                getAllMessages().stream()
                    .collect(Collectors.groupingBy(Message::getStatus));
            
            snapshot.setPendingMessages(messagesByStatus.getOrDefault(MessageStatus.PENDING, new ArrayList<>()));
            snapshot.setProcessingMessages(messagesByStatus.getOrDefault(MessageStatus.PROCESSING, new ArrayList<>()));
            snapshot.setCompletedMessages(messagesByStatus.getOrDefault(MessageStatus.COMPLETED, new ArrayList<>()));

            // 3.2 统计主题消息数量
            Map<String, Integer> topicStats = 
                getAllMessages().stream()
                    .collect(Collectors.groupingBy(
                        Message::getTopic,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                    ));
            snapshot.setTopicStats(topicStats);

            // 4. 写入快照文件
            File snapshotFile = new File(snapshotFileName);
            createDirectoryIfNotExists(snapshotFile.getParent());
            objectMapper.writeValue(snapshotFile, snapshot);

            // 5. 记录日志
            log.info("Created snapshot: {}", snapshotFileName);
            
        } catch (Exception e) {
            log.error("Failed to create snapshot", e);
        } finally {
            writeLock.unlock();
        }
    }

    public QueueSnapshot loadSnapshot(LocalDateTime pointInTime) {
        String snapshotFileName = String.format("%s/%s.json", 
            SNAPSHOT_DIR, 
            pointInTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH")));
        
        File snapshotFile = new File(snapshotFileName);
        if (!snapshotFile.exists()) {
            return null;
        }

        try {
            return objectMapper.readValue(snapshotFile, QueueSnapshot.class);
        } catch (Exception e) {
            log.error("Failed to load snapshot", e);
            return null;
        }
    }

    private List<Message> getAllMessages() {
        // 实现获取所有消息的逻辑
        // 这里需要根据实际的存储结构来实现
        // 可能需要遍历所有的消息文件并合并结果
        return new ArrayList<>(); // 临时返回空列表
    }
} 