package org.skyme.ui;

import org.skyme.Main;
import org.skyme.client.ClientThread;
import org.skyme.core.Message;
import org.skyme.core.MessageType;
import org.skyme.entity.User;
import org.skyme.util.NIOObjectUtil;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.channels.SocketChannel;


public class Login extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JPasswordField passwordField;

	private SocketChannel socket;
	private Surface friendListWindow;

	/**
	 * Launch the application.
	 */
	public SocketChannel getSocket() {
		return socket;
	}

	public void setSocket(SocketChannel socket) {
		this.socket = socket;
	}
	public ClientThread clientThread;

	public ClientThread getClientThread() {
		return clientThread;
	}

	public void setClientThread(ClientThread clientThread) {
		this.clientThread = clientThread;
	}
/**
	 * Launch the application.
	 */


	/**
	 * Create the frame.
	 */
	public Login() {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		this.setLocationRelativeTo(null);
		setTitle("Skyme-登录");
		JLabel lblNewLabel = new JLabel("用户名:");
		lblNewLabel.setBounds(44, 69, 58, 15);
		contentPane.add(lblNewLabel);
		JLabel lblNewLabel_1 = new JLabel("密码:");
		lblNewLabel_1.setBounds(44, 141, 58, 15);
		contentPane.add(lblNewLabel_1);
		JButton btnNewButton = new JButton("登录");
		btnNewButton.setBounds(89, 202, 97, 23);
		contentPane.add(btnNewButton);
		textField = new JTextField();
		textField.setBounds(89, 66, 259, 21);
		contentPane.add(textField);
		textField.setColumns(10);

		passwordField = new JPasswordField();
		passwordField.setBounds(89, 138, 259, 21);
		contentPane.add(passwordField);
		
		JButton btnNewButton_1 = new JButton("注册");
		btnNewButton_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Main.getLogin().setVisible(false);
				Main.getRegister().setVisible(true);
			}
		});
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				try {
					socket.close();
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
				setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			}
		});
		//登录的按钮
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//账号校验,如果后台返回码为1则表示成功
				String text = textField.getText();
				String text1 = passwordField.getText();
				Main.getLogin().setVisible(false);
				org.skyme.entity.User user = new User();
				user.setUsername(text);
				user.setPassword(text1);
				Message message = new Message<>();
				message.setType(MessageType.LOGIN);
				message.setMes("请求登录");
				message.setCode(1);
				message.setData(user);
				try {
					NIOObjectUtil.writeObjectToChannel(message, Login.this.socket);
					clientThread.setLogin(Login.this);
//					Message o = (Message) NIOObjectUtil.readObjectFromChannel(socket);

//					ObjectUtil.sendObject(socket,message);
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
//				 catch (ClassNotFoundException ex) {
//					throw new RuntimeException(ex);
//				}
			}
		});
		btnNewButton_1.setBounds(251, 202, 97, 23);
		contentPane.add(btnNewButton_1);


	}
	public void openFriendListWindow(User loggedInUser) {
		SwingUtilities.invokeLater(() -> {
			System.out.println("执行");
			friendListWindow = new Surface(loggedInUser,socket,clientThread);
			System.out.println("执行");
			clientThread.setSurface(friendListWindow);

			friendListWindow.setVisible(true);
			friendListWindow.setUser(loggedInUser);
			setVisible(false); // 隐藏登录窗口
		});


	}
}
