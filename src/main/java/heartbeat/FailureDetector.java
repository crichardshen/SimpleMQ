package heartbeat;

import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Component
public class FailureDetector {
    private static final int FAILURE_THRESHOLD = 3;
    private final Map<String, HeartbeatRecord> heartbeatRecords = new ConcurrentHashMap<>();
    @Autowired
    private SimpleMQBroker broker;
    @Autowired
    private HeartbeatSender heartbeatSender;
    private ServerRole currentRole;
    @Autowired
    private HeartbeatProperties properties;
    
    public void handleHeartbeatTimeout(String peerId) {
        HeartbeatRecord record = heartbeatRecords.get(peerId);
        if (record != null) {
            record.missedCount++;
            log.warn("Missed heartbeat from {}, count: {}", peerId, record.missedCount);
            
            if (record.missedCount >= FAILURE_THRESHOLD) {
                if (currentRole == ServerRole.SLAVE) {
                    log.info("Master node {} appears to be down, initiating takeover", peerId);
                    triggerTakeover();
                }
            }
        }
    }
    
    public void handleHeartbeatReceived(String peerId) {
        HeartbeatRecord record = heartbeatRecords.computeIfAbsent(
            peerId,
            k -> new HeartbeatRecord()
        );
        record.missedCount = 0;
        record.lastReceiveTime = System.currentTimeMillis();
    }
    
    private void triggerTakeover() {
        log.info("Starting takeover process...");
        
        try {
            // 1. 切换角色
            currentRole = ServerRole.MASTER;
            heartbeatSender.updateRole(ServerRole.MASTER);
            
            // 2. 激活 broker
            broker.activate();  // 需要在 SimpleMQBroker 中添加此方法
            
            log.info("Takeover completed successfully");
        } catch (Exception e) {
            log.error("Takeover failed", e);
            // 切换失败，恢复到从节点状态
            currentRole = ServerRole.SLAVE;
            heartbeatSender.updateRole(ServerRole.SLAVE);
        }
    }
    
    public void handleMasterHeartbeat(String peerId) {
        if (currentRole == ServerRole.MASTER) {
            // 如果我们也是主节点，需要通过某种机制决定谁该是主节点
            if (shouldYieldToMaster(peerId)) {
                log.info("Yielding master role to {}", peerId);
                currentRole = ServerRole.SLAVE;
                heartbeatSender.updateRole(ServerRole.SLAVE);
                broker.deactivate();
            }
        }
    }
    
    private boolean shouldYieldToMaster(String peerId) {
        HeartbeatRecord peerRecord = heartbeatRecords.get(peerId);
        if (peerRecord == null) return false;
        
        HeartbeatInfo peerInfo = peerRecord.getLastHeartbeat();
        
        // 1. 优先考虑运行时间更长的节点（使用activeTime替代startTime）
        if (peerInfo.getActiveTime() < broker.getActiveTime()) {
            return true;
        }
        
        // 2. 如果运行时间相近，考虑负载情况
        Map<String, Integer> peerMetrics = peerInfo.getMetrics();
        if (peerMetrics.get("cpu") < getCurrentCpuUsage()) {
            return true;
        }
        
        // 3. 最后才使用ID比较
        return peerId.compareTo(properties.getServerId()) < 0;
    }
    
    @Data
    class HeartbeatRecord {
        private long lastReceiveTime;
        private int missedCount;
    }
} 