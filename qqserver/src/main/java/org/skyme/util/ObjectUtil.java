//package org.skyme.util;
//
//import java.io.*;
//import java.net.Socket;
//
///**
// * @author:Skyme
// * @create: 2023-08-17 14:59
// * @Description:
// */
//public class ObjectUtil {
//    //发送相关类
//    public static void sendObject(Socket socket, Object obj) throws IOException {
//        OutputStream outputStream = socket.getOutputStream();
//        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
//        objectOutputStream.writeObject(obj);
//        objectOutputStream.flush();
//    }
//    //接收相关类
//    public static Object getObject(Socket socket) throws IOException, ClassNotFoundException {
//        InputStream in= socket.getInputStream();
//        ObjectInputStream oi = new ObjectInputStream(in);
//        Object o = oi.readObject();
//        return o;
//
//    }
//
//}
