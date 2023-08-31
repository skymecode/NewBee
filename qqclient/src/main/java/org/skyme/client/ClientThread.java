package org.skyme.client;

import org.skyme.core.Message;
import org.skyme.dto.AddFriend;
import org.skyme.dto.GroupUser;
import org.skyme.core.MessageType;
import org.skyme.entity.QQGroupMessage;
import org.skyme.entity.QQMessage;
import org.skyme.entity.User;
import org.skyme.ui.*;
import org.skyme.util.NIOObjectUtil;

import org.skyme.vo.FriendHistory;
import org.skyme.vo.FriendList;
import org.skyme.vo.GroupList;

import javax.swing.*;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;

import static javax.swing.JOptionPane.DEFAULT_OPTION;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;

/**
 * @author:Skyme
 * @create: 2023-08-18 09:31
 * @Description:
 */
public class ClientThread extends Thread {
    private Surface surface;

    private Login login;

    private InfoWindowApp infoWindowApp;

    private Selector selector;

    private SocketChannel socket;
    private ChatWindowApp chatWindowApp;

    public Surface getSurface() {
        return surface;
    }

    public void setSurface(Surface surface) {
        this.surface = surface;
    }

    public SocketChannel getSocket() {
        return socket;
    }

    public void setSocket(SocketChannel socket) {
        this.socket = socket;
    }

    public Login getLogin() {
        return login;
    }

    public void setLogin(Login login) {
        this.login = login;
    }

    public ChatWindowApp getChatWindowApp() {
        return chatWindowApp;
    }

    private GroupWindowApp groupWindowApp;

    public GroupWindowApp getGroupWindowApp() {
        return groupWindowApp;
    }

    public void setGroupWindowApp(GroupWindowApp groupWindowApp) {
        this.groupWindowApp = groupWindowApp;
    }

    public void setChatWindowApp(ChatWindowApp chatWindowApp) {
        this.chatWindowApp = chatWindowApp;
    }

    @Override
    public void run() {
        System.out.println("接收线程创建成功");
        try {
            socket.configureBlocking(false);
            selector = Selector.open();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {

            socket.register(selector, SelectionKey.OP_READ);
        } catch (ClosedChannelException e) {
            System.out.println("注册失败");
            throw new RuntimeException(e);
        }
        Set<SelectionKey> keys=null;
        while (true) {
            int select = 0;
            try {
                select = selector.select();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if(select==0){
                continue;
            }
            keys = selector.selectedKeys();
            for (SelectionKey key : keys) {
                if(key.isReadable()){
                    try {

                        //接收消息,没消息就阻塞(无法根据对方是谁进行数据接收)
                        Message message = null;
                        try {
                            message = (Message)NIOObjectUtil.readObjectFromChannel(socket);

                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                        System.out.println("接收到来自服务器的消息");
                        if(message==null) {
                            continue;
                        }
                        MessageType type = message.getType();
                        System.out.println(type);
                        //说明接收到的时候好友消息
                        HashMap<Long, ChatWindowApp> map=null;
                        if(surface!=null){
                            map= surface.getMap();
                        }

                        if (type == MessageType.RECEIVE_RESULT) {
                            QQMessage date = (QQMessage) message.getData();
                            String content = date.getContent();

                            //根据接收到数据进行加入
                            ChatWindowApp chatWindowApp1 = map.getOrDefault(date.getFromUid(), null);
                            if (chatWindowApp1 == null) {
                                System.out.println("请求查询好友列表!!");
                                Message m = new Message<>();
                                m.setData(surface.getUser());
                                m.setCode(1);
                                m.setMes("请求查询好友列表");
                                m.setType(MessageType.FRIENDS_LIST);
                                try {
                                    NIOObjectUtil.writeObjectToChannel(m,socket);
//                            ObjectUtil.sendObject(socket,m);
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
                                continue;
                            }
                            //得到最新消息
                            Message mes = new Message<>();
                            QQMessage qqMessage = new QQMessage();
                            System.out.println("发送者:"+date.getSendUid());
                            qqMessage.setFromUid(date.getSendUid());
                            System.out.println("需要接收的"+date.getFromUid());
                            qqMessage.setSendUid(date.getFromUid());
                            mes.setData(qqMessage);
                            mes.setType(MessageType.MES_HISTORY);
                            try {
                                NIOObjectUtil.writeObjectToChannel(mes,socket);
//                        ObjectUtil.sendObject(socket,mes);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            JTextArea chatTextArea = chatWindowApp1.getChatTextArea();
                            System.out.println("chatWindowApp:" + chatWindowApp1);
                            if (!content.isEmpty()) {
                                chatTextArea.append(chatWindowApp1.getFirstUser().getNickname() + ": " + content + "\n");
                            }
                        } else if (type == MessageType.MES_HISTORY_RESULT) {
                            FriendHistory history = (FriendHistory) message.getData();
                            ChatWindowApp chatWindowApp1 = map.get(history.getSendId());
                            List<QQMessage> list = history.getList();
                            JTextArea chatTextArea = chatWindowApp1.getChatTextArea();
                            chatTextArea.setText("");
                            for (int i = 0; i < list.size(); i++) {
                                if (list.get(i).getFromUid().equals(chatWindowApp1.getUser().getUid())) {
                                    if(list.get(i).getFile()!=null){
                                        chatTextArea.append(list.get(i).getSendtime()+"\n"+chatWindowApp1.getUser().getNickname() + ": " + list.get(i).getFile()+ "\n");
                                        chatTextArea.append("\n");
                                    }else{
                                        chatTextArea.append(list.get(i).getSendtime()+"\n"+chatWindowApp1.getUser().getNickname() + ": " + list.get(i).getContent() + "\n");
                                        chatTextArea.append("\n");
                                    }

                                } else {
                                    if(list.get(i).getFile()!=null){
                                        chatTextArea.append(list.get(i).getSendtime()+"\n"+chatWindowApp1.getFirstUser().getNickname() + ": " + list.get(i).getFile()+ "\n");
                                        chatTextArea.append("\n");
                                    }else{
                                        chatTextArea.append(list.get(i).getSendtime()+"\n"+chatWindowApp1.getFirstUser().getNickname()+ ": " + list.get(i).getContent() + "\n");
                                        chatTextArea.append("\n");
                                    }
                                }
                            }
                        }else if(type==MessageType.GROUP_LIST_RESULT){
                            List<GroupList> list = (List<GroupList>) message.getData();
                            //将上述里面的用户信息添加到list里面
                            HashMap<Long, GroupList> groupUidMap = surface.getGroupUidMap();
                            DefaultListModel<GroupList> groupModel = surface.getGroupModel();
                            groupModel.removeAllElements();
                            for (int i = 0; i < list.size(); i++) {
                                groupUidMap.put(list.get(i).getGroup().getGid(),list.get(i));//将好友放入到本地缓存当中
                                groupModel.addElement(list.get(i));
//
                            }
//
//
                        }
                        else if (type == MessageType.LOGOUT_RESULT) {
                            if(message.getData()!=null&&message.getData().equals(getSurface().getUser().getUid())){
                                //说明是自己
                                socket.close();//关闭通道
                                System.exit(0);
                            }

                            Message m = new Message<>();
                            m.setData(surface.getUser());
                            m.setCode(1);
                            m.setMes("请求查询好友列表");
                            m.setType(MessageType.FRIENDS_LIST);
                            try {
                                NIOObjectUtil.writeObjectToChannel(m,socket);
//                        ObjectUtil.sendObject(socket, m);
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
//
                        } else if (type == MessageType.DELETEED_RESULT) {
                            System.out.println("接收到好友被删除的信息");
                            Message<User> m = new Message<>();
                            m.setData(surface.getUser());
                            m.setCode(1);
                            m.setMes("请求查询好友列表");
                            m.setType(MessageType.FRIENDS_LIST);
                            try {
                                System.out.println("向服务器发送好友查询功能");
                                NIOObjectUtil.writeObjectToChannel(m,socket);
//                        ObjectUtil.sendObject(socket, m);
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }

//


                        }else if (type==MessageType.QUERY_LIKE_USER_RESULT){
//                    List<String> matchingUsers = surface.getMatchingUsers();
                            List<User> data = (List<User>) message.getData();
                            System.out.println("data"+data);
                            surface.setMatchingUsers((List<User>) message.getData());
                            List<String> matchingUserNicknames;
                            matchingUserNicknames = new ArrayList<>();
                            surface.setMatchingUserNicknames(matchingUserNicknames);
                            for (User user : surface.getMatchingUsers()) {
                                matchingUserNicknames.add(user.getNickname());
                            }System.out.println(surface.getMatchingUsers());
                        }else if(type==MessageType.ADD_FRIEND_RESULT){
                            //弹出好友添加方框
                            //这里用一个确定或者否来判断逻辑
                            //如果接受,那么执行一个accept那么就返回false,这个服务器根据这个具体来判断是否添加,并且通知对方消息即可
                            //
                            User user1 = (User) message.getData();
                            int choice = JOptionPane.showConfirmDialog(
                                    surface.getContentPane(),
                                    user1.getNickname(),
                                    "好友请求",
                                    JOptionPane.YES_NO_OPTION);

                            if (choice == JOptionPane.YES_OPTION) {
                                // 用户确认好友请求，执行接受操作
                                // 在这里执行接受好友请求的逻辑
                                // 可以发送确认消息给服务器，更新数据库等操作
                                Message message1 = new Message();

                                message1.setType(MessageType.ACCEPT_FRIEND);
                                AddFriend addFriend = new AddFriend();
                                addFriend.setFromUser(surface.getUser());
                                addFriend.setSendUser(user1);
                                message1.setData(addFriend);
                                NIOObjectUtil.writeObjectToChannel(message1,socket);
//                        ObjectUtil.sendObject(socket,message1);
                                JOptionPane.showMessageDialog(
                                        surface.getContentPane(),
                                        "你已接受 " + user1.getNickname()  + " 的好友请求。",
                                        "好友请求已接受",
                                        JOptionPane.INFORMATION_MESSAGE);
                                Message m = new Message<>();
                                m.setData(surface.getUser());
                                m.setCode(1);
                                m.setMes("请求查询好友列表");
                                m.setType(MessageType.FRIENDS_LIST);
                                try {
                                    System.out.println("接受方向服务器发送因为好友请求而刷新好友列表的消息");
                                    NIOObjectUtil.writeObjectToChannel(m,socket);
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
//

                            } else if (choice == JOptionPane.NO_OPTION) {
                                // 用户拒绝好友请求，执行拒绝操作
                                // 在这里执行拒绝好友请求的逻辑
                                // 可以发送拒绝消息给服务器，更新数据库等操作
                                JOptionPane.showMessageDialog(
                                        surface.getContentPane(),
                                        "你已拒绝 " + user1.getNickname()  + " 的好友请求。",
                                        "好友请求已拒绝",
                                        JOptionPane.INFORMATION_MESSAGE);
                            }

                        }else if(type==MessageType.SUCCESS_ADD_FRIEND_RESULT){
                            AddFriend addFriend = (AddFriend) message.getData();
                            JOptionPane.showMessageDialog(
                                    surface.getContentPane(),
                                    addFriend.getFromUser().getNickname()+"接受了你的好友请求",
                                    "好友添加通知",
                                    JOptionPane.INFORMATION_MESSAGE);
                            Message m = new Message<>();
                            m.setData(surface.getUser());
                            m.setCode(1);
                            m.setMes("请求查询好友列表");
                            m.setType(MessageType.FRIENDS_LIST);
                            try {
                                System.out.println("向服务器发送因为好友请求而刷新好友列表的消息");
                                NIOObjectUtil.writeObjectToChannel(m,socket);
//                        ObjectUtil.sendObject(socket, m);
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        }else if(type==MessageType.FRIENDSLIST_RESULT){
                            List<FriendList> list = (List<FriendList>) message.getData();
                            DefaultListModel<FriendList> listModel = surface.getListModel();
                            //将上述里面的用户信息添加到list里面
                            listModel.removeAllElements();
                            for (int i = 0; i < list.size(); i++) {
                                HashMap<Long, FriendList> friendUidMap = surface.getFriendUidMap();
                                friendUidMap.put(list.get(i).getUser().getUid(), list.get(i));
                                listModel.addElement(list.get(i));
//					list.get(i).getUid()+":"+list.get(i).getNickname()
                            }

                        }else if (type==MessageType.GROUP_MESSAGE_HISTORY_RESULT){
                            List<QQGroupMessage> list= (List<QQGroupMessage>) message.getData();
                            //顺带将
                            Message groupMessage = new Message<>();
                            groupMessage.setData(getSurface().getUser());
                            groupMessage.setCode(1);
                            groupMessage.setMes("请求查询群聊列表");
                            groupMessage.setType(MessageType.GROUP_LIST);
                            try {
                                NIOObjectUtil.writeObjectToChannel(groupMessage,socket);
//                        ObjectUtil.sendObject(socket,groupMessage);
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                            GroupWindowApp groupWindowApp1 = getGroupWindowApp();
                            JTextArea chatArea = groupWindowApp1.getChatArea();
                            chatArea.setText("");
                            for (QQGroupMessage qqGroupMessage : list) {
                                String gContent = qqGroupMessage.getgContent();
                                User user = qqGroupMessage.getUser();
                                chatArea.append(qqGroupMessage.getgCreateTime()+"\n"+user.getNickname()+":"+gContent+"\n");
                                chatArea.append("\n");
                            }


                        }else if(type==MessageType.GROUP_SEND_RESULT){
                            //刷新自己的群列表或对话框->
                            //如果这个群的窗口打开了,那么直接就刷新到群对话框中,否则刷新群消息列表
                            QQGroupMessage groupMessage = (QQGroupMessage) message.getData();
                            User user = groupMessage.getUser();
                            String gContent = groupMessage.getgContent();
                            Long gid = groupMessage.getGid();
                            if(Objects.equals(user.getUid(), surface.getUser().getUid())){
                                continue;
                            }
                            HashMap<Long, GroupWindowApp> openGroup = surface.getOpenGroup();
                            if(openGroup.containsKey(gid)){
                                Message message1 = new Message<>();
                                GroupUser groupUser = new GroupUser();
                                groupUser.setGid(gid);
                                groupUser.setUid(getSurface().getUser().getUid());
                                message1.setData(groupUser);
                                message1.setType(MessageType.GROUP_MESSAGE_HISTORY);
                                try {
                                    NIOObjectUtil.writeObjectToChannel(message1,socket);
//                            ObjectUtil.sendObject(socket,message1);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }else{
                                Message gMes = new Message<>();
                                gMes.setData(surface.getUser());
                                gMes.setCode(1);
                                gMes.setMes("请求查询群聊列表");
                                gMes.setType(MessageType.GROUP_LIST);
                                try {
                                    NIOObjectUtil.writeObjectToChannel(gMes,socket);
//                            ObjectUtil.sendObject(socket,gMes);
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                        }else if(type==MessageType.CREATE_OR_JOIN_GROUP_RESULT){
                            int code = message.getCode();
                            Surface surface1 = getSurface();
//                    JPanel contentPane = surface1.getContentPane();

                            if(code==0){
                                //弹窗

                                JOptionPane.showMessageDialog(null, message.getMes(), "消息", JOptionPane.INFORMATION_MESSAGE);


                            }else{
                                //更新列表

                                //顺带将
                                Message<Object> groupMessage = new Message<>();
                                groupMessage.setData(getSurface().getUser());
                                groupMessage.setCode(1);
                                groupMessage.setMes("请求查询群聊列表");
                                groupMessage.setType(MessageType.GROUP_LIST);

                                try {
                                    NIOObjectUtil.writeObjectToChannel(groupMessage,socket);
//                            ObjectUtil.sendObject(socket,groupMessage);
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
                                if(code==2){
                                    //进群成功
                                    JOptionPane.showMessageDialog(null, message.getMes(), "消息", JOptionPane.INFORMATION_MESSAGE);
                                }else if(code==1){
                                    //创建群聊成功
                                    JOptionPane.showMessageDialog(null, message.getMes(), "消息", JOptionPane.INFORMATION_MESSAGE);
                                }


                            }
                        }else if(type==MessageType.QUIT_GROUP_RESULT){
                            JOptionPane.showMessageDialog(null, message.getMes(), "消息", JOptionPane.INFORMATION_MESSAGE);
                        } else if (type==MessageType.LOGOUT_RESULT) {
                            if(message .getCode()==1){
                                JOptionPane.showConfirmDialog(null,message .getMes(),"消息提示",DEFAULT_OPTION,INFORMATION_MESSAGE);
                                System.out.println("执行登录");

                                login.openFriendListWindow((User) message.getData());
                                //启动线程监听好友列表
                            }else{
                                JOptionPane.showConfirmDialog(null,message .getMes(),"消息提示",DEFAULT_OPTION,INFORMATION_MESSAGE);
                            }
                        }else if(type==MessageType.LOG_RESULT){

                            if(message.getCode()==1){
                                JOptionPane.showMessageDialog(null,message .getMes(),"消息提示",DEFAULT_OPTION);
                                System.out.println("执行登录");
                                getLogin().openFriendListWindow((User) message.getData());
                                //启动线程监听好友列表
                            }else{
                                JOptionPane.showMessageDialog(null,message.getMes(),"消息提示",DEFAULT_OPTION);
                                getLogin().setVisible(true);
                            }

                        }else if(type==MessageType.INFO_RESULT){

                            DefaultListModel<User> requestListModel = infoWindowApp.getRequestListModel();
                            List<User> list = (List<User>) message.getData();
                            if(list != null){
                            for (User user : list) {
                                requestListModel.addElement(user);
                            }
                            }

                        }else if(type==MessageType.REG_RESULT){
                            if(message.getCode()==1){
                                JOptionPane.showMessageDialog(null,message.getMes(),"消息提示",DEFAULT_OPTION);
                            }else {
                                JOptionPane.showMessageDialog(null,message.getMes(),"消息提示",DEFAULT_OPTION);
                            }
                        }else if(type==MessageType.QUERY_GROUP_MEMBER_RESULT){
                            JList<User> memberList = groupWindowApp.getMemberList();
                            DefaultListModel<User> model = (DefaultListModel<User>) memberList.getModel();
                            model.removeAllElements();
                            List<User> list= (List<User>)message.getData();
                            for (User user : list) {
                                model.addElement(user);
                            }
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

            }
            keys.clear();



        }
    }


    public InfoWindowApp getInfoWindowApp() {
        return infoWindowApp;
    }

    public void setInfoWindowApp(InfoWindowApp infoWindowApp) {
        this.infoWindowApp = infoWindowApp;
    }
}
