package org.skyme.ui;

import org.skyme.Main;
import org.skyme.dto.Message;
import org.skyme.dto.MessageType;
import org.skyme.entity.User;
import org.skyme.util.NIOObjectUtil;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SocketChannel;

import static javax.swing.JOptionPane.DEFAULT_OPTION;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;

public class Register extends JFrame {

	private JPanel contentPane;
	private JPasswordField passwordField;

	private SocketChannel socket;

	/**
	 * Launch the application.
	 */


	public SocketChannel getSocket() {
		return socket;
	}

	public void setSocket(SocketChannel socket) {
		this.socket = socket;
	}

	/**
	 * Create the frame.
	 */

	public Register() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 468);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("用户名:");
		lblNewLabel.setBounds(46, 32, 58, 15);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("昵称:");
		lblNewLabel_1.setBounds(46, 91, 58, 15);
		contentPane.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("密码：");
		lblNewLabel_2.setBounds(46, 163, 58, 15);
		contentPane.add(lblNewLabel_2);
		
		JLabel lblNewLabel_3 = new JLabel("邮箱:");
		lblNewLabel_3.setBounds(46, 231, 58, 15);
		contentPane.add(lblNewLabel_3);
		
		JFormattedTextField formattedTextField = new JFormattedTextField();
		formattedTextField.setBounds(152, 24, 193, 32);
		contentPane.add(formattedTextField);
		
		JFormattedTextField formattedTextField_1 = new JFormattedTextField();
		formattedTextField_1.setBounds(152, 83, 193, 32);
		contentPane.add(formattedTextField_1);
		
		JFormattedTextField formattedTextField_3 = new JFormattedTextField();
		formattedTextField_3.setBounds(152, 226, 193, 26);
		contentPane.add(formattedTextField_3);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(152, 155, 193, 32);
		contentPane.add(passwordField);
		
		JButton btnNewButton = new JButton("注册");
		btnNewButton.setBounds(171, 295, 97, 23);
		contentPane.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("返回登录");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				Main.getRegister().setVisible(false);
				Main.getLogin().setVisible(true);
				String username = formattedTextField.getText();
				String nickname = formattedTextField_1.getText();
				String password = passwordField.getText();
				String email = formattedTextField_3.getText();
				MessageType reg = MessageType.REG;//设置消息路径
				User user = new User();
				user.setUsername(username);
				user.setNickname(nickname);
				user.setPassword(password);
				user.setEmail(email);
				Message<User> userMessage = new Message<User>();//生成消息
				userMessage.setDate(user);
				userMessage.setMes("请求注册");
				userMessage.setCode(1);
				userMessage.setType(reg);
				try {
					NIOObjectUtil.writeObjectToChannel(userMessage,socket);
//					ObjectUtil.sendObject(socket,userMessage);
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
				//通过socket将message类发送到
//				try {
//					Message message = (Message) ObjectUtil.getObject(socket);
//					if(message.getType()==MessageType.REG_RESULT){
//						if(message.getCode()==1){
//							JOptionPane.showConfirmDialog(contentPane,message.getMes(),"消息提示",DEFAULT_OPTION,INFORMATION_MESSAGE);
//						}else{
//							JOptionPane.showConfirmDialog(contentPane,message.getMes(),"消息提示",DEFAULT_OPTION,INFORMATION_MESSAGE);
//						}
//					}
//				} catch (IOException | ClassNotFoundException ex) {
//					throw new RuntimeException(ex);
//				}

			}
		});
		btnNewButton_1.setBounds(171, 382, 97, 23);
		contentPane.add(btnNewButton_1);
	}

}
