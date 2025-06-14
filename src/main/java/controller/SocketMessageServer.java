package com.SimpleMQ.Controller;

import com.SimpleMQ.Config.AppConfig;
import com.SimpleMQ.Entity.MQSocket;
import com.SimpleMQ.Message.*;
import com.SimpleMQ.Monitor.Grafana;
import com.SimpleMQ.Session.SocketSessionManager;
import com.SimpleMQ.Broker.MQBroker;
import com.SimpleMQ.Util.Logger;
import com.SimpleMQ.Util.StreamOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.UUID;
import java.util.concurrent.*;

@Slf4j
@Component
public class SocketMessageServer {
    private final MQBroker messageHub; // manage message
    private final SocketSessionManager sessionManager; // manage socket
    private final ExecutorService executorService; // thread pool

    private final int socketPort;
    private final AppConfig appConfig;

    @Autowired
    private Logger logger;

    public SocketMessageServer(MQBroker messageHub,
                               SocketSessionManager sessionManager,
                               @Value("${socket.server.port:9090}") int socketPort,
                               AppConfig appConfig) {
        this.messageHub = messageHub;
        this.sessionManager = sessionManager;

        //(ATMh+Jetco+Epsco)*2
        this.executorService = new ThreadPoolExecutor(
                6,12,12,
                TimeUnit.HOURS,new LinkedBlockingQueue<>(6)
        );
        this.socketPort = socketPort;
        this.appConfig = appConfig;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        logger.PrintInfo("started!");
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(socketPort)) {
                //allow reuse TIME_WAIT port
                //serverSocket.setReuseAddress(true);
                //not sure whether needs this, can verify in test phase
                //serverSocket.setPerformancePreferences(1,0,1);

                logger.PrintInfo("Socket server started on port " + socketPort );
                while (!Thread.currentThread().isInterrupted()) {
                    MQSocket mqSocket = new MQSocket(serverSocket.accept());
                    mqSocket.setSocketID(UUID.randomUUID().toString());
                    mqSocket.setLastActiveTime(System.currentTimeMillis());

                    //link the socket with socket id
                    sessionManager.addSocket(mqSocket.getSocketID(), mqSocket);

                    //link the socket id with client id
                    executorService.submit(() -> handleClient(mqSocket));
                }
            } catch (IOException e) {
                logger.PrintError("Socket server error when start server" + e );
            }
        }).start();
    }

    private void handleClient(MQSocket mqSocket) {
        String clientId = null;
        String targetClientId;
        try(
                Socket socket = mqSocket.getSocket();
                //get read stream
                DataInputStream dataIn = StreamOperation.GetDataInputStream(socket.getInputStream());
                //get write stream
                DataOutputStream dataOut = StreamOperation.GetDataOutputStream(socket.getOutputStream())
        ) {

            RawMessageContent rawMessageContent = new RawMessageContent();
            rawMessageContent.GetMsgFromStream(dataIn);
            if(rawMessageContent.getMsgType() != RawMessageType.HANDSHAKE_MSG)
            {
                String error = "This first message is not handshake message";
                logger.PrintError(error);
                throw new Exception(error);
            }

            String msgBoday = new String(rawMessageContent.getMessageBody());
            String[] parts = msgBoday.split("->");
            if(2 != parts.length)
            {
                String error = "Invalid message format. Expected 'clientId->targetClientId'";
                logger.PrintError(error);
                throw new IllegalArgumentException(error);
            }

            clientId = parts[0]; //e.g. atmh-jecto,sender is atmt
            targetClientId = parts[1]; //e.g. atmh-jecto, receiver is jetco
            mqSocket.setProducer(clientId);
            mqSocket.setConsumer(targetClientId);
            sessionManager.registerClientId(mqSocket.getSocketID(), mqSocket.getProducer());

            logger.PrintInfo("Socket producer " + mqSocket.getProducer()
                    + " connected: (sessionId= " + mqSocket.getSocketID()
                    + " target client " + targetClientId
            );

            //response handshake ok
            StreamOperation.AssignAndSendMessageOutStream(dataOut,"handshake ok".getBytes());

            //loop for rest of message
            while (!mqSocket.getSocket().isClosed()) {
                //start to receive message
                rawMessageContent = StreamOperation.GetMessageFromInStream(dataIn);

                //construct request message object
                RawMessage message = new RawMessage();
                message.setClientID(mqSocket.getProducer());
                message.setRawMessageProtocolType(RawMessageProtocolType.SOCKET);
                message.setRequestData(rawMessageContent.getMessageBody());
                message.setTimestamp(System.currentTimeMillis());
                message.setSessionID(mqSocket.getSocketID());
                message.setTargetClientID(mqSocket.getConsumer());

                logger.PrintInfo("Got message from Client: " + mqSocket.getProducer() );
                logger.PrintInfo("message detail: "
                        + new String(message.getRequestData()));

                //set the message to queue of hub and save in physical
                messageHub.AddToRequestMessageQueue(message);
                //send ack back
                StreamOperation.AssignAndSendMessageOutStream(dataOut,"ACK".getBytes());

                //do physical save with Async mode
                CompletableFuture.runAsync(()->{
                    //to try catch with exception from IO error
                    //but allow the socket continue to run
                    try{
                        //save message to local physical
                        message.SaveMessage(this.appConfig.getMessageLogPath());
                        //save message to grafana
                        Grafana gf = new Grafana(appConfig.getGrafanaLogPath());
                        gf.SaveRequestMsg(message,mqSocket);
                    }catch (Exception e)
                    {
                        logger.PrintError(e);

                    }
                });

            }
        } catch (EOFException e) {
            logger.PrintError("socket is closed (stream ended)");
            logger.PrintError(e);
        } catch (SocketException e) {
            logger.PrintError("Socket is closed abnormally");
            logger.PrintError(e);
        } catch (IOException e) {
            logger.PrintError("IO error");
            logger.PrintError(e);
        }catch (Exception e) {
            logger.PrintError("Socket Client error: "
                    + (mqSocket.getProducer() != null ? mqSocket.getProducer() : "unknown")
            );
            logger.PrintError(e);
        }finally {
            sessionManager.removeSocket(mqSocket.getSocketID());
            logger.PrintInfo("Socket Client disconnected: "+
                    (clientId != null ? mqSocket.getProducer() : "unknown")
                    +" (sessionId="+mqSocket.getSocketID()+")");
        }
    }
}
