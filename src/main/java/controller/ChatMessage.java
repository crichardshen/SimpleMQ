package com.SimpleMQ.Controller;

import com.SimpleMQ.Broker.MQBroker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/messages")
public class HttpMessageController {
    private final MQBroker messageHub;

    @Autowired
    public HttpMessageController(MQBroker messageHub) {
        this.messageHub = messageHub;
    }

    @PostMapping
    public String receiveMessage(@RequestBody byte[] data,
                                 @RequestHeader("X-Client-Id") String clientId) {
//        RequestMessage message = new RequestMessage();
//        message.setClientID(clientId);
//        message.setProtocolType(ProtocolType.HTTP);
//        message.setRequestData(data);
//        message.setTimestamp(System.currentTimeMillis());
//        message.setSessionID(UUID.randomUUID().toString());
//
//        messageHub.AddToRequestMessageQueue(message);
//        System.out.println("Received HTTP Message from Client:" + clientId);

        return "Message accepted";
    }
}
