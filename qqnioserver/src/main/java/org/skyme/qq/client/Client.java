package org.skyme.qq.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * @author:Skyme
 * @create: 2023-08-23 16:04
 * @Description:
 */
public class Client {
    public static void main(String[] args) throws IOException {
        SocketChannel open = SocketChannel.open();
        boolean connect = open.connect(new InetSocketAddress("127.0.0.1", 8088));
        if(!connect){
            while (open.finishConnect()){
                open.connect(new InetSocketAddress("127.0.0.1", 8088));
            }
        }
        String s="客户端xxx";
        ByteBuffer wrap = ByteBuffer.wrap(s.getBytes(StandardCharsets.UTF_8));
        open.write(wrap);
        open.close();



    }
}
