package socket;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import org.springframework.stereotype.Component;
import broker.SimpleMQBroker;

@Component
public class MessageSocketServer {
    private final int port = 9090;  // Socket服务端口
    private final SimpleMQBroker broker;
    private volatile boolean running = true;
    
    public MessageSocketServer(SimpleMQBroker broker) {
        this.broker = broker;
    }
    
    public void start() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                while (running) {
                    Socket clientSocket = serverSocket.accept();
                    // 为每个客户端创建一个处理线程
                    new Thread(new SocketClientHandler(clientSocket, broker)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
} 