package org.skyme.ui;

import org.skyme.core.Message;
import org.skyme.dto.AddFriend;
import org.skyme.core.MessageType;
import org.skyme.entity.User;
import org.skyme.util.NIOObjectUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.channels.SocketChannel;

public class InfoWindowApp extends JFrame {
        private DefaultListModel<User> requestListModel;
        private JList<User> requestList;

        private SocketChannel channel;

        private User user;

        private Surface surface;

        public InfoWindowApp() {
        }

        public InfoWindowApp(SocketChannel socket, User user, Surface surface) {
                this.channel = socket;
                this.user=user;
                this.surface = surface;
                setTitle("好友请求列表");
                setSize(400, 300);
                setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                setLocationRelativeTo(null);
                requestListModel = new DefaultListModel<User>();
                requestList = new JList<>(requestListModel);
                requestList.setCellRenderer(new RequestCellRenderer());
                setDefaultCloseOperation(HIDE_ON_CLOSE);
                requestList.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                                if (e.getClickCount() == 2) {
                                        int selectedIndex = requestList.locationToIndex(e.getPoint());
                                        if (selectedIndex >= 0) {
                                                User selectedRequest = requestListModel.getElementAt(selectedIndex);
                                                handleRequest(selectedRequest);

                                        }
                                }
                        }
                });
                //获取离线时候的添加删除消息
                addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowOpened(WindowEvent e) {
                                Message<User> userMessage = new Message<>();
                                userMessage.setData(user);
                                userMessage.setType(MessageType.INFO);
                                try {
                                        NIOObjectUtil.writeObjectToChannel(userMessage,channel);
                                } catch (IOException ex) {
                                        throw new RuntimeException(ex);
                                }
                        }
                });

                JScrollPane scrollPane = new JScrollPane(requestList);
                add(scrollPane, BorderLayout.CENTER);


        }


        private class RequestCellRenderer extends DefaultListCellRenderer {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                       User request = (User) value;
                        label.setText(request.getNickname()+"请求添加你为好友");
                        return label;
                }
        }




        private void handleRequest(User request) {
                int choice = JOptionPane.showOptionDialog(
                        this,
                        "您想要接受还是拒绝 " +request.getNickname() + " 的好友请求？",
                        "处理好友请求",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        new String[]{"接受", "拒绝"},
                        "接受");

                if (choice == JOptionPane.YES_OPTION) {
                        AddFriend addFriend = new AddFriend();
                        addFriend.setFromUser(user);
                        addFriend.setSendUser(request);
                        Message message= new Message<>();
                        message.setType(MessageType.ACCEPT_FRIEND);
                        message.setMes("接受了好友请求");
                        message.setData(addFriend);
                        try {
                                NIOObjectUtil.writeObjectToChannel(message,channel);
                        } catch (IOException e) {
                                throw new RuntimeException(e);
                        }
                        try {
                                Thread.sleep(100);
                        } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                        }

                        message= new Message<>();
                        message.setType(MessageType.FRIENDS_LIST);
                        message.setData(user);
                        message.setCode(1);
                        message.setMes("请求刷新列表");


                        try {
                                NIOObjectUtil.writeObjectToChannel( message ,channel);
                        } catch (IOException e) {
                                throw new RuntimeException(e);
                        }
                        requestListModel.removeElement(request);
//                        requestListModel.setElementAt(request, requestListModel.indexOf(request));
                } else if (choice == JOptionPane.NO_OPTION) {
                        //拒绝加好友
//                        requestListModel.setElementAt(request, requestListModel.indexOf(request));
                }
        }


        public DefaultListModel<User> getRequestListModel() {
                return requestListModel;
        }

        public void setRequestListModel(DefaultListModel<User> requestListModel) {
                this.requestListModel = requestListModel;
        }

        public JList<User> getRequestList() {
                return requestList;
        }

        public void setRequestList(JList<User> requestList) {
                this.requestList = requestList;
        }

        public SocketChannel getChannel() {
                return channel;
        }

        public void setChannel(SocketChannel channel) {
                this.channel = channel;
        }

        public User getUser() {
                return user;
        }

        public void setUser(User user) {
                this.user = user;
        }

        public Surface getSurface() {
                return surface;
        }

        public void setSurface(Surface surface) {
                this.surface = surface;
        }
}
