package org.skyme.service.serviceimpl;

import org.skyme.core.Request;
import org.skyme.core.Response;
import org.skyme.dto.AddFriend;
import org.skyme.dto.Message;
import org.skyme.dto.MessageType;
import org.skyme.entity.QQRelation;
import org.skyme.entity.User;
import org.skyme.dao.SqlUtil;
import org.skyme.jdbc.TimeUtil;
import org.skyme.service.UserService;
import org.skyme.util.MD5Util;
import org.skyme.vo.BaseResponse;
import org.skyme.vo.FriendList;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @author:Skyme
 * @create: 2023-08-17 16:23
 * @Description:
 */
public class UserServiceImpl implements UserService {
    @Override
    public BaseResponse register(Request request,Response response) {
        Message message = request.getMessage();
        User date = (User) message.getDate();
        date.setCreateTime(TimeUtil.date2String(new Date()));
        String password = date.getPassword();
        try {
            String md5Str = MD5Util.getMD5Str(password);
            System.out.println("加密后的"+md5Str);
            date.setPassword(md5Str);
            int insert = SqlUtil.insert(date);
            System.out.println("注册状态"+insert);
            BaseResponse baseResponse = new BaseResponse();
            if(insert>0){
                Message<String> mes = new Message<>();
                mes.setCode(1);//成功
                mes.setDate("null");
                mes.setType(MessageType.REG_RESULT);
                mes.setMes("注册成功,现在可以登录了");
                baseResponse.setMessage(mes);
                return baseResponse;
            }else{
                Message<String> mes = new Message<>();
                mes.setCode(0);//失败
                mes.setDate("null");
                mes.setType(MessageType.REG_RESULT);
                mes.setMes("注册失败,当前账号可能已存在或你输入的格式有误");
                baseResponse.setMessage(mes);
                return baseResponse;
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public BaseResponse login(Request request, Response response) {

        //获取请求的数据
        Message message = request.getMessage();
        User data = (User) message.getDate();
        //查询当前用户
        User user = SqlUtil.selectOne(User.class, "select * from qq_user where username=?", data.getUsername());
        BaseResponse baseResponse = new BaseResponse();
        Message mes = new Message<>();
        if(user.getUid()>0){
            //说明用户存在,对密码匹配
            String password = user.getPassword();
            String md5Str=null;
            try {
                 md5Str = MD5Util.getMD5Str(password);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            if(md5Str.equals(data.getPassword())){
                //密码正确
                user.setLoginTime(TimeUtil.date2String(new Date()));
                user.setOnline(1);
                int up = SqlUtil.update(user);
                System.out.println("用户登录数据更新");
                if(up>0) {
                    mes.setCode(1);//失败
                    mes.setDate(user);
                    mes.setType(MessageType.LOG_RESULT);
                    mes.setMes("登录成功");
                    baseResponse.setMessage(mes);
                    List<User> friends= SqlUtil.select(User.class, "SELECT u.* FROM `qq_user` u RIGHT JOIN `qq_relation` r ON u.uid = r.uid where r.fid=?", user.getUid());
                    response.online(user.getUid(),friends);
                    return baseResponse;
                }
                else{
                    mes.setCode(-1);//失败
                    mes.setDate("null");
                    mes.setType(MessageType.LOG_RESULT);
                    mes.setMes("账号或密码错误!");
                    baseResponse.setMessage(mes);
                    return baseResponse;
                }
            }else{
                mes.setCode(-1);//失败
                mes.setDate("null");
                mes.setType(MessageType.LOG_RESULT);
                mes.setMes("账号或密码错误!");
                baseResponse.setMessage(mes);
                return baseResponse;
            }
        }else{
            mes.setCode(-1);//失败
            mes.setDate("null");
            mes.setType(MessageType.LOG_RESULT);
            mes.setMes("账号或密码错误!");
            baseResponse.setMessage(mes);
            return baseResponse;
        }
        }


    @Override
    public BaseResponse queryFriends(Request request, Response response) {
        System.out.println("有好友查询请求");
        Message message = request.getMessage();
        User data = (User) message.getDate();
        Long uid = data.getUid();
        //查询出好友
        List<User> select = SqlUtil.select(User.class, "SELECT u.* FROM `qq_user` u INNER JOIN `qq_relation` r ON u.uid = r.fid WHERE r.uid =? AND r.`status` = 1;", uid);
        //根据每个好友再去查询我未读的消息
        List<FriendList> lists = new ArrayList<>();
        for (User user : select) {
            //拿到好友的ID
            Long friendUid = user.getUid();
            //根据我的ID和好友的ID去查询未读消息的数量

            List<Map<String, Object>> select1 = SqlUtil.select("select count(*) from qq_message WHERE from_uid=? AND send_uid=? AND `status`=0 ", friendUid,uid );
            Long nums= 0L;
            for (Map<String, Object> stringObjectMap : select1) {
                Set<Map.Entry<String, Object>> entries = stringObjectMap.entrySet();
                for (Map.Entry<String, Object> entry : entries) {

                   if(entry.getValue().getClass() == int.class || entry.getValue().getClass()==Integer.class || entry.getValue().getClass()==Long.class){
                       nums = (Long) entry.getValue();
                       System.out.println("未读消息:"+nums);
                   }
                }
            }
            FriendList friendList = new FriendList(user,nums);


            //封装一个类,存放User和未读消息数量Num

            //然后传给客户端一个集合
            lists.add(friendList);

        }
        Message mes = new Message<>();
        mes.setCode(1);
        mes.setDate(lists);
        mes.setType(MessageType.FRIENDSLIST_RESULT);
        mes.setMes("查询成功!");
//        System.out.println("查询成功"+select.get(0).getNickname());
        return new BaseResponse(mes);
    }

    @Override
    public BaseResponse logout(Request request, Response response) {
        Message message = request.getMessage();
        Long uid = (Long) message.getDate();
        User user = SqlUtil.selectOne(User.class, "select * from qq_user where uid=?", uid);
        user.setOnline(0);
        int update = SqlUtil.update(user);
        List<User> friends= SqlUtil.select(User.class, "SELECT u.* FROM `qq_user` u RIGHT JOIN `qq_relation` r ON u.uid = r.uid where r.fid=?", uid);
        message.setType(MessageType.LOGOUT_RESULT);
        try {
            response.logout(uid,friends);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new BaseResponse(message);
    }

    @Override
    public BaseResponse deleteFriend(Request request, Response response) throws IOException {
        Message message = request.getMessage();
        QQRelation qqRelation = (QQRelation) message.getDate();
      //拿到二者的好友关注,状态都设置为0
        Long uid = qqRelation.getUid();
        Long fid = qqRelation.getFid();
        User user = SqlUtil.selectOne(User.class, "select * from qq_user where uid=?", uid);
        List<QQRelation> list = SqlUtil.select(QQRelation.class, "SELECT * FROM qq_relation WHERE (uid=? and fid=?) or (uid=? AND fid=?)", uid, fid, fid, uid);
        for (int i = 0; i < list.size(); i++) {
            QQRelation qqRelation1 = list.get(i);
            qqRelation1.setStatus(0);
            SqlUtil.update(qqRelation1);
            if(!list.get(i).getFid().equals(uid)) {
                //如果好友在线则通知好友
                if (response.isOnline(fid)) {
                    System.out.println("已经向" + fid + "发送通知了");
                    response.deleteFriend(user, fid);
                }
            }
        }


        Message message1 = new Message<>();
        message1.setCode(1);
        message1.setDate(null);
        message1.setType(MessageType.DELETE_FRIEND_RESULT);//删除好友的响应
        return new BaseResponse(message1);
    }

    @Override
    public BaseResponse queryLikeUsers(Request request, Response response) {
        Message message = request.getMessage();
        String nickName = (String) message.getDate();
        List<User> select = SqlUtil.select(User.class, "select * from qq_user where nickname like ?", nickName);
        Message<List<User>> listMessage = new Message<>();
        listMessage.setDate(select);
        listMessage.setType(MessageType.QUERY_LIKE_USER_RESULT);
        return new BaseResponse(listMessage);
    }

    @Override
    public BaseResponse addFriend(Request request, Response response) throws IOException {
        Message message = request.getMessage();
        AddFriend add = (AddFriend) message.getDate();
        if(response.isOnline(add.getSendUser().getUid())){
            //向这个人发送好友请求的通知
            Message mes = new Message<>();
            mes.setType(MessageType.ADDED_FRIEND);
            //向这个加的人发送好友添加请求
            mes.setDate(add.getFromUser());
            response.sendAddMessage(add.getSendUser().getUid(), mes);
        }else{
            System.out.println("不在线加好友");
            //这里暂时只能在线加好友,后面会实现消息中心功能
            //不在线则把这二者的关系改成2,待加好友
            QQRelation qqRelation = new QQRelation();
            qqRelation.setStatus(2);
            qqRelation.setFid(add.getSendUser().getUid());
            qqRelation.setUid(add.getFromUser().getUid());
            List<QQRelation> list= SqlUtil.select(QQRelation.class, "select * from qq_relation where (uid=? and fid=?) or (fid=? and uid=?)", add.getFromUser().getUid(), add.getSendUser().getUid(), add.getFromUser().getUid(), add.getSendUser().getUid());
            if(list.isEmpty()){
                int insert = SqlUtil.insert(qqRelation);
                qqRelation.setFid(add.getFromUser().getUid());
                qqRelation.setUid(add.getSendUser().getUid());
                qqRelation.setStatus(2);
                SqlUtil.insert(qqRelation);
            }else{
                for (QQRelation relation : list) {
                    relation.setStatus(2);
                    SqlUtil.update(relation);
                }

            }
        }
        Message message1 = new Message();
        message1.setType(MessageType.NORMAL_RESULT);
        return new BaseResponse(message1);
    }

    @Override
    public BaseResponse acceptFriend(Request request, Response response) throws IOException {
        //接受uid,通知uid，好友已经接受,然后让他刷新好友列表,自己也刷新
        Message message = request.getMessage();
        AddFriend  addFriend= (AddFriend) message.getDate();
        Long uid = addFriend.getFromUser().getUid();
        Long fid = addFriend.getSendUser().getUid();
        //向数据库中加入关系
        //先查询,如果存在那么置为1状态否则新建插入
        List<QQRelation> list = SqlUtil.select(QQRelation.class, "SELECT * FROM qq_relation WHERE (uid=? and fid=?) or (uid=? AND fid=?)", uid, fid, fid, uid);
        if(list != null){
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setStatus(1);
                SqlUtil.update(list.get(i));
            }
        }else{
            QQRelation qqRelation = new QQRelation();
            qqRelation.setUid(addFriend.getSendUser().getUid());
            qqRelation.setFid(addFriend.getFromUser().getUid());
            qqRelation.setStatus(1);
            QQRelation qqRelation1 = new QQRelation();
            qqRelation1.setFid(addFriend.getSendUser().getUid());
            qqRelation1.setUid(addFriend.getFromUser().getUid());
            qqRelation1.setStatus(1);
            SqlUtil.insert(qqRelation);
            SqlUtil.insert(qqRelation1);
        }

        if(response.isOnline(addFriend.getSendUser().getUid())){
            Message message1 = new Message();
            message1.setDate(addFriend);
            message1.setType(MessageType.SUCCESS_ADD_FRIEND_RESULT);
            response.sendAcceptedMessage(addFriend.getSendUser().getUid(),message1);
            System.out.println("发送刷新列表响应");
        }
        Message message1 = new Message();
        message1.setType(MessageType.NORMAL_RESULT);
        return new BaseResponse(message1);

    }

    @Override
    public BaseResponse refuseFriend(Request request, Response response) {
        return null;
    }

    @Override
    public BaseResponse queryInfo(Request request, Response response) {
        Message<User> message = request.getMessage();
        User user = message.getDate();
        Long uid = user.getUid();
        //查询关系表中状态为2的
        List<User> list= SqlUtil.select(User.class, "SELECT u.* FROM `qq_user` u INNER JOIN `qq_relation` r ON u.uid = r.fid WHERE r.uid =? AND r.`status` = 2", uid);
        Message<List<User>> listMessage = new Message<>();
        listMessage.setType(MessageType.INFO_RESULT);
        listMessage.setCode(1);
        listMessage.setDate(list);
        listMessage.setMes("获取信息成功");
        return new BaseResponse(listMessage);
    }

}
