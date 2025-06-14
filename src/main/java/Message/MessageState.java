package com.SimpleMQ.Message;

public class MessageState {

    private String status;
    private long processStartTime;
    private long processEndTime;

    public MessageState(String status)
    {
        this.status = status;
        this.processStartTime = System.currentTimeMillis();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getProcessStartTime() {
        return processStartTime;
    }

    public void setProcessStartTime(long processStartTime) {
        this.processStartTime = processStartTime;
    }

    public long getProcessEndTime() {
        return processEndTime;
    }

    public void setProcessEndTime(long processEndTime) {
        this.processEndTime = processEndTime;
    }

}

