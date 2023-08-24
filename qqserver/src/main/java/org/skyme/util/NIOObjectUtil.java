package org.skyme.util;

import org.skyme.dto.Message;

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
        ByteBuffer buffer = ByteBuffer.allocate(102400); // Adjust buffer size
        int bytesRead = channel.read(buffer);
        if (bytesRead != -1) {
            buffer.flip();

            // Deserialize the object from the buffer
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(buffer.array()))) {
                Object receivedObj = objectInputStream.readObject();
                if (receivedObj instanceof Message) {
                    Message obj = (Message) receivedObj;
                    return obj;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }else{
            channel.close();
        }
        return null;
    }
    public static void writeObjectToChannel(Object object, SocketChannel channel) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(object);
        }
        // Write the serialized byte array to the socket channel
        ByteBuffer buffer = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
        channel.write(buffer);

    }


}
