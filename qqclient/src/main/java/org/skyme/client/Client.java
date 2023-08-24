package org.skyme.client;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.Properties;
import java.util.Scanner;

/**
 * @author:Skyme
 * @create: 2023-08-17 11:26
 * @Description:
 */
public class Client {
    private SocketChannel socket;
    private Properties properties;
    public void init() throws IOException {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("client.properties");
        try {
            properties=new Properties();
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String property = properties.getProperty("port");
        String ip = properties.getProperty("ip");
        int port = Integer.parseInt(property);
        SocketChannel open = SocketChannel.open();
        boolean connect = open.connect(new InetSocketAddress(ip, port));
        if(!connect){
            while (open.finishConnect()){
                open.connect(new InetSocketAddress(ip, port));
            }
        }
        setSocket(open);

    }

    public void setSocket(SocketChannel socket) {
        this.socket = socket;
    }

    public  SocketChannel getSocket() throws IOException {
        return socket;
    }

}
