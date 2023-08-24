package org.skyme.qq.core;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Set;

/**
 * @author:Skyme
 * @create: 2023-08-23 15:56
 * @Description:
 */
public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress(8088));
        server.configureBlocking(false);
        Selector selector = Selector.open();
        server.register(selector, SelectionKey.OP_ACCEPT);
        while(true){
            int select = selector.select();
            if(select==0){
                continue;
            }
            Set<SelectionKey> selectedKeys = selector.selectedKeys();

            for (SelectionKey key : selectedKeys) {
                if(key.isAcceptable()){
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel accept = channel.accept();
                    accept.configureBlocking(false);
                    accept.register(selector,SelectionKey.OP_READ);

                }else if(key.isReadable()){
                    SocketChannel client = (SocketChannel)key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    if(client.read(buffer)>0){
                        buffer.flip();
                        System.out.println(new String(buffer.array(), 0, buffer.remaining()));
                        buffer.clear();
                    }
                }
                selectedKeys.remove(key);
            }


        }
    }
}
