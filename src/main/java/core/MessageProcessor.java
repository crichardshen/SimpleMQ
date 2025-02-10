package core;

import message.Message;

public interface MessageProcessor {
    void process(Message message);
} 