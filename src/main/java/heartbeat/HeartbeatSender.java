package heartbeat;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class HeartbeatSender {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final DatagramSocket socket;
    private final InetAddress peerAddress;
    private final int peerPort;
    private final String serverId;
    private final ObjectMapper objectMapper;
    private ServerRole currentRole;
    
    @Autowired
    private SimpleMQBroker broker;  // 添加broker引用
    
    public HeartbeatSender(HeartbeatProperties properties) throws Exception {
        this.socket = new DatagramSocket();
        this.peerAddress = InetAddress.getByName(properties.getPeerAddress());
        this.peerPort = properties.getPort();
        this.serverId = properties.getServerId();
        this.objectMapper = new ObjectMapper();
        this.currentRole = properties.getInitialRole();
    }
    
    public void start() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                HeartbeatInfo heartbeat = new HeartbeatInfo(
                    serverId,
                    System.currentTimeMillis(),
                    broker.getActiveTime(),  // 使用broker的激活时间
                    currentRole,
                    ServerStatus.RUNNING,
                    collectMetrics()
                );
                
                byte[] data = objectMapper.writeValueAsBytes(heartbeat);
                DatagramPacket packet = new DatagramPacket(
                    data, data.length, peerAddress, peerPort
                );
                socket.send(packet);
                log.debug("Sent heartbeat to peer: {}", heartbeat);
            } catch (Exception e) {
                log.error("Failed to send heartbeat", e);
            }
        }, 0, 5, TimeUnit.SECONDS);
    }
    
    private Map<String, Integer> collectMetrics() {
        // 这里可以添加实际的指标收集逻辑
        Map<String, Integer> metrics = new HashMap<>();
        metrics.put("cpu", 50);  // 示例数据
        metrics.put("memory", 70);
        return metrics;
    }
    
    public void updateRole(ServerRole newRole) {
        this.currentRole = newRole;
    }
    
    public void shutdown() {
        scheduler.shutdown();
        socket.close();
    }
} 