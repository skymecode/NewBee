package org.skyme.service.serviceimpl;

import org.skyme.core.Request;
import org.skyme.core.Response;
import org.skyme.dto.Message;
import org.skyme.dto.MessageType;
import org.skyme.entity.QQMessage;
import org.skyme.dao.SqlUtil;
import org.skyme.entity.User;
import org.skyme.jdbc.TimeUtil;
import org.skyme.service.ChatService;
import org.skyme.service.UserService;
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

    private UserService userService = new UserServiceImpl();

    @Override
    public BaseResponse sendToFriend(Request request, Response response) {
        Message message = request.getMessage();
        MessageType type = message.getType();
        QQMessage data = (QQMessage) message.getDate();
        String content = data.getContent();
        Long sendUid = data.getSendUid();//接受者
        System.out.println("服务器转发的sendid" + sendUid);
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

        message.setType(MessageType.SEND_RESULT);
        return new BaseResponse(message);
    }

    @Override
    public BaseResponse friendHistory(Request request, Response response) {

        Message message = request.getMessage();

        QQMessage data = (QQMessage) message.getDate();

        Long sendUid = data.getSendUid();

        Long fromUid = data.getFromUid();

        List<QQMessage> select = SqlUtil.select(QQMessage.class, "SELECT * from (SELECT * FROM `qq_message` WHERE (send_uid=? and from_uid=?) or (send_uid=? AND from_uid=?) ORDER BY send_time DESC LIMIT 10) AS  s  ORDER BY s.mid asc", sendUid, fromUid, fromUid, sendUid);

        for (QQMessage qqMessage : select) {
            if(!fromUid.equals(qqMessage.getFromUid())){
            qqMessage.setStatus(1);
            SqlUtil.update(qqMessage);
            }
        }
        List<User> selects = SqlUtil.select(User.class, "SELECT u.* FROM `qq_user` u INNER JOIN `qq_relation` r ON u.uid = r.fid WHERE r.uid =? AND r.`status` = 1;", fromUid);
        //根据每个好友再去查询我未读的消息
        List<FriendList> lists = new ArrayList<>();

        for (User user : selects) {
            //拿到好友的ID
            Long friendUid = user.getUid();
            System.out.println("当前好友ID"+friendUid);
            //根据我的ID和好友的ID去查询未读消息的数量
            List<Map<String, Object>> select1 = SqlUtil.select("select count(*) from qq_message WHERE from_uid=? AND send_uid=? AND `status`=0 ", friendUid,fromUid);
            Long nums = 0L;
            for (Map<String, Object> stringObjectMap : select1) {
                Set<Map.Entry<String, Object>> entries = stringObjectMap.entrySet();
                for (Map.Entry<String, Object> entry : entries) {

                    if (entry.getValue().getClass() == int.class || entry.getValue().getClass() == Integer.class || entry.getValue().getClass() == Long.class) {
                        nums = (Long) entry.getValue();
                        System.out.println("未读消息:" + nums);
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
            mes.setDate(history);
            return new BaseResponse(mes);

        }
    }

