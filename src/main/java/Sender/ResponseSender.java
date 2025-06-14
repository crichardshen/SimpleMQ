package com.SimpleMQ.Sender;

import com.SimpleMQ.Message.ResponseMessage;

public interface ResponseSender {
    public void send(ResponseMessage response);
}
