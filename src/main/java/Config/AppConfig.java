package com.SimpleMQ.Config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Setter
@Getter
@Component
public class AppConfig {
    private final String serverPort;
    private final String socketPort;
    private final Map<String,String[]> routetable = new ConcurrentHashMap<>();
    private final String messageLogPath;
    private final String grafanaLogPath;
    private final String rountRule;

    @Autowired
    public AppConfig(Environment env) {
        this.serverPort = env.getProperty("server.port");
        this.socketPort = env.getProperty("socket.server.port");
        this.messageLogPath = env.getProperty("appConfig.MessageLog.path");
        this.grafanaLogPath = env.getProperty("appConfig.GrafanaLog.path");
        this.rountRule = env.getProperty("routetable.rule");
        this.routetable.put(env.getProperty("routetable.atmh.key")
                ,env.getProperty("routetable.atmh.socket").split(","));
        this.routetable.put(env.getProperty("routetable.jetco.key")
                ,env.getProperty("routetable.jetco.socket").split(","));
        this.routetable.put(env.getProperty("routetable.epsco.key")
                ,env.getProperty("routetable.epsco.socket").split(","));


    }

}
