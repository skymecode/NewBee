package org.skyme.ui;

import org.skyme.client.ClientThread;
import org.skyme.core.Message;
import org.skyme.core.MessageType;
import org.skyme.core.Server;
import org.skyme.dao.jdbc.TimeUtil;
import org.skyme.entity.QQMessage;
import org.skyme.entity.User;
import org.skyme.util.NIOObjectUtil;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

public class ChatWindowApp extends JFrame {

    private JList<QQMessage> friendHistoryJList;

    private ClientThread clientThread;

    private List<QQMessage> friendHistoryList;
    private JTextArea chatTextArea;
    private JTextField messageField;

    private SocketChannel socket;

    private User firstUser;

    private User user;

    private Properties properties;

    private Surface surface;

    private String ip;
    private int port;
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

    public ClientThread getClientThread() {
        return clientThread;
    }

    public void setClientThread(ClientThread clientThread) {
        this.clientThread = clientThread;
    }

    public void paintMessageList (List<QQMessage> messageLists){
        friendHistoryList = messageLists;
        ListModel<QQMessage> model = new AbstractListModel<QQMessage>() {
            @Override
            public int getSize() {
                return friendHistoryList.size();
            }
            @Override
            public QQMessage getElementAt(int index) {
                return  friendHistoryList.get(index);
            }
        };
        friendHistoryJList.setModel(model);
        friendHistoryJList.repaint();// 重新渲染
    }

    public ChatWindowApp(SocketChannel socket, String friendName,User firstUser,User user,Surface surface,ClientThread clientThread) {
        this.socket=socket;
        this.firstUser = firstUser;
        this.user=user;
        this.clientThread=clientThread;
        InputStream in = Server.class.getClassLoader().getResourceAsStream("client.properties");
//
        try{
            properties=new Properties();
            properties.load(in);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
        String ip = properties.getProperty("fileServerIp");
        String fileServerPort = properties.getProperty("fileServerPort");
        this.ip=ip;

        int port =Integer.parseInt(fileServerPort);
        this.port= port;
        setTitle("正在与 " + friendName+"聊天");
        setSize(700, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chatTextArea = new JTextArea();
        chatTextArea.setEditable(false);
        friendHistoryJList = new JList();
        friendHistoryJList.setCellRenderer(new MessageListCellRenderer());
        JScrollPane chatScrollPane = new JScrollPane(friendHistoryJList);
//        JScrollPane chatScrollPane = new JScrollPane(chatTextArea);
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

        friendHistoryJList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount()==2){
                    int selectedIndex = friendHistoryJList.getSelectedIndex();
                    QQMessage qqMessage = friendHistoryList.get(selectedIndex);

                    if(qqMessage.getFile()==null){
                        return;
                    }
                    String fileDName = qqMessage.getFile().toString();

                    int i = JOptionPane.showConfirmDialog(null, fileDName, "是否下载当前内容", JOptionPane.DEFAULT_OPTION);
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
                            fileSocket  = new Socket(ip, port);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        try {
                            OutputStream outputStream = fileSocket.getOutputStream();
                            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                            dataOutputStream.writeUTF("download");
                            dataOutputStream.writeUTF(fileDName);
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
        JButton histroy = new JButton("历史消息");
        histroy.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {
                HistoryMessageWindow historyMessageWindow = new HistoryMessageWindow(socket,user,firstUser);
                historyMessageWindow.setVisible(true);
                ClientThread clientThread1 = getClientThread();
                clientThread1.setHistoryMessageWindow(historyMessageWindow);

            }
        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(sendButton);
        buttonPanel.add(sendFileButton);
        buttonPanel.add(histroy);
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

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
//            chatTextArea.append(qqMessage.getSendtime()+"\n"+this.user.getNickname()+": " + message + "\n");
//            chatTextArea.append("\n");
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
            fileSocket  = new Socket(ip, port);
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
            messageField.setText("");
        }
    }
    private class MessageListCellRenderer extends JLabel implements ListCellRenderer<QQMessage>{

        private ImageIcon defaultIcon;
        public MessageListCellRenderer() {
            setOpaque(true);
            setBorder(new EmptyBorder(5, 5, 5, 5));
            // 使用默认头像图标
//            setBackground(Color.GRAY);
//            setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            defaultIcon = new ImageIcon("C:\\ikun.png");

        }

        @Override
        public Component getListCellRendererComponent(JList<? extends QQMessage> list,QQMessage value, int index, boolean isSelected, boolean cellHasFocus) {
            boolean equals = value.getFromUid().equals(user.getUid());

            // 使用 StringBuilder 构建 HTML 内容
            StringBuilder htmlText = new StringBuilder("<html><div style='display: flex; align-items: center;'>");

            // 根据是否为自己的消息，设置头像的位置
            if (equals) {
                setHorizontalAlignment(SwingConstants.RIGHT);
                // 如果是自己的消息，将头像置于消息内容的右边
                htmlText.append("<div style='order: 2; padding-left: 10px;'>");
            } else {
                setHorizontalAlignment(SwingConstants.LEFT);
                // 否则将头像置于消息内容的左边
                htmlText.append("<div style='order: 1; padding-right: 10px;'>");

            }
            if(value.getFile()!=null){
                String file = value.getFile();
                String imagePath = "D:/savaData/file.png"; // Replace with your image path
                String imageUrl = convertToURL(imagePath);
                htmlText.append("<img src='"+imageUrl+"' width='50' height='40' />");
                htmlText.append("</div>");
                htmlText.append("<div style='order: 3; text-align: center;'>");
                htmlText.append("<h3><font color='blue'>" + value.getFile().toString() + "</font></h3>");
                htmlText.append("</div>");
                htmlText.append("</div></html>");
                setText(htmlText.toString());
                if (isSelected) {
                    setBackground(new Color(242, 242, 242));
                    setForeground(list.getSelectionForeground());
                } else {
                    setBackground(list.getBackground());
                    setForeground(list.getForeground());
                }
            }else{
                String imagePath = "D:/savaData/default.png"; // Replace with your image path
                String imageUrl = convertToURL(imagePath);
                htmlText.append("<img src='"+imageUrl+"' width='50' height='40' />");
                htmlText.append("</div>");
                htmlText.append("<div style='order: 3; text-align: center;'>");
                htmlText.append("<h3><font color='blue'>" + value.getContent() + "</font></h3>");
                htmlText.append("</div>");
                htmlText.append("</div></html>");
                setText(htmlText.toString());
                // 根据 isSelected 设置不同的背景和前景颜色
                if (isSelected) {
                    setBackground(new Color(242, 242, 242));
                    setForeground(list.getSelectionForeground());
                } else {
                    setBackground(list.getBackground());
                    setForeground(list.getForeground());
                }
            }
            return this;
        }
    }
    private static String convertToURL(String filePath) {
        try {
            Path path = Paths.get(filePath);
            return path.toUri().toURL().toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "";
        }
    }


}
