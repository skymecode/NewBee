package org.skyme.ui;

import org.skyme.core.Message;
import org.skyme.core.MessageType;
import org.skyme.dto.Forget;
import org.skyme.util.NIOObjectUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * @author:Skyme
 * @create: 2023-09-05 16:51
 * @Description:
 */
public class ForgotPasswordApp extends JFrame {
    private JPanel contentPane;
    private JTextField textField;
    private JTextField textField_1;
    private SocketChannel socketChannel;

    /**
     * Launch the application.
     */


    /**
     * Create the frame.
     */
    public ForgotPasswordApp(SocketChannel socketChannel) {
        this.socketChannel=socketChannel;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 455, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblNewLabel = new JLabel("用户名:");
        lblNewLabel.setBounds(76, 46, 58, 15);
        contentPane.add(lblNewLabel);

        JLabel lblNewLabel_1 = new JLabel("邮箱:");
        lblNewLabel_1.setBounds(76, 142, 58, 15);
        contentPane.add(lblNewLabel_1);

        textField = new JTextField();
        textField.setBounds(169, 43, 167, 21);
        contentPane.add(textField);
        textField.setColumns(10);

        textField_1 = new JTextField();
        textField_1.setBounds(169, 139, 167, 21);
        contentPane.add(textField_1);
        textField_1.setColumns(10);

        JButton btnNewButton = new JButton("重置密码");
        btnNewButton.setBounds(177, 206, 97, 23);
        contentPane.add(btnNewButton);

        btnNewButton.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {
                //向服务器发送请求重置密码
                Message message = new Message();
                String userName = textField.getText();
                String email = textField_1.getText();
                Forget forget = new Forget();
                forget.setEmail(email);
                forget.setUsername(userName);
                message.setData(forget);
                message.setCode(1);
                message.setType(MessageType.FORGET_PASSWORD);
                try {
                    NIOObjectUtil.writeObjectToChannel(message,socketChannel);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                JOptionPane.showMessageDialog(null,"发送成功");

            }
        });
    }
}