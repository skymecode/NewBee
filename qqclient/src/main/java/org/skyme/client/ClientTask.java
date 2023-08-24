//package org.skyme.client;
//
//import org.skyme.dto.Message;
//import org.skyme.ui.ChatWindowApp;
//import org.skyme.ui.Surface;
//
//
//import java.io.IOException;
//import java.net.Socket;
//
///**
// * @author:Skyme
// * @create: 2023-08-17 21:24
// * @Description: 负责消息监听(通知各个好友)和好友列表刷新(下线通过通知各个好友)
// */
//public class ClientTask implements Runnable {
//
//    private Surface surface;
//
//    private Socket socket;
//    private ChatWindowApp chatWindowApp;
//
//    public ClientTask(Socket socket,Surface surface) {
//        this.surface = surface;
//        this.socket = socket;
//    }
//
//    public ClientTask(Surface surface, Socket socket, ChatWindowApp chatWindowApp) {
//        this.surface = surface;
//        this.socket = socket;
//        this.chatWindowApp = chatWindowApp;
//    }
//
//    @Override
//    public void run() {
//        while (true){
//            try {
//                //接收消息,没消息就阻塞
////                Message message = (Message) ObjectUtil.getObject(socket);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            } catch (ClassNotFoundException e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }
//}
