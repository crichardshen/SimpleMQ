package heartbeat;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class HeartbeatReceiver {
    private final DatagramSocket socket;
    private final Map<String, HeartbeatRecord> heartbeatRecords = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final ObjectMapper objectMapper;
    private final FailureDetector failureDetector;
    private final long heartbeatTimeout;
    
    @Data
    class HeartbeatRecord {
        private HeartbeatInfo lastHeartbeat;
        private long lastReceiveTime;
        private int missedCount;
    }
    
    public HeartbeatReceiver(HeartbeatProperties properties, FailureDetector failureDetector) throws Exception {
        this.socket = new DatagramSocket(properties.getPort());
        this.objectMapper = new ObjectMapper();
        this.failureDetector = failureDetector;
        this.heartbeatTimeout = properties.getTimeout();
    }
    
    public void start() {
        // 启动接收线程
        new Thread(() -> {
            while (!socket.isClosed()) {
                try {
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    
                    byte[] data = new byte[packet.getLength()];
                    System.arraycopy(packet.getData(), 0, data, 0, packet.getLength());
                    
                    HeartbeatInfo heartbeat = objectMapper.readValue(data, HeartbeatInfo.class);
                    updateHeartbeatRecord(heartbeat);
                    log.debug("Received heartbeat from {}: {}", heartbeat.getServerId(), heartbeat);
                } catch (Exception e) {
                    log.error("Failed to receive heartbeat", e);
                }
            }
        }).start();
        
        // 启动检查线程
        startHealthCheck();
    }
    
    private void updateHeartbeatRecord(HeartbeatInfo heartbeat) {
        HeartbeatRecord record = heartbeatRecords.computeIfAbsent(
            heartbeat.getServerId(),
            k -> new HeartbeatRecord()
        );
        record.setLastHeartbeat(heartbeat);
        record.setLastReceiveTime(System.currentTimeMillis());
        record.setMissedCount(0);
        
        // 检查对方的角色
        if (heartbeat.getRole() == ServerRole.MASTER) {
            // 如果对方是主节点，自己就要降级为从节点
            failureDetector.handleMasterHeartbeat(heartbeat.getServerId());
        }
    }
    
    private void startHealthCheck() {
        scheduler.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            heartbeatRecords.forEach((serverId, record) -> {
                if (now - record.getLastReceiveTime() > heartbeatTimeout) {
                    failureDetector.handleHeartbeatTimeout(serverId);
                }
            });
        }, 0, 1, TimeUnit.SECONDS);
    }
    
    public void shutdown() {
        scheduler.shutdown();
        socket.close();
    }
} 