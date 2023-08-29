package org.skyme.util;

import org.skyme.core.Message;

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
        ByteBuffer buffer = ByteBuffer.allocate(1024);

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
//        int bytesRead = channel.read(buffer);
//        if (bytesRead >0) {
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
                    System.out.println("等于");
                    Message obj = (Message) receivedObj;

                    return obj;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }


        return null;
    }
    public static void writeObjectToChannel(Object object, SocketChannel channel) throws IOException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(object);
//            objectOutputStream.flush();
        }
        ByteBuffer buffer = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
        channel.write(buffer);
//        channel.socket().getOutputStream().flush();

    }


}
