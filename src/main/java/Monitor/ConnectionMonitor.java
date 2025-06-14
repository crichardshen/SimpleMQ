package com.SimpleMQ.Monitor;

import com.SimpleMQ.Broker.MQBroker;
import com.SimpleMQ.Broker.MessageProcessor;
import com.SimpleMQ.Session.SocketSessionManager;
import com.SimpleMQ.Util.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.Socket;

@Configuration
public class ConnectionMonitor implements CommandLineRunner  {
    private final MessageProcessor messageProcessor;
    private final SocketSessionManager sessionManager;
    private final Logger logger; //= new Logger("AppStartupConfig");


    @Autowired
    public ConnectionMonitor(MessageProcessor messageProcessor1, SocketSessionManager messageProcessor, Logger logger)
    {
        this.messageProcessor = messageProcessor1;
        this.sessionManager = messageProcessor;
        this.logger = logger;
    }

    @Override
    public void run(String... args) throws Exception {
        new Thread(()->{
            while(!Thread.currentThread().isInterrupted()) {
                this.sessionManager.getSockets().forEach((clientId, mqSocket) -> {
                    Socket socket = mqSocket.getSocket();
                    if(!socket.isConnected() ||
                            System.currentTimeMillis() - mqSocket.getLastActiveTime()  > 30000) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }else {
                        System.out.println("refresh active live for " + mqSocket.getSocketID());
                        mqSocket.setLastActiveTime(System.currentTimeMillis());
                    }
                });

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

}
