package heartbeat;

import java.io.Serializable;
import java.util.Map;
import lombok.Data;

@Data
public class HeartbeatInfo implements Serializable {
    private String serverId;        // 服务器标识(1a/2a)
    private long timestamp;         // 心跳发送时间
    private long activeTime;        // broker激活时间，替换原来的startTime
    private ServerRole role;        // 当前角色
    private ServerStatus status;    // 服务器状态
    private Map<String, Integer> metrics;  // 服务器指标
    
    public HeartbeatInfo(String serverId, long timestamp, long activeTime,
                        ServerRole role, ServerStatus status, 
                        Map<String, Integer> metrics) {
        this.serverId = serverId;
        this.timestamp = timestamp;
        this.activeTime = activeTime;
        this.role = role;
        this.status = status;
        this.metrics = metrics;
    }
} 