package com.SimpleMQ.Sender;

import com.SimpleMQ.Broker.MQBroker;
import com.SimpleMQ.Message.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * HTTP协议响应发送器
 */
@Slf4j
@Component
public class HttpResponseSender implements ResponseSender {

    private final RestTemplate restTemplate;
    private final MQBroker messageHub;

    @Autowired
    public HttpResponseSender(RestTemplate restTemplate, MQBroker messageHub) {
        this.restTemplate = restTemplate;
        this.messageHub = messageHub;
    }

    @Override
    public void send(ResponseMessage response) {
        String callbackUrl = "http://client/callback"; // call back url, here is the fake sample

        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                    callbackUrl,
                    response.getResponseMessage(),
                    String.class);

            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                System.out.println("Failed to send HTTP response to Client: " + response.getClientID());
            }
        } catch (Exception e) {
            System.out.println("Error sending HTTP response to Client: " + response.getClientID());
            e.printStackTrace();
        }
    }
}
