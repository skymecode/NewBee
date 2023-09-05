package org.skyme.ui;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import org.skyme.client.ClientThread;
import org.skyme.core.Message;
import org.skyme.core.MessageType;
import org.skyme.dto.*;
import org.skyme.entity.QQGroup;
import org.skyme.entity.QQMessage;
import org.skyme.entity.QQRelation;
import org.skyme.entity.User;
import org.skyme.util.NIOObjectUtil;
import org.skyme.vo.FriendList;
import org.skyme.vo.GroupList;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
/**
 * @author Skyme
 */
public class Surface extends JFrame {
	private Socket fileSocket;

	private JPanel contentPane;
	private static final int ICON_SIZE = 40;

	private SocketChannel socket;

	private ClientThread clientThread;

	private User user;

	//存放chatwindowsapp,不管哪个线程拿到数据都会先去找map里面对应的聊天框对象
	private   HashMap<Long,ChatWindowApp> map=new HashMap<>();

	private HashMap<Long,FriendList> friendUidMap=new HashMap<>();

	private HashMap<Long, GroupList> groupUidMap=new HashMap<>();

	private HashMap<Long,GroupWindowApp> openGroup=new HashMap<>();//判断是否打开聊天对话框


	private  DefaultListModel<GroupList> groupModel;

	public HashMap<Long, GroupWindowApp> getOpenGroup() {
		return openGroup;
	}

	public void setOpenGroup(HashMap<Long, GroupWindowApp> openGroup) {
		this.openGroup = openGroup;
	}

	private JRadioButton friendRadioButton;
	private JRadioButton groupChatRadioButton;
	private ButtonGroup contentButtonGroup;
	private boolean showFriends = true; // 默认显示好友
	List<User> matchingUsers;

	private InfoWindowApp infoWindowApp;
	private List<FriendList> friends;

	private JList<FriendList> friendList;

	private List<GroupList> groups;
	private  JList<GroupList> groupList;
	List<String> matchingUserNicknames ;

	public boolean isShowFriends() {
		return showFriends;
	}

	public void setShowFriends(boolean showFriends) {
		this.showFriends = showFriends;
	}

	public List<String> getMatchingUserNicknames() {
		return matchingUserNicknames;
	}

	public void setMatchingUserNicknames(List<String> matchingUserNicknames) {
		this.matchingUserNicknames = matchingUserNicknames;
	}

	public  List<User> getMatchingUsers() {
		return matchingUsers;
	}

	public void setMatchingUsers(List<User> matchingUsers) {

		this.matchingUsers = matchingUsers;

	}
	public JList<GroupList> getGroupList() {
		return groupList;
	}
	public void setGroupList(JList<GroupList> groupList) {
		this.groupList = groupList;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public InfoWindowApp getInfoWindowApp() {
		return infoWindowApp;
	}
	public void setInfoWindowApp(InfoWindowApp infoWindowApp) {
		this.infoWindowApp = infoWindowApp;
	}
	/**
	 * Launch the application.
	 *
	 */
	public ClientThread getClientThread() {
		return clientThread;
	}
	public void setClientThread(ClientThread clientThread) {
		this.clientThread = clientThread;
	}
	public HashMap<Long, ChatWindowApp> getMap() {
		return map;
	}
	public void setMap(HashMap<Long, ChatWindowApp> map) {
		this.map = map;
	}
	public HashMap<Long, FriendList> getFriendUidMap() {
		return friendUidMap;
	}
	public void setFriendUidMap(HashMap<Long, FriendList> friendUidMap) {
		this.friendUidMap = friendUidMap;
	}
	private DefaultListModel<FriendList> listModel;
	public DefaultListModel<FriendList> getListModel() {
		return listModel;
	}
	public void setListModel(DefaultListModel<FriendList> listModel) {
		this.listModel = listModel;
	}
	public HashMap<Long, GroupList> getGroupUidMap() {
		return groupUidMap;
	}
	public DefaultListModel<GroupList> getGroupModel() {
		return groupModel;
	}
	public void setGroupModel(DefaultListModel<GroupList> groupModel) {
		this.groupModel = groupModel;
	}
	public void setGroupUidMap(HashMap<Long, GroupList> groupUidMap) {
		this.groupUidMap = groupUidMap;
	}

	/**
	 * Create the frame.
	 *
	 */
	public Surface(User loggedInUser,SocketChannel socket,ClientThread clientThread) {

		this.socket=socket;
		this.user=loggedInUser;
		this.clientThread=clientThread;
		JLabel avatarLabel = new JLabel();
		this.setLocationRelativeTo(null);

		avatarLabel.setBounds(10, 32, ICON_SIZE, ICON_SIZE);
//		avatarLabel.setIcon(new ImageIcon(scaledAvatarImage));
		setTitle("当前用户"+loggedInUser.getNickname());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 290, 747);
		setMinimumSize(getSize());
		setMaximumSize(getSize());
		friendRadioButton = new JRadioButton("好友");
		groupChatRadioButton = new JRadioButton("群聊");
		contentButtonGroup = new ButtonGroup();
		contentButtonGroup.add(friendRadioButton);
		contentButtonGroup.add(groupChatRadioButton);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		contentPane.add(avatarLabel);
		JLabel lblNewLabel = new JLabel("用户名:");
		lblNewLabel.setBounds(69, 32, 56, 33);
		contentPane.add(lblNewLabel);
		groupModel=new DefaultListModel<>();
		groupList= new JList<>();
		groupList.setCellRenderer(new GroupListCellRenderer());
		JScrollPane scrollPane2 = new JScrollPane(groupList);
		scrollPane2.setBounds(10, 150, 256, 550);
		contentPane.add(scrollPane2);
		scrollPane2.setVisible(false);
		listModel = new DefaultListModel<>();
//		friendList= new JList<FriendList>(listModel);
		friendList=new JList<>();
		friendList.setCellRenderer(new FriendListCellRenderer());
		JScrollPane scrollPane = new JScrollPane(friendList);
		scrollPane.setBounds(10, 150, 256, 550);
		contentPane.add(scrollPane);
		setTaskbarIcon(this);
		JLabel lblNewLabel_1 = new JLabel(loggedInUser.getNickname());
		lblNewLabel_1.setBounds(117, 36, 56, 24);
		contentPane.add(lblNewLabel_1);
		lblNewLabel_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2){
					String newUsername = JOptionPane.showInputDialog("请输入新的昵称");
//					int result = optionPane.showConfirmDialog(null, "请输入新的用户名", "确认", JOptionPane.OK_CANCEL_OPTION);
					if (newUsername != null && !newUsername.trim().isEmpty()) {
						lblNewLabel_1.setText(newUsername); // 将用户输入的文本设置为JTextField的值
						setTitle("当前用户"+newUsername);
						user.setNickname(newUsername);
						//通知服务器修改了昵称
						Message userMessage = new Message();
						userMessage.setType(MessageType.MOD_NICKNAME);
						userMessage.setData(user);
						userMessage.setMes("发送");
						userMessage.setCode(1);
						try {
							System.out.println("发送修改");
							NIOObjectUtil.writeObjectToChannel(userMessage,socket);
						} catch (IOException ex) {
							throw new RuntimeException(ex);
						}

					}
				}
			}
		});
		JButton btnNewButton = new JButton("添加好友");
		btnNewButton.setBounds(10, 80, 100, 24);
		contentPane.add(btnNewButton);
		friendRadioButton.setSelected(true); // 默认选中好友
		friendRadioButton.addActionListener(e -> {
			showFriends = true;
			scrollPane2.setVisible(false);
			scrollPane.setVisible(true);
			updateListModel();
		});
		groupChatRadioButton.addActionListener(e -> {
			showFriends = false;
			scrollPane.setVisible(false);
			scrollPane2.setVisible(true);
			updateListModel();
		});
		friendRadioButton.setBounds(120, 80, 80, 25);
		groupChatRadioButton.setBounds(200, 80, 80, 25);
		JButton addGroupButton = new JButton("添/创群聊");
		addGroupButton.setBounds(10, 110, 100, 25);
		contentPane.add(addGroupButton);
		JButton infoButton = new JButton("消息通知");
		infoButton.setBounds(120, 120, 150, 25);
		contentPane.add(infoButton);
		infoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 在按钮点击事件中显示 InfoWindowApp 窗口
				InfoWindowApp infoWindowApp = new InfoWindowApp(socket,user, Surface.this);
				clientThread.setInfoWindowApp(infoWindowApp);
				infoWindowApp.setVisible(true);
			}
		});
		avatarLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2){
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.showOpenDialog(Surface.this);
					File selectedFile = fileChooser.getSelectedFile();
					// 创建Socket对象，指定IP地址和端口号
					Socket fileSocket = null;
					try {
						fileSocket  = new Socket("127.0.0.1", 8089);
					} catch (IOException ex) {
						throw new RuntimeException(ex);
					}
					DataOutputStream dataOutputStream=null;
					try {
						OutputStream outputStream = fileSocket.getOutputStream();
						 dataOutputStream = new DataOutputStream(outputStream);
						dataOutputStream.writeUTF("upload");
						dataOutputStream.flush();
						dataOutputStream.writeUTF(user.getUid()+".png");//文件名
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
					ImageIcon defaultAvatarIcon = new ImageIcon(selectedFile.getAbsolutePath()); // 替换为实际路径

					Image scaledAvatarImage = defaultAvatarIcon.getImage().getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH);
					avatarLabel.setIcon(new ImageIcon(scaledAvatarImage));
				}
			}
		});
		// 添加按钮点击事件监听器
		addGroupButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 在这里添加打开添加群聊对话框的逻辑
				// 你可以使用 JOptionPane 或其他方式来实现
				// 例如：显示一个输入对话框，输入群聊的名称或其他信息
				CreateGroupChatDialog dialog = new CreateGroupChatDialog(Surface.this);
//				String groupName = CreateGroupChatDialog.showInputDialog(contentPane, "请输入群聊的名称：");
//				if (groupName != null && !groupName.isEmpty()) {
//					// 在这里执行添加群聊的逻辑
//					// 可以向服务器发送请求来创建一个新的群聊
//					// 并更新群聊列表
//				}
				dialog.setVisible(true);
			}
		});
		contentPane.add(friendRadioButton);
		contentPane.add(groupChatRadioButton);
		btnNewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 弹出输入对话框获取好友昵称
				String nickname = JOptionPane.showInputDialog(contentPane, "请输入好友的昵称：");
				if (nickname != null && !nickname.isEmpty()) {
					// 查询匹配的用户列表

					try {
						System.out.println("查询用户");
						getMatchingUsers(nickname);
					} catch (IOException ex) {
						throw new RuntimeException(ex);
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException ex) {
						throw new RuntimeException(ex);
					}

					// 显示用户列表选择对话框
					System.out.println("继续执行");

					String selectedNickname = (String) JOptionPane.showInputDialog(
							contentPane,
							"选择要添加的用户：",
							"选择用户",
							JOptionPane.PLAIN_MESSAGE,
							null,
							matchingUserNicknames.toArray(),
							null);

					if (selectedNickname != null) {
						User selectedUser = null;
						for (User user : matchingUsers) {
							if (user.getNickname().equals(selectedNickname)) {
								selectedUser = user;
								break;
							}
						}
						// 在这里添加将选中的用户添加为好友的逻辑
						Message<AddFriend> user1 = new Message<>();
						AddFriend addFriend = new AddFriend();
						addFriend.setSendUser(selectedUser);
						addFriend.setFromUser(user);
						user1.setData(addFriend);
						user1.setType(MessageType.ADD_FRIEND);
						try {
							NIOObjectUtil.writeObjectToChannel(user1,socket);
//							ObjectUtil.sendObject(socket,user1);
						} catch (IOException ex) {
							throw new RuntimeException(ex);
						}
						JOptionPane.showMessageDialog(contentPane, "已经向 " + selectedUser.getNickname() + " 发送好友申请");
					}
				}
			}
		});
	      WindowAdapter w=	new WindowAdapter() {


			@Override
			public void windowOpened(WindowEvent e) {
				// 创建Socket对象，指定IP地址和端口号
				Socket fileSocket = null;
				try {
					fileSocket  = new Socket("127.0.0.1", 8089);
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
				try {
					OutputStream outputStream = fileSocket.getOutputStream();
					DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
					dataOutputStream.writeUTF("download");
					dataOutputStream.flush();
					dataOutputStream.writeUTF(user.getUid()+".png");
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
					File file = new File("C:\\NewBeeData\\userAvatar");
					if(!file.exists()){
						file.mkdirs();//创建目录
						fileOutputStream = new FileOutputStream(new File("C:\\NewBeeData\\userAvatar", fileName));
						byte[] bytes = new byte[1024];
						int len=0;
						//写入到本地
						while ((len=dataInputStream.read(bytes))!=-1){
							fileOutputStream.write(bytes, 0, len);
						}
					}else{
						fileOutputStream = new FileOutputStream(new File("C:\\NewBeeData\\userAvatar", fileName));
						byte[] bytes = new byte[1024];
						int len=0;
						//写入到本地
						while ((len=dataInputStream.read(bytes))!=-1){
							fileOutputStream.write(bytes, 0, len);
						}
					}
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}finally {
					try {
						if(fileOutputStream!=null){
						fileOutputStream.close();
						}
					} catch (IOException ex) {
						throw new RuntimeException(ex);
					}
					try {
						fileSocket.close();
					} catch (IOException ex) {
						throw new RuntimeException(ex);
					}
				}
				//加载头像内容
				File file = new File("C:\\NewBeeData\\userAvatar\\"+user.getUid()+".png");
				if(!file.exists()){
					//没有头像就去获取默认头像
					file=new File("C:\\NewBeeData\\userAvatar\\default.png");
				}
				ImageIcon defaultAvatarIcon = new ImageIcon(file.getAbsolutePath()); // 替换为实际路径
				Image scaledAvatarImage = defaultAvatarIcon.getImage().getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH);
				avatarLabel.setIcon(new ImageIcon(scaledAvatarImage));
				Message groupMessage = new Message<>();
				groupMessage.setData(loggedInUser);
				groupMessage.setCode(1);
				groupMessage.setMes("请求查询群聊列表");
				groupMessage.setType(MessageType.GROUP_LIST);
				try {
					System.out.println("请求查询群列表");
					NIOObjectUtil.writeObjectToChannel(groupMessage,socket);
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException ex) {
					throw new RuntimeException(ex);
				}
				//查询好友列表
				Message m = new Message<>();
				m.setData(loggedInUser);
				m.setCode(1);
				m.setMes("请求查询好友列表");
				m.setType(MessageType.FRIENDS_LIST);
				try {
					System.out.println("请求查询好友列表");
					NIOObjectUtil.writeObjectToChannel(m,socket);
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}

			}
		};
		addWindowListener(w);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// 在窗口关闭时执行下线操作
				// 发送下线消息或其他下线逻辑
				Message<Long> longMessage = new Message<>();
				longMessage.setType(MessageType.LOGOUT);
				longMessage.setData(loggedInUser.getUid());
				longMessage.setCode(1);
				longMessage.setMes("请求下线");
				try {
					NIOObjectUtil.writeObjectToChannel(longMessage,socket);
//					ObjectUtil.sendObject(socket,longMessage);
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
				setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				//等待3s
				try {
					Thread.sleep(3000);
				} catch (InterruptedException ex) {
					throw new RuntimeException(ex);
				}
				System.exit(0);
			}
		});
		groupList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // 双击事件
                    int index = groupList.locationToIndex(e.getPoint());
					System.out.println("进入双击事件");
					System.out.println("index:"+index);
                    if (index >= 0) {
//							System.out.println(1);
						openGroupChatWindow(groups.get(index).getGroup());
//						listModel.getElementAt(index).substring(0,listModel.getElementAt(index).indexOf(":"))))
					}
//					}else{
//
//							System.out.println(2);
//
//
//					}
					//向服务器发送已经读取消息的通知
					//
                }
            }
        });
		friendList.addMouseListener(new MouseAdapter() {


			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) { // 双击事件
					int index = friendList.locationToIndex(e.getPoint());
					if (index >= 0) {
						if(isShowing()){
							openChatWindow(friends.get(index).getUser());
//							openChatWindow(friendUidMap.get(listModel.getElementAt(index).getUser().getUid()).getUser());
//						listModel.getElementAt(index).substring(0,listModel.getElementAt(index).indexOf(":"))))
						}
					}
				}
			}
		});
		JPopupMenu popupMenu1 = new JPopupMenu();
		JMenuItem deleteGroupMenuItem = new JMenuItem("删除群聊");
		popupMenu1.add(deleteGroupMenuItem);
		groupList.setComponentPopupMenu(popupMenu1);
		deleteGroupMenuItem.addActionListener(e->{

				int selectedIndex = groupList.getSelectedIndex();
				if (selectedIndex != -1) {
					GroupList selectedGroup  = groups.get(selectedIndex);
//					GroupList selectedGroup = groupModel.getElementAt(selectedIndex);

					int confirm = JOptionPane.showConfirmDialog(
							contentPane,
							"确定要退出群聊 " + selectedGroup.getGroup().getGName() + " 吗？",
							"确认退出",
							JOptionPane.YES_NO_OPTION);

					if (confirm == JOptionPane.YES_OPTION) {
						// 在这里执行退出群聊的操作
						// 在这里向服务器发退出群聊的请求，更新数据库
						friends.remove(selectedIndex);
//						groupModel.remove(selectedIndex);
//						groupUidMap.remove(selectedGroup.getGroup().getGid());
						Message<RemoveGroup> delete  = new Message<>();
						delete.setCode(1);
						delete.setType(MessageType.QUIT_GROUP);
						RemoveGroup removeGroup = new RemoveGroup();
						removeGroup.setGid(selectedGroup.getGroup().getGid());
						removeGroup.setUid(user.getUid());
						delete.setData(removeGroup);
						try {
							NIOObjectUtil.writeObjectToChannel(delete,socket);
//							ObjectUtil.sendObject(socket, delete);
						} catch (IOException ex) {
							throw new RuntimeException(ex);
						}

					}
				}

		});
		JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem deleteFriendMenuItem = new JMenuItem("删除好友");
		popupMenu.add(deleteFriendMenuItem);
		// 将右键菜单添加到好友列表
		friendList.setComponentPopupMenu(popupMenu);
		// 添加删除好友选项的动作监听器
		deleteFriendMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedIndex = friendList.getSelectedIndex();
				if (selectedIndex != -1) {
					FriendList selectedUser = friends.get(selectedIndex);
//					FriendList selectedUser = listModel.getElementAt(selectedIndex);

					int confirm = JOptionPane.showConfirmDialog(
							contentPane,
							"确定要删除好友 " + selectedUser.getUser().getNickname() + " 吗？",
							"确认删除",
							JOptionPane.YES_NO_OPTION);

					if (confirm == JOptionPane.YES_OPTION) {
						// 在这里执行删除好友的操作
						// 在这里向服务器发送删除好友的请求，更新数据库
						Message<QQRelation> delete  = new Message<>();
						delete.setCode(1);
						delete.setType(MessageType.DELETE_FRIEND);
						QQRelation qqRelation = new QQRelation();
						qqRelation.setFid(selectedUser.getUser().getUid());
						qqRelation.setUid(user.getUid());
						delete.setData(qqRelation);
						try {
							NIOObjectUtil.writeObjectToChannel(delete,socket);
//							ObjectUtil.sendObject(socket, delete);
						} catch (IOException ex) {
							throw new RuntimeException(ex);
						}


						JOptionPane.showMessageDialog(
								contentPane,
								"好友 " + selectedUser.getUser().getNickname() + " 已删除",
								"删除成功",
								JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
		});
	}
	private void openGroupChatWindow(QQGroup group) {
		SwingUtilities.invokeLater(() -> {
			System.out.println("点开的群聊对象:"+group.getGid());
			//对好友生成聊天窗口
			GroupWindowApp chatWindow = new GroupWindowApp(socket,group,this.user,this);
			//放入缓存当中
			openGroup.put(group.getGid(),chatWindow);
			//历史消息查询
			Message message = new Message<>();
			GroupUser groupUser = new GroupUser();
			groupUser.setGid(group.getGid());
			groupUser.setUid(user.getUid());
			message.setData(groupUser);
			message.setType(MessageType.GROUP_MESSAGE_HISTORY);
			try {
				NIOObjectUtil.writeObjectToChannel(message,socket);
//				ObjectUtil.sendObject(socket,message);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			//将此类传入线程，监听
			chatWindow.setUser(this.user);//自己传入
			openGroup.put(user.getUid(),chatWindow);
			clientThread.setGroupWindowApp(chatWindow);
			chatWindow.setVisible(true);
		});
	}

	private void getMatchingUsers(String nickname) throws IOException {
		// 向服务器发送数据
		Message message = new Message<>();
		message.setData(nickname);
		message.setCode(1);
		message.setType(MessageType.QUERY_LIKE_USER);
		NIOObjectUtil.writeObjectToChannel(message,socket);
//		ObjectUtil.sendObject(socket,message);
	}

	public void setSocket(Socket socket) {
	}

	private class GroupListCellRenderer extends JLabel implements ListCellRenderer<GroupList>{

		private ImageIcon defaultIcon;
		public GroupListCellRenderer() {
			setOpaque(true);
			setBorder(new EmptyBorder(5, 5, 5, 5));
			// 使用默认头像图标
			defaultIcon = new ImageIcon("C:\\ikun.png");
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends GroupList> list, GroupList value, int index, boolean isSelected, boolean cellHasFocus) {
			String gName = value.getGroup().getGName();
			setText("<html><h3><font color='blue'>"+value.getGroup().getGName()+"</font></h3></html>");
			if(value.getNums()>0){
//				String text = getText();
//				text=text+"  未读:"+value.getNums();
				setText("<html><h3><font color='blue'>"+value.getGroup().getGName()+"</font></h3>未读:"+value.getNums()+"</html>");
			}
			Image scaledImage = defaultIcon.getImage().getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH);
			setIcon(new ImageIcon(scaledImage));

			// 可以根据isSelected来设置不同的背景和前景颜色
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			return this;
		}
		}


	private class FriendListCellRenderer extends JLabel implements ListCellRenderer<FriendList> {
        private ImageIcon defaultIcon;

        public FriendListCellRenderer() {
            setOpaque(true);
            setBorder(new EmptyBorder(5, 5, 5, 5));

            // 使用默认头像图标
            defaultIcon = new ImageIcon("C:\\ikun.png");
        }


       @Override
        public Component getListCellRendererComponent(JList<? extends FriendList> list,  FriendList value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {


		 if(value.getUser().getOnline()==1){
			 setText("<html><h3><font color='green'>"+value.getUser().getNickname()+"</front></h3>"+"  "+"\t在线");


		 }else{
			 setText("<html><h3><font color='green'>"+value.getUser().getNickname()+"</front></h3>"+"  "+"\t离线");

		 }
		   if(value.getNums()>0){
			   String text = getText();
			   text=text+"  未读:"+value.getNums()+"</html>";
			   setText(text);
		   }else{
			   String text = getText();
			   text=text+"</html>";
			   setText(text);
		   }


		 Image scaledImage = defaultIcon.getImage().getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH);
            setIcon(new ImageIcon(scaledImage));
            
            // 可以根据isSelected来设置不同的背景和前景颜色
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            return this;
        }



	}
	 private void openChatWindow(User user) {
	        // 在此处添加打开聊天窗口的逻辑
//	        JOptionPane.showMessageDialog(this, "打开与 " + friendName + " 的聊天窗口");
	        SwingUtilities.invokeLater(() -> {
				System.out.println("点开聊天框对象是:"+user.getUid());
				//对好友生成聊天窗口
	            ChatWindowApp chatWindow = new ChatWindowApp(socket,user.getNickname(),user,this.user,this,clientThread);
				map.put(user.getUid(),chatWindow);
				//历史消息查询
				Message message = new Message<>();
				QQMessage qqMessage = new QQMessage();
				System.out.println("发送者:"+this.user.getUid());
				qqMessage.setFromUid(this.user.getUid());
				System.out.println("需要接收的"+user.getUid());
				qqMessage.setSendUid(user.getUid());
				message.setData(qqMessage);
				message.setType(MessageType.MES_HISTORY);
				try {
					NIOObjectUtil.writeObjectToChannel(message,socket);
//					ObjectUtil.sendObject(socket,message);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}

				//将此类传入线程，监听
				chatWindow.setUser(this.user);//自己传入
				map.put(user.getUid(),chatWindow);
				clientThread.setChatWindowApp(chatWindow);
	            chatWindow.setVisible(true);
	        });
	    }
	private void updateListModel() {
		listModel.clear();
		groupModel.clear();
		if (showFriends) {
			// 添加好友到列表
			for (FriendList friend : friendUidMap.values()) {
				listModel.addElement(friend);
			}
			friendList.setVisible(true);
		} else {
			for(GroupList group:groupUidMap.values()){
				groupModel.addElement(group);
			}
			groupList.setVisible(true);
		}
	}
	  class CreateGroupChatDialog extends JDialog {

		private JTextField groupNameField;
		private JButton createButton;
		private JButton cancelButton;
		private	JButton joinButton;

		public CreateGroupChatDialog(Frame parent) {
			super(parent, "添加或创建群聊", true); // 设置为模态对话框

			JPanel panel = new JPanel(new BorderLayout());

			groupNameField = new JTextField(20);
			createButton = new JButton("创建");
			joinButton= new JButton("加入");
			cancelButton = new JButton("取消");

			JPanel inputPanel = new JPanel(new FlowLayout());
			inputPanel.add(new JLabel("群聊名称:"));
			inputPanel.add(groupNameField);

			JPanel buttonPanel = new JPanel(new FlowLayout());
			buttonPanel.add(createButton);
			buttonPanel.add(cancelButton);
			buttonPanel.add(joinButton);

			panel.add(inputPanel, BorderLayout.CENTER);
			panel.add(buttonPanel, BorderLayout.SOUTH);

			add(panel);

			createButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String groupName = groupNameField.getText();
					if (!groupName.isEmpty()) {
						// 在这里执行创建群聊的逻辑
//						System.out.println("创建群聊: " + groupName);
						Message message = new Message<>();
						message.setCode(1);
						message.setType(MessageType.CREATE_GROUP);
						AddGroup addGroup = new AddGroup();
						addGroup.setGroupName(groupName);
						addGroup.setUid(user.getUid());
						message.setData(addGroup);
						try {
							NIOObjectUtil.writeObjectToChannel(message,socket);
//							ObjectUtil.sendObject(socket,message);
						} catch (IOException ex) {
							throw new RuntimeException(ex);
						}

					} else {
						JOptionPane.showMessageDialog(
								CreateGroupChatDialog.this,
								"请输入群聊名称",
								"错误",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			});
			joinButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String groupName = groupNameField.getText();
					if (!groupName.isEmpty()) {
						Message message = new Message<>();
						message.setCode(1);
						message.setType(MessageType.APPLY_JOIN_GROUP);
						AddGroup addGroup = new AddGroup();
						addGroup.setGroupName(groupName);
						addGroup.setUid(user.getUid());
						message.setData(addGroup);
						try {
							NIOObjectUtil.writeObjectToChannel(message,socket);
						} catch (IOException ex) {
							throw new RuntimeException(ex);
						}
					} else {
						JOptionPane.showMessageDialog(
								CreateGroupChatDialog.this,
								"请输入群聊名称",
								"错误",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			});
			cancelButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dispose(); // 关闭对话框
				}
			});

			pack();
			setLocationRelativeTo(parent);
		}
}
	public void paintFriendList (List<FriendList> userList){
		friends = userList;
		ListModel<FriendList> model = new AbstractListModel<FriendList>() {
			@Override
			public int getSize() {
				return friends.size();
			}
			@Override
			public FriendList getElementAt(int index) {
				return friends.get(index);
			}
		};
		friendList.setModel(model);
		friendList.repaint();// 重新渲染
	}
	public void paintGroupList (List<GroupList> groupLists){
		groups = groupLists;
		ListModel<GroupList> model = new AbstractListModel<GroupList>() {
			@Override
			public int getSize() {
				return groups.size();
			}

			@Override
			public GroupList getElementAt(int index) {
				return  groups.get(index);
			}
		};
		groupList.setModel(model);
		groupList.repaint();// 重新渲染
	}
	private static void setTaskbarIcon(JFrame frame) {
		if (SystemTray.isSupported()) {
			SystemTray tray = SystemTray.getSystemTray();
			Image image = Toolkit.getDefaultToolkit().getImage("F:\\ikun.png"); // 任务栏图标的图片文件路径
			// 创建任务栏图标
			TrayIcon trayIcon = new TrayIcon(image, "即时聊天");
			trayIcon.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					frame.setVisible(true); // 单击任务栏图标时，显示主窗口
					frame.setExtendedState(JFrame.NORMAL);
				}
			});

			trayIcon.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON3) {
						// 右键点击时显示退出菜单项
						PopupMenu popupMenu = new PopupMenu();
						MenuItem exitItem = new MenuItem("Exit");
						exitItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								// 退出应用程序
								System.exit(0);
							}
						});
						popupMenu.add(exitItem);
						trayIcon.setPopupMenu(popupMenu);
					}
				}
			});

			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				System.err.println("Could not add tray icon");
			}
		} else {
			System.err.println("SystemTray is not supported");
		}
	}
}

