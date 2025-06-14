package com.SimpleMQ.Broker;

import com.SimpleMQ.Client.ClientSession;
import com.SimpleMQ.Message.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;

@Component
@Getter
@Setter
public class MQBroker {
    public Map<String, BlockingQueue<RawMessage>> getRequestMessageQueues() {
        return requestMessageQueues;
    }
    public BlockingQueue<ResponseMessage> getResponseMessageQueues() {
        return responseMessageQueues;
    }
//    public Map<String, ClientSession> getClientSession() {
//        return clientSession;
//    }
//    public Map<String, MessageState> getMessageStates() {
//        return messageStates;
//    }

    private final Map<String, BlockingQueue<RawMessage>> requestMessageQueues =
            new ConcurrentHashMap<>();
    private final BlockingQueue<ResponseMessage> responseMessageQueues=
            new ArrayBlockingQueue<>(45000); //around 2.5 GB with (12 msg/second * 10KB) * 1 hours
    private final Map<String, ClientSession> clientSession =
            new ConcurrentHashMap<>();
    private final Map<String, MessageState> messageStates =
            new ConcurrentHashMap<>();
    //private final Map<String, RoutingInfo> routingTable  = new ConcurrentHashMap<>();

    public void AddToRequestMessageQueue(RawMessage message)
            throws Exception {
        String correlationId = UUID.randomUUID().toString();
        String clientID = message.getClientID();
        /*
        * To use client id as unit key which linked with a queue
        * and add the message to a queue
        * so that with client id can get all message should send to it
        * */
        Boolean result = requestMessageQueues.computeIfAbsent(clientID,
                k->new ArrayBlockingQueue<>(45000)).offer(message);

        if(!result)
        {
            throw new Exception("The message can not be added in the requestMessageQueues");
        }

//        requestMessageQueues.computeIfAbsent(clientID,
//                k->new ArrayBlockingQueue<>()).offer(message);

        //<atmh,queue> -> <'atmh', ['abcd']-[message2]-[msg3]-.....>
        messageStates.put(message.getClientID() + ":" + System.currentTimeMillis(),
                new MessageState(MessageStatus.RECEIVED.toString()));
    }

    public Optional<RawMessage> PollRequestMessage(String clientID)
    {
        Queue<RawMessage> queue = requestMessageQueues.get(clientID);
        return queue != null ? Optional.ofNullable(queue.poll()) : Optional.empty();
    }

    public void AddResponseMessage(ResponseMessage responseMessage)
            throws InterruptedException {

        //if the queue is full, will block the thread and wait for the available
        responseMessageQueues.put(responseMessage);
        messageStates.put(responseMessage.getMessageID()
                ,new MessageState(MessageStatus.RESPONSED.toString()));
    }

//    public Optional<ResponseMessage> PollResponseMessage()
//    {
//        return Optional.ofNullable(responseMessageQueues.poll());
//    }

    //It will be waiting until the head element is existed and available
    public ResponseMessage TakeResponseMessage() throws InterruptedException {
        return responseMessageQueues.take();
    }

    public Optional<ClientSession> GetClientSession(String clientID)
    {
        return Optional.ofNullable(clientSession.get(clientID));
    }
}


