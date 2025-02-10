package heartbeat;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;
import javax.annotation.PostConstruct;

@Data
@Component
@ConfigurationProperties(prefix = "simplemq.heartbeat")
public class HeartbeatProperties {
    private int interval = 5000;    // 心跳间隔(ms)
    private int timeout = 15000;    // 超时时间(ms)
    private int port = 9091;        // 心跳端口
    private String peerAddress;     // 对端地址
    private String serverId;        // 服务器ID
    private ServerRole initialRole; // 初始角色
    private long startTime;        // 启动时间
    
    @PostConstruct
    public void init() {
        this.startTime = System.currentTimeMillis();  // 在服务启动时记录时间戳
    }
} 