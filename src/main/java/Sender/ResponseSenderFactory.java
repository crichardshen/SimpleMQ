package com.SimpleMQ.Sender;

import com.SimpleMQ.Message.RawMessageProtocolType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ResponseSenderFactory {
    private static ApplicationContext applicationContext;

    @Autowired
    public ResponseSenderFactory(ApplicationContext applicationContext) {
        ResponseSenderFactory.applicationContext = applicationContext;
    }

    public static ResponseSender getSender(RawMessageProtocolType protocol) {
        switch (protocol) {
            case SOCKET:
                return applicationContext.getBean(SocketResponseSender.class);
            case WEBSOCKET:
                return applicationContext.getBean(WebSocketResponseSender.class);
            case HTTP:
                return applicationContext.getBean(HttpResponseSender.class);
            default:
                throw new IllegalArgumentException("Unsupported protocol: " + protocol);
        }
    }
}
