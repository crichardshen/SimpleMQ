package monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NodeStatusMonitor {
    private static final Logger log = LoggerFactory.getLogger(NodeStatusMonitor.class);
    
    @Autowired
    private SimpleMQBroker broker;
    
    public NodeStatus getCurrentStatus() {
        return new NodeStatus(
            properties.getServerId(),
            currentRole,
            broker.isActive(),
            broker.getActiveTime(),
            System.currentTimeMillis()
        );
    }
}

@Data
public class NodeStatus {
    private String nodeId;          // 节点ID
    private ServerRole role;        // 当前角色
    private boolean active;         // 是否激活
    private long activeTime;        // 激活时间
    private long lastCheckTime;     // 最后检查时间
} 