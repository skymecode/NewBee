package org.skyme.ui;

import org.skyme.core.Message;
import org.skyme.core.MessageType;
import org.skyme.dto.ChangePassWord;
import org.skyme.util.NIOObjectUtil;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * @author:Skyme
 * @create: 2023-09-05 17:30
 * @Description:
 */
public class ChangePassWordApp extends JFrame{
    private JPanel contentPane;
    private JTextField textField;
    private JTextField textField_1;
    private JTextField textField_2;

    private SocketChannel socketChannel;


    /**
     * Launch the application.
     */


    /**
     * Create the frame.
     */
    public ChangePassWordApp(SocketChannel socketChannel) {
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

        JLabel lblNewLabel_1 = new JLabel("原密码");
        lblNewLabel_1.setBounds(76, 97, 58, 15);
        contentPane.add(lblNewLabel_1);

        textField = new JTextField();
        textField.setBounds(169, 43, 167, 21);
        contentPane.add(textField);
        textField.setColumns(10);

        textField_1 = new JTextField();
        textField_1.setBounds(169, 94, 167, 21);
        contentPane.add(textField_1);
        textField_1.setColumns(10);

        JButton btnNewButton = new JButton("修改密码");
        btnNewButton.setBounds(177, 206, 97, 23);
        contentPane.add(btnNewButton);

        JLabel lblNewLabel_2 = new JLabel("新密码:");
        lblNewLabel_2.setBounds(76, 160, 58, 15);
        contentPane.add(lblNewLabel_2);

        textField_2 = new JTextField();
        textField_2.setBounds(169, 157, 167, 21);
        contentPane.add(textField_2);
        textField_2.setColumns(10);

        btnNewButton.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {
                ChangePassWord changePassWord = new ChangePassWord();
                changePassWord.setUserName(textField.getText());
                changePassWord.setPassWord(textField_1.getText());
                changePassWord.setNewPassWord(textField_2.getText());
                //修改密码
                Message message = new Message();
                message.setCode(1);
                message.setType(MessageType.CHANGE_PASSWORD);
                message.setData(changePassWord);
                try {
                    System.out.println("发送成功");
                    NIOObjectUtil.writeObjectToChannel(message,socketChannel);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }
}
