package org.skyme.ui;

import org.skyme.core.Message;
import org.skyme.core.MessageType;
import org.skyme.dao.jdbc.TimeUtil;
import org.skyme.entity.QQMessage;
import org.skyme.entity.User;
import org.skyme.ui.reconnect.Reconnect;
import org.skyme.util.NIOObjectUtil;


import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

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
        JButton sendButton = new JButton("发送消息");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        JButton sendFileButton = new JButton("发送文件");
        sendFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendFileMessage();
            }
        });
        chatTextArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount()==2){
                    int caretPosition = chatTextArea.getCaretPosition();
                    int startOfLine = 0;
                    String lineContent = "";
                    try {
                        startOfLine = chatTextArea.getLineStartOffset(chatTextArea.getLineOfOffset(caretPosition));
                        int endOfLine = chatTextArea.getLineEndOffset(chatTextArea.getLineOfOffset(caretPosition));

                        chatTextArea.setSelectionStart(startOfLine);
                        chatTextArea.setSelectionEnd(endOfLine);
                        lineContent = chatTextArea.getDocument().getText(startOfLine, endOfLine - startOfLine);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    int i1 = lineContent.indexOf("\n");
                    lineContent=lineContent.substring(lineContent.indexOf(":")+2,i1);
                    int i = JOptionPane.showConfirmDialog(null, lineContent, "是否下载当前内容", JOptionPane.DEFAULT_OPTION);
                    if(i==0){
                        //选择下载到的地址,然后向服务器获取要下载的文件,然后文件就获取到本地了

                        JFileChooser fileChooser = new JFileChooser();
                        //选择储存的文件夹
                        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        fileChooser.showOpenDialog(ChatWindowApp.this);
                        File selectedFile = fileChooser.getSelectedFile();
                        // 创建Socket对象，指定IP地址和端口号
                        Socket fileSocket = null;
                        try {
                            fileSocket  = new Socket("192.168.0.33", 8089);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        try {
                            OutputStream outputStream = fileSocket.getOutputStream();
                            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                            dataOutputStream.writeUTF("download");
                            dataOutputStream.writeUTF(lineContent);
                            dataOutputStream.flush();

                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        //写入文件
                        FileOutputStream fileOutputStream=null;
                        try {
                            InputStream inputStream = fileSocket.getInputStream();
                            //
                            DataInputStream dataInputStream = new DataInputStream(inputStream);
                            //读取的文件名称
                            String fileName = dataInputStream.readUTF();
                            //读取文件大小
                            long size = dataInputStream.readLong();
                            fileOutputStream = new FileOutputStream(new File(selectedFile, fileName));
                            byte[] bytes = new byte[1024];
                            int len=0;
                            //写入到本地
                            while ((len=dataInputStream.read(bytes))!=-1){
                                fileOutputStream.write(bytes, 0, len);
                            }
                            JOptionPane.showMessageDialog(null,"文件下载成功");

                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }finally {
                            try {
                                fileOutputStream.close();
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                            try {
                                fileSocket.close();
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                    }

                }
            }
        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(sendButton);
        buttonPanel.add(sendFileButton);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(buttonPanel, BorderLayout.EAST);


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
            qqMessage.setFromUid(this.user.getUid());
            qqMessage.setSendUid(firstUser.getUid());
            qqMessage.setSendtime(TimeUtil.date2String(new Date()));
            qqMessage.setStatus(0);//默认是未读
            message1.setData(qqMessage);
            try {

                NIOObjectUtil.writeObjectToChannel(message1,socket);
//                ObjectUtil.sendObject(socket,message1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            chatTextArea.append(qqMessage.getSendtime()+"\n"+this.user.getNickname()+": " + message + "\n");
            chatTextArea.append("\n");
            messageField.setText("");
        }
    }
    //上传文件
    private void sendFileMessage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.showOpenDialog(this);
        File selectedFile = fileChooser.getSelectedFile();
        // 创建Socket对象，指定IP地址和端口号
        if(selectedFile==null){
            return;
        }
        String uuid = UUID.randomUUID().toString();
        JOptionPane.showMessageDialog(this,"文件正在发送中");
        Socket fileSocket = null;
        try {
            fileSocket  = new Socket("192.168.0.33", 8089);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        DataOutputStream dataOutputStream=null;
        try {

            OutputStream outputStream = fileSocket.getOutputStream();
            dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeUTF("upload");
            dataOutputStream.flush();
            dataOutputStream.writeUTF(uuid+selectedFile.getName());//文件名
            dataOutputStream.flush();
            dataOutputStream.writeLong(selectedFile.length());//文件大小
            dataOutputStream.flush();
            FileInputStream fileInputStream = new FileInputStream(selectedFile);
            byte [] bytes = new byte[1024];
            int len = 0;
            while((len = fileInputStream .read(bytes)) != -1){
                dataOutputStream.write(bytes,0,len);
                dataOutputStream.flush();
            }

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }finally {
            try {
                dataOutputStream.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            try {
                fileSocket.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        JOptionPane.showMessageDialog(this,"文件发送成功");
        String message = selectedFile.getName();
        if (!message.isEmpty()) {
            Long uid = firstUser.getUid();
            Message message1 = new Message<>();
            message1.setType(MessageType.SEND);
            message1.setCode(1);
            message1.setMes("发送文件消息给好友");
            QQMessage qqMessage = new QQMessage();
            qqMessage.setContent(message);
            System.out.println("发送者:"+this.user.getUid());
            qqMessage.setFromUid(this.user.getUid());
            System.out.println("需要接收的"+firstUser.getUid());
            qqMessage.setSendUid(firstUser.getUid());
            qqMessage.setStatus(0);//默认是未读
            qqMessage.setSendtime(TimeUtil.date2String(new Date()));
            qqMessage.setFile(uuid+selectedFile.getName());
            message1.setData(qqMessage);
            try {
                NIOObjectUtil.writeObjectToChannel(message1,socket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            chatTextArea.append(qqMessage.getSendtime()+"\n"+this.user.getNickname()+": " + uuid+selectedFile.getName()  +"\n");
           chatTextArea.append("\n");
            messageField.setText("");
        }
    }


}
