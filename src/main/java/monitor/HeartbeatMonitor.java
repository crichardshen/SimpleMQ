package monitor;

import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class HeartbeatMonitor {
    // 存储每个节点的心跳统计信息
    private final Map<String, HeartbeatStats> stats = new ConcurrentHashMap<>();
    private static final Logger log = LoggerFactory.getLogger(HeartbeatMonitor.class);
    
    /**
     * 记录并分析心跳信息
     * @param peerId 对端节点ID
     * @param heartbeat 心跳信息
     */
    public void recordHeartbeat(String peerId, HeartbeatInfo heartbeat) {
        HeartbeatStats stat = stats.computeIfAbsent(peerId, k -> new HeartbeatStats());
        stat.recordHeartbeat();
        
        // 检查心跳延迟，如果延迟超过超时时间的一半，发出警告
        long delay = System.currentTimeMillis() - heartbeat.getTimestamp();
        if (delay > properties.getTimeout() / 2) {
            log.warn("High heartbeat delay detected: {}ms for peer {}", delay, peerId);
        }
    }
}

@Data
class HeartbeatStats {
    private long totalHeartbeats;    // 总心跳次数
    private long missedHeartbeats;   // 丢失的心跳次数
    private long lastHeartbeatTime;  // 最后一次心跳时间
    private long maxDelay;           // 最大延迟时间
} 