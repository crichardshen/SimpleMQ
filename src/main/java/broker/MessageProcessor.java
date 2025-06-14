package com.SimpleMQ.Broker;

import com.SimpleMQ.Config.AppConfig;
import com.SimpleMQ.Message.*;
import com.SimpleMQ.Sender.ResponseSenderFactory;
import com.SimpleMQ.Session.SocketSessionManager;
import com.SimpleMQ.Util.Logger;
import com.SimpleMQ.Util.RouteOperation;
import com.SimpleMQ.Util.ThreadOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;


@Service
public class MessageProcessor {
    private final MQBroker messageHub;
    private final ExecutorService processingPool;
    private final SocketSessionManager sessionManager;
    private final Logger logger; //= new Logger("MessageProcessor");
    private final AppConfig appConfig;

    @Autowired
    public MessageProcessor(MQBroker messageHub, SocketSessionManager sessionManager, Logger logger
            , AppConfig appConfig) {
        this.messageHub = messageHub;
        this.sessionManager = sessionManager;
        this.logger = logger;
        this.appConfig = appConfig;
        this.processingPool = Executors.newWorkStealingPool();
    }

    /**
     * this is the class running with async to handle the message from ourside
     */
    @Async
    public void startProcessing() {
        logger.PrintInfo("started!");
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        executorService.execute(()->{
            Thread.currentThread().setName("[Thread:Process Request Msg]");
            final String threadName = Thread.currentThread().getName();
            logger.PrintInfo(threadName + " stared");
            while(!Thread.currentThread().isInterrupted())
            {
                try
                {
                        /*
                         * This function will do below tasks
                         * 1- To cover the request message to handled message
                         * 2- To cover the handled message to response message
                         * 3- add the response message to hub queue
                         */
                        processRequestMessages();

                } catch (Exception e) {
                    logger.PrintError("Error occurred! with thread" + threadName);
                    logger.PrintError("Detail: " + e);
                    break;
                }
            }
        });

        executorService.execute(()-> {
            Thread.currentThread().setName("[Thread:Sendout Response Msg]");
            final String threadName = Thread.currentThread().getName();
            logger.PrintInfo(threadName + " stared");
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    /*
                     * after the response message add to the hub queue
                     * this function will get the message from the hub
                     * and select target protocal to transfer the message out
                     */
                    sendOutResponseMsg();
                }
                catch(Exception e){
                    logger.PrintError("Error occurred! with thread" + threadName);
                    logger.PrintError("Detail: " + e);
                    break;
                }
            }
        });
    }

    /**
     * This function will do below tasks
     * 1- To cover the request message to handled message
     * 2- To cover the handled message to response message
     * 3- add the response message to hub queue
     */
    private void processRequestMessages() {
        if(messageHub.getRequestMessageQueues().isEmpty()) {return;}

        //<atmh,queue> -> <'atmh', ['abcd']-[message2]-[msg3]-.....>
        //<jetco,queue> -> <'jetco', ['abcd']-[message2]-[msg3]-.....>
        //<epsco,queue> -> <'epsco', ['abcd']-[message2]-[msg3]-.....>
        messageHub.getRequestMessageQueues().forEach((clientId, queue) -> {
            //try to get the message, and will delete it only the message processed
            RawMessage rawMessage = null;
            try {
                rawMessage = queue.take();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            /*
            * To get the socket id by consumer id
            * clinetid -> targetclientid -> socketid -> socket
            * */
            String destinationID = RouteOperation.GetRoutedTargetOnRequestMessage(appConfig.getRountRule(),
                    rawMessage,appConfig.getRoutetable());
            String targetSocketIDWithClient
                    = this.sessionManager.getSocketIdByClient(destinationID);

            if((targetSocketIDWithClient == null)
                    || (this.sessionManager.getSocket(targetSocketIDWithClient)==null))
            {
                // add back the message to the queue, due to no target socket found
                // no need to alert error here, because it could be the destination handler is not started yet
                try {
                    queue.put(rawMessage);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return;
            }

            final RawMessage  finalRawMessage = rawMessage;
            processingPool.submit(() -> {
                try {
                    ThreadOperation.SetCurrentThreadName(finalRawMessage.getMessageId());
                    String logInfo = ThreadOperation.ToString();
                    //Do nothing, just convert type from request message to handled message
//                    List<HandledMessage> handledMessages = unpackMessages(finalRawMessage);
//                    logger.PrintInfo(logInfo + "Converted done");

                    //to prepare response message
                    //converHandledMsgToResMsg(handledMessages);
                    ConverHandledMsgToResMsg(finalRawMessage);
                    logger.PrintInfo(logInfo + "Add response queue done");
                } catch (Exception e) {
                    logger.PrintError(e);
                }
            });
        });
    }

    /**
     * send out the response message
     */
    private void sendOutResponseMsg() {
        try {
            ResponseMessage response = messageHub.TakeResponseMessage();
            processingPool.submit(() -> {
                try {
                    ResponseSenderFactory.getSender(response.getRawMessageProtocolType())
                            .send(response);
                    logger.PrintInfo("Message sent " + response);
                } catch (Exception e) {
                    logger.PrintError(e);
                }
            });
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * this function is for multiple Message in one time raw data
     * but base on the design, no need to handle this case
     */
//    private List<HandledMessage> unpackMessages(RawMessage rawMessage) {
//        List<HandledMessage> messages = new ArrayList<>();
//        HandledMessage handledMessage = new HandledMessage(rawMessage);
//        messages.add(handledMessage);
//        return messages;
//    }

    /**
     * 1- convert list of handled message to list of response message
     * 2- add the response message to queue in hub
     */
    private void ConverHandledMsgToResMsg(RawMessage messages) {
        try {
            ResponseMessage response = new ResponseMessage();
            response.setMessageID(messages.getMessageId());
            response.setClientID(messages.getClientID());
            response.setRawMessageProtocolType(RawMessageProtocolType.valueOf(
                    messages.getRawMessageProtocolType().toString())
            );
            response.setResponseMessage(messages.getRequestData());
            response.setOriginalMsg(messages);

            //set the actual destination by route table
            response.setDestinationIDAfterRounted(
                    RouteOperation.GetRoutedTargetOnResponseMessage(appConfig.getRountRule(),
                            response,appConfig.getRoutetable())
            );
            messageHub.AddResponseMessage(response);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e)
        {
            logger.PrintError(e);
        }
//        messages.forEach(msg -> {
//            try {
//                ResponseMessage response = new ResponseMessage();
//                response.setMessageID(msg.getMessageID());
//                response.setClientID(msg.getCliendID());
//                response.setProtocolType(ProtocolType.valueOf(
//                        msg.getMetaData().get("protocol").toString())
//                );
//                response.setResponseMessage(msg.getContent().getBytes());
//                response.setOriginalMsg(msg.getOriginalMessage());
//
//                //set the actual destination by route table
//                response.setDestinationIDAfterRounted(
//                        RouteOperation.GetRoutedTargetOnResponseMessage(appConfig.getRountRule(),
//                                response,appConfig.getRoutetable())
//                );
//                messageHub.AddResponseMessage(response);
//
//                TimeUnit.MILLISECONDS.sleep(100);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            } catch (Exception e)
//            {
//                logger.PrintError(e.getMessage());
//                e.printStackTrace();
//            }
//        });
    }
}
