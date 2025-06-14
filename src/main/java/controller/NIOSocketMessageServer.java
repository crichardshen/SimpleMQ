package com.SimpleMQ.Controller;

import com.SimpleMQ.Broker.MQBroker;
import com.SimpleMQ.Config.AppConfig;
import com.SimpleMQ.Session.SocketSessionManager;
import com.SimpleMQ.Util.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.expression.spel.ast.Selection;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Set;

@Component
public class NIOSocketMessageServer {
    private final MQBroker messageHub; // manage message
    private final SocketSessionManager sessionManager;
    private final AppConfig appConfig;

    @Autowired
    private Logger logger;

    public NIOSocketMessageServer(MQBroker messageHub, SocketSessionManager sessionManager, AppConfig appConfig) {
        this.messageHub = messageHub;
        this.sessionManager = sessionManager;
        this.appConfig = appConfig;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start()
    {
        ServerSocketChannel nioSocketServer;
        {
//            System.out.println("NIOSocketServer is started!");
//            try {
//                nioSocketServer = ServerSocketChannel.open();
//                nioSocketServer.configureBlocking(false); //不阻塞等待
//                nioSocketServer.bind(new InetSocketAddress(9092));
//
//                //阻塞等待
//                Selector selector = Selector.open();
//                nioSocketServer.register(selector, SelectionKey.OP_ACCEPT);
//
//                //SocketChannel socketChannel = nioSocketServer.accept();
//
//                while(true)
//                {
//                    selector.select();//block until some server is connected
//                    Set<SelectionKey> keys = selector.selectedKeys();
//                    for (SelectionKey key : keys) {
//                        if (key.isAcceptable()) {
//                            SocketChannel socketChannel = nioSocketServer.accept(); // 此时不会返回null
//                            socketChannel.configureBlocking(false);
//                            socketChannel.register(selector, SelectionKey.OP_READ);
//                        }
//                        else if(key.isReadable())
//                        {
//                            SocketChannel socketChannel =  (SocketChannel)key.channel();
//                            ByteBuffer byteBuffer = ByteBuffer.allocate(4);
//                            socketChannel.read(byteBuffer);
//                            byteBuffer.flip();
//                            int msgLen = byteBuffer.getInt();
//
//                            if(msgLen>0)
//                            {
//                                //socketChannel.register(selector,SelectionKey.OP_WRITE);
//                                byteBuffer = ByteBuffer.allocate(msgLen);
//                                socketChannel.read(byteBuffer);
//                                byte[] msg = byteBuffer.array();
//                                System.out.println("[RECEIVE] from " + new String(msg));
//                                key.interestOps(SelectionKey.OP_WRITE);
//                                //here can attch anything
//                                key.attach(new String(msg));
//                            }
//                        }
//                        else if(key.isWritable())
//                        {
//                            SocketChannel socketChannel = (SocketChannel) key.channel();
//                            String requestMsgBody = (String)key.attachment();
//
//                            byte[] response = ("[SEND]handshake ok with "
//                                        + requestMsgBody.split("->")[0]).getBytes();
//
//                            ByteBuffer byteBuffer = ByteBuffer.allocate(4+response.length);
//                            byteBuffer = ByteBuffer.allocate(4+response.length);
//                            byteBuffer.putInt(response.length);
//                            byteBuffer.put(response);
//                            byteBuffer.flip();
//                            socketChannel.write(byteBuffer);
//                            socketChannel.close();
//                            System.out.println("Message sent");
//                        }
//                    }
//                    keys.clear();
//                }
//
//
////                ByteBuffer byteBuffer = ByteBuffer.allocate(4);
////                int result = nioSocketServer.read(byteBuffer);
////                byteBuffer.clear();
////                int msgLen = byteBuffer.getInt();
////                byteBuffer = ByteBuffer.allocate(msgLen);
////                nioSocketServer.read(byteBuffer);
////                String msg = new String(byteBuffer.array());
////                System.out.println("Get the message from client : " + msg);
////
////                byteBuffer.clear();
////
////                byte[] responseMsg = "hand shake ok!".getBytes();
////                byteBuffer = ByteBuffer.allocate(4+responseMsg.length);
////                byteBuffer.putInt(responseMsg.length);
////                byteBuffer.put(responseMsg);
////                byteBuffer.flip();
////                nioSocketServer.write(byteBuffer);
////                System.out.println("response send out!");
////                nioSocketServer.close();
//
//
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
        }


    }




}
