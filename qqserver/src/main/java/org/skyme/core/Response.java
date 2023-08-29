package org.skyme.core;

import org.skyme.entity.User;
import org.skyme.util.NIOObjectUtil;

import org.skyme.vo.FriendList;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Map;

/**
 * @author:Skyme
 * @create: 2023-08-17 16:13
 * @Description:响应
 */
public class Response {
    private Server server;

    private SocketChannel socket;

    public Response(Server server, SocketChannel socket) {
        this.server = server;
        this.socket = socket;
    }

    //上线
    public void online(Long uid, List<User> friends){
        Map<Long, SocketChannel> onlineUsers = server.getOnlineUsers();
        onlineUsers.put(uid, socket);
        for (int i = 0; i < friends.size(); i++) {
            Long uid1 = friends.get(i).getUid();
            if(isOnline(uid1)){
                //通知更新好友列表
                Message message = new Message();
                message.setMes("好友上线");
                message.setType(MessageType.LOGOUT_RESULT);
                message.setData(null);
                message.setCode(1);
                try {
                    NIOObjectUtil.writeObjectToChannel(message,onlineUsers.get(uid1));
//                    ObjectUtil.sendObject(onlineUsers.get(uid1),message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    //判断是否在线
    public boolean isOnline(Long uid) {
        boolean b = server.getOnlineUsers().containsKey(uid);
        return b;
    }
    //发送消息给在线的人
    public int sendMessage(Long uid, Message message) throws IOException {
        Map<Long, SocketChannel> onlineUsers = server.getOnlineUsers();
        SocketChannel socket1 = onlineUsers.get(uid);
//        OutputStream outputStream = null;
//        try {
//            outputStream = socket1.getOutputStream();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        message.setType(MessageType.RECEIVE_RESULT);
        NIOObjectUtil.writeObjectToChannel(message,socket1);
        System.out.println("发送给"+uid+"成功");
        return 1;
    }
    public int sendAddMessage(Long uid, Message message) throws IOException {
        Map<Long, SocketChannel> onlineUsers = server.getOnlineUsers();
        SocketChannel socket1 = onlineUsers.get(uid);
//        OutputStream outputStream = null;
//        try {
//            outputStream = socket1.getOutputStream();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        message.setType(MessageType.ADD_FRIEND_RESULT);
        NIOObjectUtil.writeObjectToChannel(message,socket1);
//        objectOutputStream.writeObject(message);
        System.out.println("发送给"+uid+"成功");
        return 1;
    }
    public int sendAcceptedMessage(Long uid, Message message) throws IOException {
        Map<Long, SocketChannel> onlineUsers = server.getOnlineUsers();
        SocketChannel socket1 = onlineUsers.get(uid);
//        OutputStream outputStream = null;
//        try {
//            outputStream = socket1.getOutputStream();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        message.setType(MessageType.SUCCESS_ADD_FRIEND_RESULT);
        NIOObjectUtil.writeObjectToChannel(message,socket1);
//        objectOutputStream.writeObject(message);
        System.out.println("发送给"+uid+"成功");
        return 1;
    }

    public int logout(Long uid, List<User> friends) throws IOException {
        //还要通知其他的在线用户其实
        Map<Long, SocketChannel> onlineUsers = server.getOnlineUsers();
        SocketChannel socket1 = onlineUsers.remove(uid);
        for (int i = 0; i < friends.size(); i++) {
            Long uid1 = friends.get(i).getUid();
            if(isOnline(uid1)){
                //通知更新好友列表
                Message message = new Message();
                message.setMes("好友下线");
                message.setType(MessageType.LOGOUT_RESULT);
                message.setData(uid);
                message.setCode(1);
                NIOObjectUtil.writeObjectToChannel(message,onlineUsers.get(uid1));
//                ObjectUtil.sendObject(onlineUsers.get(uid1),message);
            }
        }

        return 1;
    }
    public int flushFriendList(Long uid, List<FriendList> lists) throws IOException {
        //还要通知其他的在线用户其实
        Map<Long, SocketChannel> onlineUsers = server.getOnlineUsers();
        SocketChannel socket1 = onlineUsers.get(uid);
        Message message = new Message();
        message.setMes("刷新列表");
        message.setType(MessageType.FRIENDSLIST_RESULT);
        message.setData(lists);
        message.setCode(1);
        NIOObjectUtil.writeObjectToChannel(message,socket1);
//        ObjectUtil.sendObject(socket,message);

        return 1;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public SocketChannel getSocket() {
        return socket;
    }

    public void setSocket(SocketChannel socket) {
        this.socket = socket;
    }

    public void deleteFriend(User user, Long uid) throws IOException {
        Map<Long, SocketChannel> onlineUsers = server.getOnlineUsers();
        Message message = new Message();
        message.setMes("已经被好友"+user.getNickname()+"删除");
        message.setType(MessageType.DELETEED_RESULT);
        message.setData(null);
        message.setCode(1);
        NIOObjectUtil.writeObjectToChannel(message,onlineUsers.get(uid));
    }

    public void sendGroupMessage(Long uid, Message message) {
        //发送给群成员
        Map<Long, SocketChannel> onlineUsers = server.getOnlineUsers();
        SocketChannel socket1 = onlineUsers.get(uid);
        try {
            NIOObjectUtil.writeObjectToChannel(message,socket1);
//            ObjectUtil.sendObject(socket1,message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void flushList(Long uid) {
    }
}
