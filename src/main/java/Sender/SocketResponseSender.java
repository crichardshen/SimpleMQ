package com.SimpleMQ.Sender;

import com.SimpleMQ.Broker.MQBroker;
import com.SimpleMQ.Config.AppConfig;
import com.SimpleMQ.Entity.MQSocket;
import com.SimpleMQ.Monitor.Grafana;
import com.SimpleMQ.Session.SocketSessionManager;
import com.SimpleMQ.Message.ResponseMessage;
import com.SimpleMQ.Util.Logger;
import com.SimpleMQ.Util.StreamOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.FutureTask;

@Component
public class SocketResponseSender implements ResponseSender {
    private final MQBroker messageHub;
    private final SocketSessionManager sessionManager;
    private final Logger logger; // = new Logger("SocketResponseSender");
    private final AppConfig appConfig;

    @Autowired
    public SocketResponseSender(MQBroker messageHub, SocketSessionManager sessionManager
            , Logger logger, AppConfig appConfig) {
        this.messageHub = messageHub;
        this.sessionManager = sessionManager;
        this.logger = logger;
        this.appConfig = appConfig;
    }

    @Override
    public void send(ResponseMessage response) {
        String destinationID = response.getDestinationIDAfterRounted();
        String socketID = sessionManager.getSocketIdByClient(destinationID);
        if(socketID == null)
        {
            logger.PrintInfo("socket NOT FOUND with target client id : "
                    + destinationID
            );
            try{
                messageHub.AddResponseMessage(response);
            }
            catch (Exception e)
            {
                logger.PrintError(e);
            }

            return;
        }


        Socket socket = sessionManager.getSocket(socketID).getSocket();

        if((socket == null) || (socket.isClosed()))
        {
            logger.PrintInfo("Socket session not found or closed: "
                    + response.getClientID());
            try{
                messageHub.AddResponseMessage(response);
            }
            catch (Exception e)
            {
                logger.PrintError(e);
            }
            return;
        }

        //set back the message to queue if failed
        //await status
        if (!socket.isClosed()) {
            try
            {
                DataOutputStream dataOut
                        = StreamOperation.GetDataOutputStream(socket.getOutputStream());
                StreamOperation.AssignAndSendMessageOutStream(dataOut,response.getResponseMessage());

                //log down grafana
                CompletableFuture.runAsync(()->{
                    try{
                        Grafana gf = new Grafana(appConfig.getGrafanaLogPath());
                        gf.SaveResponseMsg(response,new MQSocket(socket));
                    }catch (Exception e)
                    {
                        logger.PrintError(e);
                    }
                });
            } catch (EOFException e) {
                logger.PrintError("socket is closed (stream ended)");
                logger.PrintError(e);
            } catch (SocketException e) {
                logger.PrintError("Socket is closed abnormally");
                logger.PrintError(e);
            } catch (IOException e) {
                logger.PrintError("Failed to send Socket Message to session: "
                        + response.getSessionID());
                sessionManager.removeSocket(response.getSessionID());
                try {
                    socket.close();
                } catch (IOException ex) {
                    logger.PrintError("closing socket");
                    logger.PrintError(e);
                }
            } catch (Exception e) {
                logger.PrintError("Failed to send Socket Message to session: "
                        + response.getSessionID());
                sessionManager.removeSocket(response.getSessionID());
                logger.PrintError(e);
            }
        } else {

        }
    }
}
