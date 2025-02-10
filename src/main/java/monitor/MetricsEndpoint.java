package monitor;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/metrics")
public class MetricsEndpoint {
    private final NodeStatusMonitor nodeStatusMonitor;
    private final HeartbeatMonitor heartbeatMonitor;
    private final SwitchEventMonitor switchEventMonitor;

    public MetricsEndpoint(NodeStatusMonitor nodeStatusMonitor, HeartbeatMonitor heartbeatMonitor, SwitchEventMonitor switchEventMonitor) {
        this.nodeStatusMonitor = nodeStatusMonitor;
        this.heartbeatMonitor = heartbeatMonitor;
        this.switchEventMonitor = switchEventMonitor;
    }

    /**
     * 获取当前节点状态
     * @return 节点状态信息
     */
    @GetMapping("/node/status")
    public NodeStatus getNodeStatus() {
        return nodeStatusMonitor.getCurrentStatus();
    }
    
    /**
     * 获取心跳统计信息
     * @return 所有节点的心跳统计
     */
    @GetMapping("/heartbeat/stats")
    public Map<String, HeartbeatStats> getHeartbeatStats() {
        return heartbeatMonitor.getStats();
    }
    
    /**
     * 获取角色切换历史
     * @return 切换事件列表
     */
    @GetMapping("/switch/history")
    public List<SwitchEvent> getSwitchHistory() {
        return switchEventMonitor.getHistory();
    }
} 