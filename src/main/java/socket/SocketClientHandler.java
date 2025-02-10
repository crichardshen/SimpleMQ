package socket;

import java.net.Socket;
import java.io.*;
import message.Message;
import broker.SimpleMQBroker;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SocketClientHandler implements Runnable {
    private final Socket clientSocket;
    private final SimpleMQBroker broker;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // 增加缓冲区大小
    private static final int BUFFER_SIZE = 4096;  // 4KB缓冲
    
    public SocketClientHandler(Socket socket, SimpleMQBroker broker) {
        this.clientSocket = socket;
        this.broker = broker;
    }
    
    @Override
    public void run() {
        try (
            BufferedInputStream buffIn = new BufferedInputStream(
                clientSocket.getInputStream(), BUFFER_SIZE);
            DataInputStream in = new DataInputStream(buffIn)
        ) {
            while (true) {
                // 读取消息类型
                byte messageType = in.readByte();
                
                if (messageType == 1) {  // 二进制消息
                    // 读取主题
                    String topic = in.readUTF();
                    // 读取消息长度
                    int length = in.readInt();
                    // 读取消息内容
                    byte[] data = new byte[length];
                    in.readFully(data);
                    
                    // 创建消息并发布
                    Message message = new Message();
                    message.getHeader().setTopic(topic);
                    message.setBody(data);
                    broker.publish(message);
                    
                    // 发送确认
                    out.writeBoolean(true);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 