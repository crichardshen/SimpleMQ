package com.SimpleMQ.Util;

import com.SimpleMQ.Message.RawMessageContent;
import com.SimpleMQ.Message.RawMessageType;

import java.io.*;

public class StreamOperation {
    public static DataOutputStream GetDataOutputStream(OutputStream out)
    {
        return new DataOutputStream(new BufferedOutputStream(out));
    }

    public static DataInputStream GetDataInputStream(InputStream in)
    {
        return new DataInputStream(new BufferedInputStream(in));
    }

    public static RawMessageContent GetMessageFromInStream(DataInputStream dataIn)
            throws Exception {
        if(null == dataIn)
        {
            throw new Exception("The data input stream is empty");
        }

        RawMessageContent rawMessageContent = new RawMessageContent();
        rawMessageContent.GetMsgFromStream(dataIn);
        return rawMessageContent;
    }

    public static void AssignAndSendMessageOutStream(DataOutputStream dataOut, byte[] msg)
            throws Exception {
        if(null == dataOut)
        {
            throw new Exception("The data output stream is empty");
        }

        int messageLen = msg.length;
        //|MsgType(4 digit) + MsgBodyLen + MsgBody|
        dataOut.write(RawMessageType.BiZ_MSG.getMagicCode());
        dataOut.writeInt(messageLen);
        dataOut.write(msg);
        dataOut.flush();
    }
}
