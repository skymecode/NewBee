package org.skyme.ui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SocketChannel;

import org.skyme.core.Message;
import org.skyme.core.MessageType;
import org.skyme.entity.QQMessage;
import org.skyme.entity.User;
import org.skyme.util.NIOObjectUtil;

/**
 * @author:Skyme
 * @create: 2023-09-05 15:14
 * @Description:
 */
public class HistoryMessageWindow extends JFrame {

    private JTextArea historyTextArea;

    private User local;

    private SocketChannel socketChannel;
    private User guest;

    public JTextArea getHistoryTextArea() {
        return historyTextArea;
    }

    public void setHistoryTextArea(JTextArea historyTextArea) {
        this.historyTextArea = historyTextArea;
    }

    public User getLocal() {
        return local;
    }

    public void setLocal(User local) {
        this.local = local;
    }

    public User getGuest() {
        return guest;
    }

    public void setGuest(User guest) {
        this.guest = guest;
    }

    public HistoryMessageWindow(SocketChannel socketChannel, User local, User guest) {
        this.local=local;
        this.guest=guest;
        this.socketChannel=socketChannel;
        setTitle("历史消息");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        historyTextArea = new JTextArea();
        historyTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(historyTextArea);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        JButton closeButton = new JButton("关闭");

        addWindowListener(new WindowAdapter(){
            @Override
            public void windowOpened(WindowEvent e) {
                //请求获取历史消息
                Message message = new Message();
                message.setType(MessageType.HISTORY_FRIEND_MESSAGE);
                QQMessage qqMessage = new QQMessage();
                qqMessage.setFromUid(local.getUid());
                qqMessage.setSendUid(guest.getUid());
                message.setData(qqMessage);
                try {
                    NIOObjectUtil.writeObjectToChannel(message,socketChannel);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // 关闭窗口
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);

        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    // 设置历史消息内容
    public void setHistoryMessages(String messages) {
        historyTextArea.setText(messages);
    }


}
