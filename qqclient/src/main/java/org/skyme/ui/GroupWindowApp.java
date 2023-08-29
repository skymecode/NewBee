package org.skyme.ui;

import org.skyme.core.Message;
import org.skyme.core.MessageType;
import org.skyme.entity.QQGroup;
import org.skyme.entity.QQGroupMessage;
import org.skyme.entity.User;
import org.skyme.dao.jdbc.TimeUtil;
import org.skyme.util.NIOObjectUtil;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GroupWindowApp extends JFrame {
    private JTextArea chatArea;
    private JTextField inputField;
    private JList<User> memberList;

    private SocketChannel socket;

    private QQGroup group;

    private Surface surface;


    private User user;

    public JTextArea getChatArea() {
        return chatArea;
    }

    public void setChatArea(JTextArea chatArea) {
        this.chatArea = chatArea;
    }

    public Map<Long, User> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(Map<Long, User> groupMembers) {
        this.groupMembers = groupMembers;
    }

    private Map<Long, User> groupMembers = new HashMap<>(); // 群聊成员列表
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public JList<User> getMemberList() {
        return memberList;
    }

    public void setMemberList(JList<User> memberList) {
        this.memberList = memberList;
    }

    public GroupWindowApp(SocketChannel socket, QQGroup group, User user, Surface surface) {
        this.group = group;
        this.user=user;
        this.socket = socket;
        setTitle("QQ群聊");
        setSize(800, 600); // 调整窗口大小
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // 在窗口关闭时执行下线操作
                // 发送下线消息或其他下线逻辑

                // 关闭客户端线程和socket连接等资源
                HashMap<Long, GroupWindowApp> openGroup = surface.getOpenGroup();
                openGroup.remove(group.getGid());
//				try {
//					if (socket != null) {
//						socket.close(); // 关闭socket连接
//					}
//				} catch (IOException ex) {
//					ex.printStackTrace();
//				}
            }
        });
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        add(chatScrollPane, BorderLayout.CENTER);

        inputField = new JTextField();
        JButton sendButton = new JButton("发送");

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(inputPanel, BorderLayout.SOUTH);
        JPanel memberPanel = new JPanel(new BorderLayout());
        JLabel memberLabel = new JLabel("群成员");
        memberLabel.setHorizontalAlignment(SwingConstants.CENTER);
        memberPanel.add(memberLabel, BorderLayout.NORTH);
        DefaultListModel<User> memberListModel = new DefaultListModel<>();
        memberList = new JList<User>(memberListModel);
        memberList.setCellRenderer(new GroupMemberListCellRenderer()); // 设置渲染器
        JScrollPane memberScrollPane = new JScrollPane(memberList);
        memberScrollPane.setPreferredSize(new Dimension(120, 0)); // 调整宽度
        memberPanel.add(memberScrollPane, BorderLayout.CENTER);
        add(memberPanel, BorderLayout.EAST);

        //获取群成员
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                //请求获取所有群成员
                Message<Long> longMessage = new Message<>();
                longMessage.setType(MessageType.QUERY_GROUP_MEMBER);
                longMessage.setData(group.getGid());
                try {
                    NIOObjectUtil.writeObjectToChannel(longMessage,socket);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

    }

    private void sendMessage() {
        String message = inputField.getText();
        if (!message.isEmpty()) {
            chatArea.append(user.getNickname()+":" + message + "\n");
            inputField.setText("");
            QQGroupMessage qqGroupMessage = new QQGroupMessage();
            qqGroupMessage.setUser(user);
            qqGroupMessage.setgContent(message);
            qqGroupMessage.setGid(group.getGid());

            qqGroupMessage.setgCreateTime(TimeUtil.date2String(new Date()));
            Message<QQGroupMessage> sendToServer = new Message<>();
            sendToServer.setMes("发送给服务器的"+group.getGid()+"群聊消息");
            sendToServer.setCode(1);
            sendToServer.setType(MessageType.GROUP_SEND_MESSAGE);
            sendToServer.setData(qqGroupMessage);
            try {
                NIOObjectUtil.writeObjectToChannel(sendToServer, socket);
//                ObjectUtil.sendObject(socket,sendToServer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private void addMember(User memberName) {
        DefaultListModel<User> model = (DefaultListModel) memberList.getModel();
        model.addElement(memberName);

    }
    private class GroupMemberListCellRenderer extends JPanel implements ListCellRenderer<User> {
        private JLabel nameLabel;
        private JLabel avatarLabel;

        public GroupMemberListCellRenderer() {
            setLayout(new BorderLayout());
            setOpaque(true);
            setBorder(new EmptyBorder(5, 5, 5, 5));
            nameLabel = new JLabel();
            avatarLabel = new JLabel();
            avatarLabel.setPreferredSize(new Dimension(40, 40)); // 设置头像大小
            add(avatarLabel, BorderLayout.WEST);
            add(nameLabel, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends User> list, User value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            nameLabel.setText(value.getNickname());
            ImageIcon avatarIcon = new ImageIcon("F:\\ikun.png");
            Image scaledAvatarImage = avatarIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            avatarLabel.setIcon(new ImageIcon(scaledAvatarImage));
            if(isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            }
            else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            return this;
        }
    }




}
