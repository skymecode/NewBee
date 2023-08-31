package org.skyme.ui.reconnect;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Properties;

/**
 * @author:Skyme
 * @create: 2023-08-30 22:41
 * @Description:
 */
public class Reconnect {
    private static Properties properties;
    //判断是否重连
    public static void reConnect(SocketChannel socketChannel) throws IOException {
        if(!socketChannel.isConnected()){
            JOptionPane.showMessageDialog(null,"连接已经断开,需要重新登录");
          System.exit(0);

        }


    }
}
