package com.SimpleMQ.Monitor;
import com.SimpleMQ.Entity.MQSocket;
import com.SimpleMQ.Message.MQMessage;
import com.SimpleMQ.Message.RawMessage;
import com.SimpleMQ.Message.ResponseMessage;
import com.SimpleMQ.Util.DateOperation;
import com.SimpleMQ.Util.IOOperation;
import java.io.IOException;

public class Grafana {
    private String grafanaLogPath = "";


    public Grafana(String logPath) {
        this.grafanaLogPath = logPath;
    }


    public void SaveRequestMsg(MQMessage message, MQSocket mqSocket) throws IOException {
        RawMessage rawMessage = (RawMessage) message;
        GrafanaContent gc = new GrafanaContent(
                DateOperation.GetStandardDateTime(),
                "CONEGW",
                "MQ",
                rawMessage.getClientID()+"_to_"+ rawMessage.getTargetClientID(),
                "SocketMessageServerCall",
                "individual",
                rawMessage.getMessageId(),
                mqSocket.getSocket().getInetAddress().getHostAddress(),
                "200","",
                String.valueOf(System.currentTimeMillis()- rawMessage.getTimestamp()),
                rawMessage.getMessageId(),"");

        this.GrafanaWriteForMessage(gc);
    }

    public void SaveResponseMsg(MQMessage message, MQSocket mqSocket) throws IOException {
        RawMessage rawMessage = ((ResponseMessage)message).getOriginalMsg();
        GrafanaContent gc = new GrafanaContent(
                DateOperation.GetStandardDateTime(),
                "CONEGW",
                "MQ",
                rawMessage.getClientID()+"_to_"+ rawMessage.getTargetClientID(),
                "SocketMessageServerCall",
                "individual",
                rawMessage.getMessageId(),
                mqSocket.getSocket().getInetAddress().getHostAddress(),
                "200","",
                String.valueOf(System.currentTimeMillis()- rawMessage.getTimestamp()),
                rawMessage.getMessageId(),"");

        this.GrafanaWriteForMessage(gc);
    }



    private void GrafanaWriteForMessage(GrafanaContent gc) throws IOException {
        String grafanaLogFileName = "grafana_" + DateOperation.GetLocalDate() + ".log";
        IOOperation.SaveFile(grafanaLogPath, grafanaLogFileName,gc.ToString());
    }
}
