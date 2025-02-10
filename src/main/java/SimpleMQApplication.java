import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import socket.MessageSocketServer;
import heartbeat.HeartbeatSender;
import heartbeat.HeartbeatReceiver;

@SpringBootApplication
public class SimpleMQApplication {
    @Autowired
    private MessageSocketServer socketServer;
    
    @Autowired
    private HeartbeatSender heartbeatSender;
    
    @Autowired
    private HeartbeatReceiver heartbeatReceiver;
    
    public static void main(String[] args) {
        SpringApplication.run(SimpleMQApplication.class, args);
    }
    
    @PostConstruct
    public void init() {
        socketServer.start();
        heartbeatSender.start();
        heartbeatReceiver.start();
    }
    
    @PreDestroy
    public void destroy() {
        heartbeatSender.shutdown();
        heartbeatReceiver.shutdown();
    }
} 