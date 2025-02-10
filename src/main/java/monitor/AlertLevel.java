package monitor;

import org.springframework.stereotype.Service;

@Service
public class AlertService {
    public void sendAlert(String message, AlertLevel level) {
        Alert alert = new Alert(
            System.currentTimeMillis(),
            message,
            level
        );
        
        // 根据告警级别采取不同措施
        switch (level) {
            case CRITICAL:
                sendEmail(alert);
                sendSMS(alert);
                break;
            case WARNING:
                sendEmail(alert);
                break;
            case INFO:
                logAlert(alert);
                break;
        }
    }
}

public enum AlertLevel {
    INFO,
    WARNING,
    CRITICAL
} 