package controller;

import consumer.Consumer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import message.Message;
import broker.SimpleMQBroker;
import org.springframework.beans.factory.annotation.Value;
import java.util.Base64;
import model.SubscriptionResponse;
import model.ConsumerInfo;
import org.springframework.http.MediaType;
import model.ProducerInfo;
import model.ConnectionResponse;
import config.DispatcherProperties;

//对外暴露 REST API
//用于接收来自生产者的消息，以及，来自消费者的订阅
@RestController
@RequestMapping("/api/message")
public class MessageController {
    
    @Value("${simplemq.websocket.url}")
    private String websocketUrl;
    
    @Autowired
    private SimpleMQBroker broker;
    
    @Autowired
    private DispatcherProperties properties;
    
    //生产者通过调用 REST API 来传递消息
    @PostMapping(value = "/publish", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> publishBinaryMessage(
            @RequestHeader("X-Topic") String topic,
            @RequestBody byte[] messageBody) {
        try {
            Message message = new Message();
            message.getHeader().setTopic(topic);
            message.setBody(messageBody);
            
            broker.publish(message);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body("Failed to publish message: " + e.getMessage());
        }
    }
    
    // 保留原有的 JSON 消息接口
    @PostMapping(value = "/publish", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> publishJsonMessage(@RequestBody Message message) {
        try {
            broker.publish(message);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body("Failed to publish message: " + e.getMessage());
        }
    }
    
    //消费者通过调用 REST API来订阅
    //返回：websocket 的相关信息，消费者拿到信息之后通过 websocket 和程序建立实时链接
    @PostMapping("/subscribe/{topic}")
    public ResponseEntity<?> subscribe(
            @PathVariable String topic, 
            @RequestBody ConsumerInfo consumerInfo) {
        try {
            String token = generateToken(consumerInfo.getConsumerId(), topic);
            consumerInfo.setToken(token);
            
            // 创建消费者
            Consumer consumer = new Consumer(topic, consumerInfo);

            // 设置消费者优先级，有两种选择，当前使用第二种，优先级的主动权在 MQ
            // 1- 这里是按照来自consumer 发送的优先级来设置优先级
            //consumer.setPriority(consumerInfo.getPriority()); 

            // 2- 这里是根据配置文件里的hostname设置优先级
            String hostname = getHostnameFromConsumerId(consumerInfo.getConsumerId());
            int priority = properties.getPriorityForHost(hostname);
            consumer.setPriority(priority);
            
            broker.subscribe(consumer);
            
            return ResponseEntity.ok(new SubscriptionResponse(
                websocketUrl,
                token,
                topic
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body("Failed to subscribe: " + e.getMessage());
        }
    }
    
    private String generateToken(String consumerId, String topic) {
        // 简单的token生成逻辑，实际应用中应该更安全
        return Base64.getEncoder().encodeToString(
            (consumerId + ":" + topic + ":" + System.currentTimeMillis()).getBytes()
        );
    }
    
    private String getHostnameFromConsumerId(String consumerId) {
        // 假设consumerId的格式包含hostname信息
        // 例如：consumerId = "consumer-abc_1a"
        // 返回 "abc_1a"
        return consumerId.split("-")[1];  // 根据实际的consumerId格式调整
    }
    
    @PostMapping("/register/producer")
    public ResponseEntity<?> registerProducer(@RequestBody ProducerInfo producerInfo) {
        try {
            String token = generateProducerToken(producerInfo);
            
            // 返回WebSocket连接信息
            return ResponseEntity.ok(new ConnectionResponse(
                websocketUrl,
                token,
                "producer"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body("Failed to register producer: " + e.getMessage());
        }
    }
    
    private String generateProducerToken(ProducerInfo producerInfo) {
        // Implementation of generateProducerToken method
        // This method should be implemented to generate a token for a producer
        // For now, we'll use a placeholder return
        return "placeholder_producer_token";
    }
} 