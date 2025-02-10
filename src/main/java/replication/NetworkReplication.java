package replication;

import core.Replication;
import message.Message;

public class NetworkReplication implements Replication {
    private final NetworkTransport transport;
    private final ReplicationState state;

    @Override
    public void syncToSlave(Message message) {

    }

    @Override
    public void promoteToMaster() {

    }

    @Override
    public void degradeToSlave() {

    }

    @Override
    public boolean isMaster() {
        return false;
    }

    // 网络复制方式的主备实现
} 