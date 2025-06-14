package com.SimpleMQ.Broker;

import com.SimpleMQ.Util.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

/*
* To start the MessageProcessor
*/
@Configuration
public class MessageProcessorStarter implements CommandLineRunner {
    private final MessageProcessor messageProcessor;
    private final Logger logger; //= new Logger("AppStartupConfig");

    @Autowired
    public MessageProcessorStarter(MessageProcessor messageProcessor, Logger logger) {
        this.messageProcessor = messageProcessor;
        this.logger = logger;
        logger.PrintInfo("MessageProcessor init done!");
    }

    @Override
    public void run(String... args) throws Exception {
        messageProcessor.startProcessing();
    }
}
