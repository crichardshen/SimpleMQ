package com.SimpleMQ.Message;


import com.SimpleMQ.Util.DateOperation;
import com.SimpleMQ.Util.IOOperation;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.UUID;


@Setter
@Getter
public class RawMessage implements MQMessage{

    private String clientID;
    private RawMessageProtocolType rawMessageProtocolType;
    private byte[] requestData;
    private long timestamp;
    private String sessionID;
    private String targetClientID;

    //header len, body len

    private final String messageId = UUID.randomUUID().toString();
    private String callbackUrl;

    @Override
    public String toString()
    {
        return String.format("Message detail:" +
                "ClientID: %s \n" +
                "TargetClientID: %s \n" +
                "Content: %s"
                ,this.clientID,this.targetClientID, IOOperation.ToStringFromByte(this.requestData));
    }

    @Override
    public void SaveMessage(String pathToSave) {
        String storagePath = pathToSave + "\\" +
                DateOperation.GetLocalDate() + "\\";

        String fileName =  DateOperation.GetLocalTime() + "_" + this.messageId + ".txt";

        try {
            IOOperation.SaveFile(storagePath,fileName,
                    IOOperation.ToStringFromByte(this.getRequestData()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

