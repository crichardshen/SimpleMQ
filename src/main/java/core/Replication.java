package core;
import message.Message;

public interface Replication {
    void syncToSlave(Message message);
    void promoteToMaster();
    void degradeToSlave();
    boolean isMaster();
} 