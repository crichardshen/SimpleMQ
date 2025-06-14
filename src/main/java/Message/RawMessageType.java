package com.SimpleMQ.Message;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public enum RawMessageType {
    HANDSHAKE_MSG("HATM"),
    BiZ_MSG("BATM");

    public byte[] getMagicCode() {
        return magicCode.clone();
    }

    private byte[] magicCode ;
    RawMessageType(String magicCode) {
        this.magicCode = magicCode.getBytes(StandardCharsets.US_ASCII);
    }

    public static RawMessageType FromByte(byte[] msgType) throws Exception {
        if (((msgType[0] & 0x80) != 0) && (
                msgType[1] == 0x41 && msgType[2] == 0x54 && msgType[3] == 0x4D))
        {
            throw new Exception("The message type has incorrect byte");
        }

        if(Arrays.equals(msgType,RawMessageType.HANDSHAKE_MSG.getMagicCode())){
            return RawMessageType.HANDSHAKE_MSG;
        }

        if(Arrays.equals(msgType,RawMessageType.BiZ_MSG.getMagicCode())){
            return RawMessageType.BiZ_MSG;
        }

        throw new Exception("The message type has incorrect byte");
    }
}
