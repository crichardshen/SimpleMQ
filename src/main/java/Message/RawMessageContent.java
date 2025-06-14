package com.SimpleMQ.Message;


import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Setter
@Getter
public class RawMessageContent {
    private byte[] msgTypeinBtye;
    private int messageBodyLen;
    private byte[] messageBody;
    private RawMessageType msgType;

    public RawMessageContent(RawMessageType msgType, int messageHead, byte[] messageBody) {
        //"HATM" -> ATM message for hand shake
        //"MATM" -> ATM message for normal data
        this.msgType = msgType;
        this.msgTypeinBtye = msgType== RawMessageType.HANDSHAKE_MSG?
                "HATM".getBytes(StandardCharsets.US_ASCII) : "MATM".getBytes(StandardCharsets.US_ASCII);
        this.messageBodyLen = messageHead;
        this.messageBody = messageBody;
    }

    public RawMessageContent() {}

    public void GetMsgFromStream(DataInputStream dataInputStream) throws Exception {
        byte[] msgType = new byte[4];
        dataInputStream.readFully(msgType);
        this.msgType = RawMessageType.FromByte(msgType);
        this.msgTypeinBtye = msgType;
        this.messageBodyLen = dataInputStream.readInt();
        this.messageBody = new byte[messageBodyLen];
        dataInputStream.readFully(messageBody);
    }



//    public OutputStream MassageMsg(OutputStream outputStream) throws IOException {
//        outputStream.write(this.msgType);
//        outputStream.write(this.messageHead);
//        outputStream.write(this.messageBody);
//        return outputStream;
//    }
//
//    public DataOutputStream MassageMsg(DataOutputStream dataOutputStream) throws IOException {
//        dataOutputStream.write(this.msgType);
//        dataOutputStream.write(this.messageHead);
//        dataOutputStream.write(this.messageBody);
//        return dataOutputStream;
//    }

}

