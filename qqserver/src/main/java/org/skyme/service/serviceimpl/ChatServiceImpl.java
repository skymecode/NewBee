package org.skyme.service.serviceimpl;

import org.skyme.core.Message;
import org.skyme.core.Request;
import org.skyme.core.Response;
import org.skyme.dao.QQMessageDao;
import org.skyme.dao.UserDao;
import org.skyme.dao.daoimpl.QQMessageDaoImpl;
import org.skyme.dao.daoimpl.UserDaoImpl;
import org.skyme.core.MessageType;
import org.skyme.entity.QQMessage;
import org.skyme.dao.jdbc.SqlUtil;
import org.skyme.entity.User;
import org.skyme.dao.jdbc.TimeUtil;
import org.skyme.service.ChatService;
import org.skyme.vo.BaseResponse;
import org.skyme.vo.FriendHistory;
import org.skyme.vo.FriendList;

import java.io.IOException;
import java.util.*;

/**
 * @author:Skyme
 * @create: 2023-08-18 09:26
 * @Description:
 */
public class ChatServiceImpl implements ChatService {
    private QQMessageDao qqMessageDao=new QQMessageDaoImpl();

    private UserDao userDao=new UserDaoImpl();



    @Override
    public BaseResponse sendToFriend(Request request, Response response) {
        Message message = request.getMessage();
        MessageType type = message.getType();
        QQMessage data = (QQMessage) message.getData();
        String content = data.getContent();
        Long sendUid = data.getSendUid();//接受者

        Long fromUid = data.getFromUid();//发送者
        Integer status = data.getStatus();
        data.setSendtime(TimeUtil.date2String(new Date()));
        try {
            if (response.isOnline(sendUid)) {
                int i = response.sendMessage(sendUid, message);
                //同时保存在数据库中
                SqlUtil.insert(data);
                message.setCode(i);
            } else {
                //存放到数据库当中
                SqlUtil.insert(data);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<QQMessage> select = qqMessageDao.select(sendUid, fromUid, fromUid, sendUid);
        Message message1 = new Message();
        message1.setCode(1);
        message1.setType(MessageType.SEND_RESULT);
        message1.setData(select);
        return new BaseResponse(message1);
    }

    @Override
    public BaseResponse friendHistory(QQMessage data, Response response) {
        Long sendUid = data.getSendUid();
        Long fromUid = data.getFromUid();
        List<QQMessage> select = qqMessageDao.select(sendUid, fromUid, fromUid, sendUid);
//        List<QQMessage> select = SqlUtil.select(QQMessage.class, "SELECT * from (SELECT * FROM `qq_message` WHERE (send_uid=? and from_uid=?) or (send_uid=? AND from_uid=?) ORDER BY send_time DESC LIMIT 10) AS  s  ORDER BY s.mid asc", sendUid, fromUid, fromUid, sendUid);
        for (QQMessage qqMessage : select) {
            if(!fromUid.equals(qqMessage.getFromUid())){
            qqMessage.setStatus(1);
            int i=qqMessageDao.update(qqMessage);
            }
        }
        List<User> selects = userDao.queryFriends(fromUid);
//        List<User> selects = SqlUtil.select(User.class, "SELECT u.* FROM `qq_user` u INNER JOIN `qq_relation` r ON u.uid = r.fid WHERE r.uid =? AND r.`status` = 1;", fromUid);
        //根据每个好友再去查询我未读的消息
        List<FriendList> lists = new ArrayList<>();

        for (User user : selects) {
            //拿到好友的ID
            Long friendUid = user.getUid();
            //根据我的ID和好友的ID去查询未读消息的数量
            List<Map<String, Object>> select1 = SqlUtil.select("select count(*) from qq_message WHERE from_uid=? AND send_uid=? AND `status`=0 ", friendUid,fromUid);
            Long nums = 0L;
            for (Map<String, Object> stringObjectMap : select1) {
                Set<Map.Entry<String, Object>> entries = stringObjectMap.entrySet();
                for (Map.Entry<String, Object> entry : entries) {
                    if (entry.getValue().getClass() == int.class || entry.getValue().getClass() == Integer.class || entry.getValue().getClass() == Long.class) {
                        nums = (Long) entry.getValue();

                    }
                }
            }
            FriendList friendList = new FriendList(user, nums);
            //封装一个类,存放User和未读消息数量Num
            //然后传给客户端一个集合
            lists.add(friendList);
        }
            try {
                response.flushFriendList(fromUid, lists);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            FriendHistory history = new FriendHistory();
            history.setList(select);
            history.setFromId(fromUid);
            history.setSendId(sendUid);
            Message<FriendHistory> mes = new Message<>();
            mes.setType(MessageType.MES_HISTORY_RESULT);
            mes.setMes("获取历史消息成功");
            mes.setCode(1);
            mes.setData(history);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new BaseResponse(mes);
        }

    @Override
    public BaseResponse historyFriend(QQMessage data, Response response) {
        Long fromUid = data.getFromUid();//发送者
        Long sendUid = data.getSendUid();//接收者
        List<QQMessage> select = qqMessageDao.selectAll(sendUid, fromUid, fromUid, sendUid);
        Message message = new Message();
        message.setData(select);
        message.setType(MessageType.HISTORY_FRIEND_MESSAGE_RESULT);
        message.setCode(1);
        message.setMes("历史消息查询成功");
        return new BaseResponse(message);
    }
}

