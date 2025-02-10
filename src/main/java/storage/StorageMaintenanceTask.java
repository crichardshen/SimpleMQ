package storage;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StorageMaintenanceTask {
    private final JsonFileMessageStorage messageStorage;
    
    @Scheduled(cron = "0 0 * * * *")  // 每小时执行
    public void createSnapshot() {
        messageStorage.createHourlySnapshot();
    }
    
    @Scheduled(cron = "0 0 0 * * *")  // 每天执行
    public void cleanupOldData() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(7);  // 保留7天数据
        messageStorage.cleanExpiredData(cutoff);
    }
} 