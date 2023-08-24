package org.skyme.ui;

import org.skyme.dto.Message;
import org.skyme.dto.MessageType;
import org.skyme.entity.QQMessage;
import org.skyme.entity.User;
import org.skyme.util.NIOObjectUtil;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.HashMap;

public class ChatWindowApp extends JFrame {
    private JTextArea chatTextArea;
    private JTextField messageField;

    private SocketChannel socket;

    private User firstUser;

    private User user;

    private Surface surface;

    public Surface getSurface() {
        return surface;
    }

    public void setSurface(Surface surface) {
        this.surface = surface;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public JTextArea getChatTextArea() {
        return chatTextArea;
    }

    public void setChatTextArea(JTextArea chatTextArea) {
        this.chatTextArea = chatTextArea;
    }

    public JTextField getMessageField() {
        return messageField;
    }

    public void setMessageField(JTextField messageField) {
        this.messageField = messageField;
    }

    public SocketChannel getSocket() {
        return socket;
    }

    public void setSocket(SocketChannel socket) {
        this.socket = socket;
    }

    public User getFirstUser() {
        return firstUser;
    }

    public void setFirstUser(User firstUser) {
        this.firstUser = firstUser;
    }

    public ChatWindowApp(SocketChannel socket, String friendName,User firstUser,User user,Surface surface) {
        this.socket=socket;
        this.firstUser = firstUser;
        this.user=user;
        setTitle("Chat with " + friendName);
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chatTextArea = new JTextArea();
        chatTextArea.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatTextArea);
        messageField = new JTextField();
        messageField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        JButton sendButton = new JButton("发送");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(chatScrollPane, BorderLayout.CENTER);
        getContentPane().add(inputPanel, BorderLayout.SOUTH);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // 在窗口关闭时执行下线操作
                // 发送下线消息或其他下线逻辑

                // 关闭客户端线程和socket连接等资源
                HashMap<Long, ChatWindowApp> map = surface.getMap();
                map.remove(firstUser.getUid());
//				try {
//					if (socket != null) {
//						socket.close(); // 关闭socket连接
//					}
//				} catch (IOException ex) {
//					ex.printStackTrace();
//				}
            }
        });
        setVisible(true);
    }

    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            Long uid = firstUser.getUid();
            Message message1 = new Message<>();
            message1.setType(MessageType.SEND);
            message1.setCode(1);
            message1.setMes("发送消息给好友");
            QQMessage qqMessage = new QQMessage();
            qqMessage.setContent(message);
            System.out.println("发送者:"+this.user.getUid());
            qqMessage.setFromUid(this.user.getUid());
            System.out.println("需要接收的"+firstUser.getUid());
            qqMessage.setSendUid(firstUser.getUid());
            qqMessage.setStatus(0);//默认是未读
            message1.setDate(qqMessage);
            try {
                NIOObjectUtil.writeObjectToChannel(message1,socket);
//                ObjectUtil.sendObject(socket,message1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            chatTextArea.append(this.user.getNickname()+": " + message + "\n");
            messageField.setText("");
        }
    }


}
