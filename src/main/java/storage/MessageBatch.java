package storage;

import message.Message;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class MessageBatch {
    private List<Message> messages = new ArrayList<>();
} 