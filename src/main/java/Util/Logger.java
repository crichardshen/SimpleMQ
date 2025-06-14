package com.SimpleMQ.Util;

import com.SimpleMQ.Config.AppConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.LinkedBlockingQueue;

@Component
@Getter
@Setter
public class Logger {
    private String handler;
    private String level;
    private String logPath;
    @Autowired
    private AppConfig appConfig;
    private LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();

    //To get executor which writng the log information
    private void GetFunctionTrigger()
    {
        //a class -> PrintError -> GetFunctionTrigger -> Logger class
        //so here is 3
        this.handler = Thread.currentThread().getStackTrace()[3].getClassName();
    }

    @PostConstruct
    private void init()
    {
        new Thread(()->{
            try {
                while (!Thread.currentThread().isInterrupted())
                {
                    String logPath = appConfig.getMessageLogPath();
                    String logName = DateOperation.GetLocalDate() + ".log";
                    String fullmsg = this.queue.poll();
                    if(fullmsg==null || fullmsg.isEmpty() || fullmsg.isBlank())
                    {
                        continue;
                    }
                    IOOperation.SaveFile(logPath,logName,fullmsg);
                }
            } catch (Exception e) {
                PrintError(e);
            }
        }).start();
    }

    private void PrintMsg(String level,String message)
    {
        String fullMessage = "["+ DateOperation.GetLocalTime() +"]["+this.handler+"]" + "["+level+"]" + ": "+ message;
        System.out.println(fullMessage);
        this.queue.add(fullMessage);
    }

    public void PrintInfo(String message)
    {
        //get the trigger of the function
        this.GetFunctionTrigger();
        PrintMsg("INFO",message);
    }

    public void PrintError(String message)
    {
        //get the trigger of the function
        this.GetFunctionTrigger();
        PrintMsg("ERROR",message);
    }

    public void PrintError(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        //get the trigger of the function
        this.GetFunctionTrigger();
        PrintMsg("ERROR",sw.toString());
    }

    public void PrintDebug(String message)
    {
        //get the trigger of the function
        this.GetFunctionTrigger();
        PrintMsg("DEBUG",message);
    }
}
