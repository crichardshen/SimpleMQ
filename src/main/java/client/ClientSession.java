 package com.SimpleMQ.Client;

import com.SimpleMQ.Message.RawMessageProtocolType;

/*
* The class to log down the session information and link to a message
* client id : is the id of message source
* protocolType : how message transfer, socket, web socket, HTML
* sessionID : unique id of the message
* lastActiveTime : the message last activation time, no use so far
*/
public class ClientSession {
    private String clientID;
    private RawMessageProtocolType rawMessageProtocolType;
    private String sessionID;
    private long lastActiveTime;

    public ClientSession(String clientID, RawMessageProtocolType rawMessageProtocolType, String sessionID, long lastActiveTime) {
        this.clientID = clientID;
        this.rawMessageProtocolType = rawMessageProtocolType;
        this.sessionID = sessionID;
        this.lastActiveTime = lastActiveTime;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public RawMessageProtocolType getProtocolType() {
        return rawMessageProtocolType;
    }

    public void setProtocolType(RawMessageProtocolType rawMessageProtocolType) {
        this.rawMessageProtocolType = rawMessageProtocolType;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public long getLastActiveTime() {
        return lastActiveTime;
    }

    public void setLastActiveTime(long lastActiveTime) {
        this.lastActiveTime = lastActiveTime;
    }

}
