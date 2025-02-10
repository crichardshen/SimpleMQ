package metrics;

import org.springframework.stereotype.Component;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

@Component
public class MessageMetrics {
    private final Counter messageCounter;
    private final Timer processTimer;
    
    public MessageMetrics(MeterRegistry registry) {
        this.messageCounter = registry.counter("message.processed");
        this.processTimer = registry.timer("message.process.time");
    }
} 