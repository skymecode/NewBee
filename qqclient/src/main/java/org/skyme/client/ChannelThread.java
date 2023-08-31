package org.skyme.client;

import javax.swing.*;
import java.nio.channels.SocketChannel;

/**
 * @author:Skyme
 * @create: 2023-08-31 00:22
 * @Description:
 */
public class ChannelThread extends Thread{
    private SocketChannel socketChannel;

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
    public void run() {

        //负责监听管道是否关闭
        while (true){
            if(!socketChannel.isConnected()){
                JOptionPane.showMessageDialog(null,"网络连接出错,请重新登录");
                System.exit(0);
            }

        }
    }
}
