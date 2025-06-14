package com.SimpleMQ.Entity;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.*;

@Setter
@Getter
public final class MQSocket {
    private Long lastActiveTime = 0L;
    private String producer = "";
    private String consumer = "";
    private String socketID = "";
    private Socket socket = null;

    public MQSocket(Socket socket)
    {
        this.socket = socket;
    }

    public void SetLastActiveTimeByDefault()
    {
        this.lastActiveTime = System.currentTimeMillis();
    }
}
