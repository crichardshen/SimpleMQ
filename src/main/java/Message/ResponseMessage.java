package com.SimpleMQ.Message;

import com.SimpleMQ.Util.DateOperation;
import com.SimpleMQ.Util.IOOperation;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

@Getter
@Setter
public class ResponseMessage implements MQMessage {
    private String messageID; // original request message id
    private String clientID;
    private byte[] responseMessage;
    private RawMessageProtocolType rawMessageProtocolType;
    private String sessionID;
    private RawMessage originalMsg;
    private String destinationIDAfterRounted;

//    public RequestMessage getOriginalMsg() {
//        return originalMsg;
//    }
//
//    public void setOriginalMsg(RequestMessage originalMsg) {
//        this.originalMsg = originalMsg;
//    }
//
//    public String getMessageID() {
//        return messageID;
//    }
//
//    public void setMessageID(String messageID) {
//        this.messageID = messageID;
//    }
//
//    public String getClientID() {
//        return clientID;
//    }
//
//    public void setClientID(String clientID) {
//        this.clientID = clientID;
//    }
//
//    public byte[] getResponseMessage() {
//        return responseMessage;
//    }
//
//    public void setResponseMessage(byte[] responseMessage) {
//        this.responseMessage = responseMessage;
//    }
//
//    public ProtocolType getProtocolType() {
//        return protocolType;
//    }
//
//    public void setProtocolType(ProtocolType protocolType) {
//        this.protocolType = protocolType;
//    }
//
//    public String getSessionID() {
//        return sessionID;
//    }
//
//    public void setSessionID(String sessionID) {
//        this.sessionID = sessionID;
//    }

    @Override
    public String toString()
    {
        return String.format("Message detail: \n" +
                        "ClientID: %s \n" +
                        "TargetClientID: %s \n" +
                        "Content: %s"
                ,this.clientID,this.originalMsg.getTargetClientID()
                , IOOperation.ToStringFromByte(this.responseMessage));
    }

    @Override
    public void SaveMessage(String pathToSave) {
        String storagePath = pathToSave + "\\" +
                DateOperation.GetLocalDate() + "\\";

        String fileName =  DateOperation.GetLocalTime() + "_" + this.messageID + ".txt";

        try {
            IOOperation.SaveFile(storagePath,fileName,
                    IOOperation.ToStringFromByte(this.getResponseMessage()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
