package monitor;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SwitchEventMonitor {
    // 使用线程安全的列表存储切换历史
    private final List<SwitchEvent> switchHistory = new CopyOnWriteArrayList<>();
    
    /**
     * 记录角色切换事件
     * @param fromRole 原角色
     * @param toRole 新角色
     * @param reason 切换原因
     */
    public void recordSwitch(ServerRole fromRole, ServerRole toRole, String reason) {
        SwitchEvent event = new SwitchEvent(
            System.currentTimeMillis(),
            fromRole,
            toRole,
            reason
        );
        switchHistory.add(event);
        
        // 如果短时间内切换次数过多，发送告警
        if (switchHistory.size() > 5) {  
            alertService.sendAlert("Frequent role switches detected!");
        }
    }
} 