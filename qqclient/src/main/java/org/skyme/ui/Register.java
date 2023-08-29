package org.skyme.ui;

import org.skyme.Main;
import org.skyme.core.Message;
import org.skyme.core.MessageType;
import org.skyme.entity.User;
import org.skyme.util.NIOObjectUtil;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.channels.SocketChannel;

import static javax.swing.JOptionPane.DEFAULT_OPTION;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;

public class Register extends JFrame {

	private JPanel contentPane;
	private JPasswordField passwordField;

	private SocketChannel socket;

	private Login login;

	public Login getLogin() {
		return login;
	}

	public void setLogin(Login login) {
		this.login = login;
	}

	/**
	 * Launch the application.
	 *
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

	public Register(Login login) {
		this.login=login;
		setBounds(100, 100, 450, 468);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setLocationRelativeTo(null);
		setTitle("Skyme-注册");
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
		btnNewButton_1.setBounds(171, 382, 97, 23);
		contentPane.add(btnNewButton_1);

		btnNewButton_1.addMouseListener(new MouseAdapter() {
											@Override
											public void mouseClicked(MouseEvent e) {
												Register.this.setVisible(false);
												getLogin().setVisible(true);
											}
										}
		);
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				Main.getRegister().setVisible(false);
				Main.getLogin().setVisible(true);
				String username = formattedTextField.getText();
				if(!username.matches("^[a-zA-Z0-9_]{3,20}$")){
					JOptionPane.showConfirmDialog(null,"用户名不合法","警告",DEFAULT_OPTION,INFORMATION_MESSAGE);

				}else{
					String nickname = formattedTextField_1.getText();
					String password = passwordField.getText();
					if(!password.matches("^(?=.*[0-9])(?=.*[a-zA-Z])[a-zA-Z0-9]{6,18}$")){
						JOptionPane.showConfirmDialog(null,"密码不合法","警告",DEFAULT_OPTION,INFORMATION_MESSAGE);

					}else{
						String email = formattedTextField_3.getText();
						MessageType reg = MessageType.REG;//设置消息路径
						User user = new User();
						user.setUsername(username);
						user.setNickname(nickname);
						user.setPassword(password);
						user.setEmail(email);
						Message<User> userMessage = new Message<User>();//生成消息
						userMessage.setData(user);
						userMessage.setMes("请求注册");
						userMessage.setCode(1);
						userMessage.setType(reg);
						try {
							NIOObjectUtil.writeObjectToChannel(userMessage,socket);
						} catch (IOException ex) {
							throw new RuntimeException(ex);
						}
					}
				}




			}
		});

	}

}
