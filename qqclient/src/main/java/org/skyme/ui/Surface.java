package org.skyme.ui;

import org.skyme.client.ClientThread;
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
import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * @author Skyme
 */
public class Surface extends JFrame {

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

	private JList<FriendList> friendList;
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

	public JList<FriendList> getFriendList() {
		return friendList;
	}

	public void setFriendList(JList<FriendList> friendList) {
		this.friendList = friendList;
	}

	public JList<GroupList> getGroupList() {
		return groupList;
	}

	public void setGroupList(JList<GroupList> groupList) {
		this.groupList = groupList;
	}
	//	@Override
//	public JPanel getContentPane() {
//		return contentPane;
//	}
//
//	public void setContentPane(JPanel contentPane) {
//		this.contentPane = contentPane;
//	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * Launch the application.
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
		avatarLabel.setBounds(10, 32, ICON_SIZE, ICON_SIZE);
		ImageIcon defaultAvatarIcon = new ImageIcon("F:\\ikun.png"); // 替换为实际路径
		Image scaledAvatarImage = defaultAvatarIcon.getImage().getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH);
		avatarLabel.setIcon(new ImageIcon(scaledAvatarImage));
		setTitle("当前用户"+loggedInUser.getNickname());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 290, 747);
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
		groupList= new JList<GroupList>(groupModel);
		groupList.setCellRenderer(new GroupListCellRenderer());
		JScrollPane scrollPane2 = new JScrollPane(groupList);
		scrollPane2.setBounds(10, 150, 256, 550);
		contentPane.add(scrollPane2);
		scrollPane2.setVisible(false);
		listModel = new DefaultListModel<>();
		friendList= new JList<FriendList>(listModel);
		friendList.setCellRenderer(new FriendListCellRenderer());
		JScrollPane scrollPane = new JScrollPane(friendList);
		scrollPane.setBounds(10, 150, 256, 550);
		contentPane.add(scrollPane);
		JLabel lblNewLabel_1 = new JLabel(loggedInUser.getNickname());
		lblNewLabel_1.setBounds(117, 36, 56, 24);
		contentPane.add(lblNewLabel_1);
		JButton btnNewButton = new JButton("添加好友");
		btnNewButton.setBounds(10, 80, 100, 24);
		contentPane.add(btnNewButton);
		//
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
		JButton addGroupButton = new JButton("添加/创建群聊");
		addGroupButton.setBounds(10, 110, 100, 25);
		contentPane.add(addGroupButton);

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
						Thread.sleep(1000);
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
						user1.setDate(addFriend);
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
				Message groupMessage = new Message<>();
				groupMessage.setDate(loggedInUser);
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
					socket.socket().getOutputStream().flush();
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
				//查询好友列表
				Message m = new Message<>();
				m.setDate(loggedInUser);
				m.setCode(1);
				m.setMes("请求查询好友列表");
				m.setType(MessageType.FRIENDS_LIST);
				try {
					NIOObjectUtil.writeObjectToChannel(m,socket);
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}

//				try {
//					Message object = (Message) ObjectUtil.getObject(socket);
//
//					if(object.getType()==MessageType.GROUP_LIST_RESULT){
//						List<GroupList> list = (List<GroupList>) object.getDate();
//						//将上述里面的用户信息添加到list里面
//						for (int i = 0; i < list.size(); i++) {
//							groupUidMap.put(list.get(i).getGroup().getGid(),list.get(i));//将好友放入到本地缓存当中
//							groupModel.addElement(list.get(i));
////							list.get(i).getUid()+":"+list.get(i).getNickname()
//						}
//						JOptionPane.showMessageDialog(null, object.getMes(), "初始化", JOptionPane.PLAIN_MESSAGE);
//						clientThread.start();
//					}else{
//						JOptionPane.showMessageDialog(null, object.getMes(), "初始化", JOptionPane.PLAIN_MESSAGE);
//					}
//
//
//				} catch (IOException | ClassNotFoundException ex) {
//					throw new RuntimeException(ex);
//				}

			}
		};
		addWindowListener(w);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// 在窗口关闭时执行下线操作
				// 发送下线消息或其他下线逻辑
				System.out.println("请求下线");
				Message<Long> longMessage = new Message<>();
				longMessage.setType(MessageType.LOGOUT);
				longMessage.setDate(loggedInUser.getUid());
				longMessage.setCode(1);
				longMessage.setMes("请求下线");
				try {
					NIOObjectUtil.writeObjectToChannel(longMessage,socket);
//					ObjectUtil.sendObject(socket,longMessage);
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
				setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

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

							System.out.println(1);
							openGroupChatWindow(groupModel.getElementAt(index).getGroup());
//						listModel.getElementAt(index).substring(0,listModel.getElementAt(index).indexOf(":"))))

					}else{

							System.out.println(2);


					}
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
					System.out.println("进入双击事件");
					System.out.println("index:"+index);
					if (index >= 0) {
						if(isShowing()){
							System.out.println(1);
							openChatWindow(friendUidMap.get(listModel.getElementAt(index).getUser().getUid()).getUser());
//						listModel.getElementAt(index).substring(0,listModel.getElementAt(index).indexOf(":"))))
						}else{
							System.out.println(2);
							openGroupChatWindow(groupModel.getElementAt(index).getGroup());
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
					GroupList selectedGroup = groupModel.getElementAt(selectedIndex);

					int confirm = JOptionPane.showConfirmDialog(
							contentPane,
							"确定要退出群聊 " + selectedGroup.getGroup().getGName() + " 吗？",
							"确认退出",
							JOptionPane.YES_NO_OPTION);

					if (confirm == JOptionPane.YES_OPTION) {
						// 在这里执行退出群聊的操作
						// 在这里向服务器发退出群聊的请求，更新数据库
						groupModel.remove(selectedIndex);
						groupUidMap.remove(selectedGroup.getGroup().getGid());
						Message<RemoveGroup> delete  = new Message<>();
						delete.setCode(1);
						delete.setType(MessageType.QUIT_GROUP);
						RemoveGroup removeGroup = new RemoveGroup();
						removeGroup.setGid(selectedGroup.getGroup().getGid());
						removeGroup.setUid(user.getUid());
						delete.setDate(removeGroup);
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
					FriendList selectedUser = listModel.getElementAt(selectedIndex);

					int confirm = JOptionPane.showConfirmDialog(
							contentPane,
							"确定要删除好友 " + selectedUser.getUser().getNickname() + " 吗？",
							"确认删除",
							JOptionPane.YES_NO_OPTION);

					if (confirm == JOptionPane.YES_OPTION) {
						// 在这里执行删除好友的操作
						// 在这里向服务器发送删除好友的请求，更新数据库
						listModel.remove(selectedIndex);
						friendUidMap.remove(selectedUser.getUser().getUid());
						Message<QQRelation> delete  = new Message<>();
						delete.setCode(1);
						delete.setType(MessageType.DELETE_FRIEND);
						QQRelation qqRelation = new QQRelation();
						qqRelation.setFid(selectedUser.getUser().getUid());
						qqRelation.setUid(user.getUid());
						delete.setDate(qqRelation);
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
			message.setDate(groupUser);
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
		message.setDate(nickname);
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
			defaultIcon = new ImageIcon("F:\\ikun.png");
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends GroupList> list, GroupList value, int index, boolean isSelected, boolean cellHasFocus) {
			String gName = value.getGroup().getGName();
			setText(gName);
			if(value.getNums()>0){
				String text = getText();
				text=text+"  未读:"+value.getNums();
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


	private class FriendListCellRenderer extends JLabel implements ListCellRenderer<FriendList> {
        private ImageIcon defaultIcon;

        public FriendListCellRenderer() {
            setOpaque(true);
            setBorder(new EmptyBorder(5, 5, 5, 5));

            // 使用默认头像图标
            defaultIcon = new ImageIcon("F:\\ikun.png");
        }


       @Override
        public Component getListCellRendererComponent(JList<? extends FriendList> list,  FriendList value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {


		 if(value.getUser().getOnline()==1){
			 setText(value.getUser().getNickname()+"  "+"\t在线");


		 }else{
			 setText(value.getUser().getNickname()+"  "+"\t离线");
		 }
		 if(value.getNums()>0){
			 String text = getText();
			 text=text+"  未读:"+value.getNums();
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
	            ChatWindowApp chatWindow = new ChatWindowApp(socket,user.getNickname(),user,this.user,this);
				map.put(user.getUid(),chatWindow);
				//历史消息查询
				Message message = new Message<>();
				QQMessage qqMessage = new QQMessage();
				System.out.println("发送者:"+this.user.getUid());
				qqMessage.setFromUid(this.user.getUid());
				System.out.println("需要接收的"+user.getUid());
				qqMessage.setSendUid(user.getUid());
				message.setDate(qqMessage);
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
						message.setDate(addGroup);
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
						message.setDate(addGroup);
						try {
							NIOObjectUtil.writeObjectToChannel(message,socket);
						} catch (IOException ex) {
							throw new RuntimeException(ex);
						}
						// 在这里执行加入群聊的逻辑
//						System.out.println("加入群聊: " + groupName);

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
}

