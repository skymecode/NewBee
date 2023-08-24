package org.skyme;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import org.skyme.client.Client;
import org.skyme.ui.Login;
import org.skyme.ui.Register;

import java.awt.EventQueue;
import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SocketChannel;

import javax.swing.*;


/**
	 * Launch the application.
	 * 
	 */
public class Main {
	private JPanel contentPane;
	private JPasswordField passwordField;
	private static Register register;
	private static Login login;

	private static SocketChannel socket;

	public static void main(String[] args) {
//		try {
//			UIManager.setLookAndFeel( new FlatMacLightLaf());
//		} catch( Exception ex ) {
//			System.err.println( "Failed to initialize LaF" );
//		}

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				Client client = new Client();
				try {
					client.init();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				try {
					socket = client.getSocket();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				Login login1 = getLogin();
				login1.setVisible(true);
				login1.setSocket(socket);
				Register register1 = getRegister();
				register1.setSocket(socket);

			}
		});
	}
	public static Login getLogin() {
		if(login==null) {
			login =new Login();
		}
		return login;
	}
	public static Register getRegister() {
		if(register==null) {
			register=new Register();
		}
		return register;
	}

	/**
	 * Create the frame.
	 */
 

	
}



