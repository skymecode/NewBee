package org.skyme.util;

import org.skyme.core.Message;
import org.skyme.ui.reconnect.Reconnect;

import javax.swing.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author:Skyme
 * @create: 2023-08-23 16:17
 * @Description:
 */
public class NIOObjectUtil {
    public static Object readObjectFromChannel(SocketChannel channel) throws IOException, ClassNotFoundException {
        //判断channel是否是关闭的,如果关闭则跳转到登录界面
        Reconnect.reConnect(channel);

        ByteBuffer buffer = ByteBuffer.allocate(128400);
        int len=-1;
        int count = 0;
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        while ((len=channel.read(buffer))>0){
            System.out.println("接收到");
            buffer.flip();
            count+=len;
            bytesOut.write(buffer.array(), 0, len);
            buffer.clear();
        }
        if(len==-1&&count==0){
            channel.close();
            throw  new RuntimeException("接受的请求为空!");
        }else if (len==0&&count==0){
            System.out.println("len为0");
            return null;
        }
        else{

            System.out.println("count"+count);
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytesOut.toByteArray()))) {

                Object receivedObj = objectInputStream.readObject();
                if (receivedObj instanceof Message) {
                    Message obj = (Message) receivedObj;
                    return obj;
                }
            } catch (Exception e) {
                if(e instanceof  EOFException){
                    JOptionPane.showMessageDialog(null,"网络检测异常,请重新操作");
                }
                e.printStackTrace();
            }
        }
        return null;
    }
    public static void writeObjectToChannel(Object object, SocketChannel channel) throws IOException {
        Reconnect.reConnect(channel);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(object);
        }
        ByteBuffer buffer = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
        channel.write(buffer);


    }


}
