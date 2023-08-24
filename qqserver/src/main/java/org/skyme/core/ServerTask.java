package org.skyme.core;

import org.skyme.dto.Message;
import org.skyme.dto.MessageType;
import org.skyme.util.NIOObjectUtil;
import org.skyme.vo.BaseResponse;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.SocketException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;

/**
 * @author:Skyme
 * @create: 2023-08-17 11:02
 * @Description:
 */
public class ServerTask implements Runnable{
    private SocketChannel socket;

    private Server server;

    private final HashMap<String,String> servletMap;

    private final HashMap<String,String> servletToContollerMap;

    public ServerTask(Server server,SocketChannel socket, HashMap<String, String> servletMap, HashMap<String, String> servletToContollerMap) {
        this.server=server;
        this.socket = socket;
        this.servletMap = servletMap;
        this.servletToContollerMap = servletToContollerMap;
    }

    @Override
    public void run() {
        //处理socket的消息请求

            try {
                if(socket.isOpen()){
                Object o = NIOObjectUtil.readObjectFromChannel(socket);
                Message mes= (Message) o;
                MessageType type = mes.getType();
                String path = type.getPath();
                String path1 = path.substring(0,path.indexOf("/",1));
                String method=path.substring(path.indexOf("/",1)+1);
                //解析path->转到UserController
                String s = servletMap.get(path1);
                String s1 = servletToContollerMap.get(s);
                System.out.println(s1);
                Class<?> aClass = Class.forName(s1);
                Method method1 = aClass.getMethod(method, Request.class,Response.class);
                Object o1 = aClass.newInstance();
                Response response = new Response(server, socket);
                Request request = new Request(mes);
                BaseResponse response1 = (BaseResponse) method1.invoke(o1, request,response);//拿到响应
                Message message = response1.getMessage();
                NIOObjectUtil.writeObjectToChannel(message,socket);
                System.out.println("接收数据后");
                if(message.getType()==MessageType.LOGOUT_RESULT){
                    socket.close();
                }
            } else{
                socket.close();
                }
            } catch (IOException | ClassNotFoundException | InvocationTargetException | InstantiationException |
                     NoSuchMethodException | IllegalAccessException e) {
             throw new RuntimeException(e);
            }
//       try {
//           socket.close();
//       } catch (IOException e) {
//           throw new RuntimeException(e);
//       }



    }

}
