package core;
import message.Message;

import java.util.List;

public interface Storage {
    void store(Message message);
    Message retrieve(String messageId);
    List<Message> retrieveAll(String topic);
    void delete(String messageId);
} 