package org.skyme.core;

import org.skyme.util.XmlUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author:Skyme
 * @create: 2023-08-17 10:53
 * @Description:
 */
public class Server {
    private Properties properties ;

    private ExecutorService fileExecutorService;

    private ServerSocket serverSocket; //Socket
    private ExecutorService executorService;//线程池
    private Map<Long, SocketChannel> onlineUsers;//在线表
    private final HashMap<String,String> servletMap=new HashMap<>();
    private final HashMap<String,String> servletToContollerMap = new HashMap<>();
    private HashMap<String,Object> controllers=new HashMap<>();
    public Map<Long, SocketChannel> getOnlineUsers() {
        return onlineUsers;
    }

    public void setOnlineUsers(Map<Long, SocketChannel> onlineUsers) {
        this.onlineUsers = onlineUsers;
    }

    public  void init() throws IOException {
        fileExecutorService = Executors.newFixedThreadPool(25);
        ServerSocket serverSocket = new ServerSocket(8888);
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    while(true){
                        Socket accept = serverSocket.accept();
                        fileExecutorService.submit(new FileThread(accept));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        String path="F:\\project\\QQNIO\\qqserver\\src\\main\\resources\\server.properties";

        InputStream inputStream =new FileInputStream(path);

        try{
            properties=new Properties();
            properties.load(inputStream);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
        String property = properties.getProperty("port");
        int port = Integer.parseInt(property);

        onlineUsers=new ConcurrentHashMap<>();
        executorService= Executors.newFixedThreadPool(25);
        List<String> servletPaths = XmlUtil.getServletPaths();
        List<String> servletName = XmlUtil.getServletName();
        List<String> names = XmlUtil.servletName();
        List<String> servletClass = XmlUtil.getServletClass();
        //启动服务器时,得到对应的Servlet映射(方便后续客户端访问服务器)
        for (int i = 0; i < servletPaths.size(); i++) {
            servletMap.put(servletPaths.get(i),servletName.get(i));
        }
        for (int i = 0; i < names.size(); i++) {
            servletToContollerMap.put(names.get(i),servletClass.get(i));
        }
        ServerSocketChannel server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress(port));
        server.configureBlocking(false);
        Selector selector = Selector.open();
        server.register(selector, SelectionKey.OP_ACCEPT);
        Set<SelectionKey> selectedKeys=null;
        while(true){
            int select = selector.select();
            if(select==0){
                continue;
            }
            selectedKeys= selector.selectedKeys();
            for (SelectionKey key : selectedKeys) {
                if(key.isAcceptable()){
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel accept = channel.accept();
                    accept.configureBlocking(false);
                    accept.register(selector,SelectionKey.OP_READ);
                }else if(key.isReadable()){
                    SocketChannel client = (SocketChannel)key.channel();
                    if(client.isOpen()){
                    executorService.execute(new ServerTask(this,client, servletMap, servletToContollerMap));
                    }else{
                        key.cancel();
                        client.close();
                    }
                    }

                }
            selectedKeys.clear();
            }


        }





    public static void main(String[] args) throws IOException {
        new Server().init();

    }

}
