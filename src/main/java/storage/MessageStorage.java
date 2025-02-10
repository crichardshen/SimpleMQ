package storage;

import message.Message;
import java.time.LocalDateTime;
import java.util.List;

public interface MessageStorage {
    /**
     * 存储消息
     * @param message 消息对象
     */
    void store(Message message);
    
    /**
     * 批量存储消息
     * @param messages 消息列表
     */
    void storeBatch(List<Message> messages);
    
    /**
     * 更新消息状态
     * @param messageId 消息ID
     * @param status 新状态
     */
    void updateStatus(String messageId, MessageStatus status);
    
    /**
     * 按时间范围查询消息
     * @param start 开始时间
     * @param end 结束时间
     */
    List<Message> queryByTimeRange(LocalDateTime start, LocalDateTime end);
    
    /**
     * 获取某个时间点的队列快照
     * @param pointInTime 时间点
     */
    QueueSnapshot getQueueSnapshot(LocalDateTime pointInTime);
    
    /**
     * 清理过期数据
     * @param before 此时间之前的数据将被清理
     */
    void cleanExpiredData(LocalDateTime before);
} 