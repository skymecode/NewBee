package org.skyme.service.serviceimpl;

import org.skyme.core.Message;
import org.skyme.core.Request;
import org.skyme.core.Response;
import org.skyme.dao.QQRelationDao;
import org.skyme.dao.UserDao;
import org.skyme.dao.daoimpl.QQRelationDaoImpl;
import org.skyme.dao.daoimpl.UserDaoImpl;
import org.skyme.dto.AddFriend;
import org.skyme.core.MessageType;
import org.skyme.entity.QQRelation;
import org.skyme.entity.User;
import org.skyme.dao.jdbc.SqlUtil;
import org.skyme.dao.jdbc.TimeUtil;
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
    private UserDao userDao=new UserDaoImpl();
    private QQRelationDao qqRelationDao = new QQRelationDaoImpl();


    @Override
    public BaseResponse register(User data,Response response) {

        data.setCreateTime(TimeUtil.date2String(new Date()));
        String password = data.getPassword();
        try {
            String md5Str = MD5Util.getMD5Str(password);

            data.setPassword(md5Str);
            int insert = userDao.insert(data);
//            int insert = SqlUtil.insert(date);

            BaseResponse baseResponse = new BaseResponse();
            if(insert>0){
                Message<String> mes = new Message<>();
                mes.setCode(1);//成功
                mes.setData("null");
                mes.setType(MessageType.REG_RESULT);
                mes.setMes("注册成功,现在可以登录了");
                baseResponse.setMessage(mes);
                return baseResponse;
            }else{
                Message<String> mes = new Message<>();
                mes.setCode(0);//失败
                mes.setData("null");
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
    public BaseResponse login(User data, Response response) {
        //查询当前用户
        User user = userDao.queryUserByUsername(data.getUsername());
        BaseResponse baseResponse = new BaseResponse();
        Message mes = new Message<>();
        if(user!=null){
            //说明用户存在,对密码匹配
            String password = data.getPassword();
            String md5Str=null;
            try {
                 md5Str = MD5Util.getMD5Str(password);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }


            if(md5Str.equals(user.getPassword())){
                //密码正确
                user.setLoginTime(TimeUtil.date2String(new Date()));
                user.setOnline(1);
                int up= userDao.update(user);
//                int up = SqlUtil.update(user);

                if(up>0) {
                    mes.setCode(1);//失败
                    mes.setData(user);
                    mes.setType(MessageType.LOG_RESULT);
                    mes.setMes("登录成功");
                    baseResponse.setMessage(mes);
                    List<User> friends = userDao.queryFriends(user.getUid());
                    response.online(user.getUid(),friends);
                    return baseResponse;
                }
                else{
                    mes.setCode(-1);//失败
                    mes.setData("null");
                    mes.setType(MessageType.LOG_RESULT);
                    mes.setMes("账号或密码错误!");
                    baseResponse.setMessage(mes);
                    return baseResponse;
                }
            }else{
                mes.setCode(-1);//失败
                mes.setData("null");
                mes.setType(MessageType.LOG_RESULT);
                mes.setMes("账号或密码错误!");
                baseResponse.setMessage(mes);
                return baseResponse;
            }
        }else{
            mes.setCode(-1);//失败
            mes.setData("null");
            mes.setType(MessageType.LOG_RESULT);
            mes.setMes("账号或密码错误!");
            baseResponse.setMessage(mes);
            return baseResponse;
        }
        }


    @Override
    public BaseResponse queryFriends(User data, Response response) {

        Long uid = data.getUid();
        //查询出好友
        List<User> users = userDao.queryFriends(uid);
//        List<User> select = SqlUtil.select(User.class, "SELECT u.* FROM `qq_user` u INNER JOIN `qq_relation` r ON u.uid = r.fid WHERE r.uid =? AND r.`status` = 1;", uid);
        //根据每个好友再去查询我未读的消息
        List<FriendList> lists = new ArrayList<>();
        for (User user : users) {
            //拿到好友的ID
            Long friendUid = user.getUid();
            //根据我的ID和好友的ID去查询未读消息的数量
            List<Map<String, Object>> maps = userDao.queryCounts(friendUid, uid);
//            List<Map<String, Object>> select1 = SqlUtil.select("select count(*) from qq_message WHERE from_uid=? AND send_uid=? AND `status`=0 ", friendUid,uid );
            Long nums= 0L;
            for (Map<String, Object> stringObjectMap : maps) {
                Set<Map.Entry<String, Object>> entries = stringObjectMap.entrySet();
                for (Map.Entry<String, Object> entry : entries) {

                   if(entry.getValue().getClass() == int.class || entry.getValue().getClass()==Integer.class || entry.getValue().getClass()==Long.class){
                       nums = (Long) entry.getValue();

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
        mes.setData(lists);
        mes.setType(MessageType.FRIENDSLIST_RESULT);
        mes.setMes("查询成功!");
        return new BaseResponse(mes);
    }

    @Override
    public BaseResponse logout(Long uid, Response response) {

        User user = userDao.queryUserById(uid);
        user.setOnline(0);
        int update = userDao.update(user);
//        int update = SqlUtil.update(user);
        List<User> friends = userDao.queryFriends(uid);
//        List<User> friends= SqlUtil.select(User.class, "SELECT u.* FROM `qq_user` u RIGHT JOIN `qq_relation` r ON u.uid = r.uid where r.fid=?", uid);
        Message message = new Message();
        message.setMes("退出");
        message.setData(uid);
        message.setCode(1);
        message.setType(MessageType.LOGOUT_RESULT);
        try {
            response.logout(uid,friends);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new BaseResponse(message);
    }

    @Override
    public BaseResponse deleteFriend(QQRelation qqRelation, Response response) throws IOException {

      //拿到二者的好友关注,状态都设置为0
        Long uid = qqRelation.getUid();
        Long fid = qqRelation.getFid();
        User user = userDao.queryUserById(uid);
//        User user = SqlUtil.selectOne(User.class, "select * from qq_user where uid=?", uid);
        List<QQRelation> list = qqRelationDao.queryRelation(uid, fid, fid, uid);
        for (int i = 0; i < list.size(); i++) {
            QQRelation qqRelation1 = list.get(i);
            qqRelation1.setStatus(0);
            SqlUtil.update(qqRelation1);
            if(!list.get(i).getFid().equals(uid)) {
                //如果好友在线则通知好友
                if (response.isOnline(fid)) {

                    response.deleteFriend(user, fid);
                }
            }
        }
        Message message1 = new Message<>();
        message1.setCode(1);
        message1.setData(null);
        message1.setType(MessageType.DELETE_FRIEND_RESULT);//删除好友的响应
        return new BaseResponse(message1);
    }

    @Override
    public BaseResponse queryLikeUsers(String nickName, Response response) {
        List<User> select= userDao.queryLikeNickName(nickName);
        Message<List<User>> listMessage = new Message<>();
        listMessage.setData(select);
        listMessage.setType(MessageType.QUERY_LIKE_USER_RESULT);
        return new BaseResponse(listMessage);
    }

    @Override
    public BaseResponse addFriend(AddFriend add, Response response) throws IOException {

        if(response.isOnline(add.getSendUser().getUid())){
            //向这个人发送好友请求的通知
            Message mes = new Message<>();
            mes.setType(MessageType.ADDED_FRIEND);
            //向这个加的人发送好友添加请求
            mes.setData(add.getFromUser());
            response.sendAddMessage(add.getSendUser().getUid(), mes);
        }else{
            //不在线一边改为2，一边改为3
            QQRelation qqRelation = new QQRelation();
            qqRelation.setStatus(3);
            qqRelation.setFid(add.getSendUser().getUid());
            qqRelation.setUid(add.getFromUser().getUid());
            List<QQRelation> list = qqRelationDao.queryRelation(add.getFromUser().getUid(), add.getSendUser().getUid(), add.getSendUser().getUid(), add.getFromUser().getUid());
//            List<QQRelation> list= SqlUtil.select(QQRelation.class, "select * from qq_relation where (uid=? and fid=?) or (fid=? and uid=?)", );
            if(list.isEmpty()){
                int insert=  qqRelationDao.insert(qqRelation);
                qqRelation.setFid(add.getFromUser().getUid());
                qqRelation.setUid(add.getSendUser().getUid());
                qqRelation.setStatus(2);
                int insert1 = qqRelationDao.insert(qqRelation);
            }else{
                for (QQRelation relation : list) {
                    if(relation.getUid().equals(add.getFromUser().getUid())){
                        relation.setStatus(3);
                    }else{
                        relation.setStatus(2);
                    }
                    int i = qqRelationDao.update(relation);
                }
            }
        }
        Message message1 = new Message();
        message1.setCode(1);
        message1.setData(null);
        message1.setType(MessageType.NORMAL_RESULT);
        return new BaseResponse(message1);
    }

    @Override
    public BaseResponse acceptFriend(Request request, Response response) throws IOException {
        //接受uid,通知uid，好友已经接受,然后让他刷新好友列表,自己也刷新
        Message message = request.getMessage();
        AddFriend  addFriend= (AddFriend) message.getData();
        Long uid = addFriend.getFromUser().getUid();
        Long fid = addFriend.getSendUser().getUid();
        //向数据库中加入关系
        //先查询,如果存在那么置为1状态否则新建插入
        List<QQRelation> list = SqlUtil.select(QQRelation.class, "SELECT * FROM qq_relation WHERE (uid=? and fid=?) or (uid=? AND fid=?)", uid, fid, fid, uid);
        if(!list.isEmpty()){
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
            message1.setData(addFriend);
            message1.setType(MessageType.SUCCESS_ADD_FRIEND_RESULT);
            response.sendAcceptedMessage(addFriend.getSendUser().getUid(),message1);

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
    public BaseResponse queryInfo(User user, Response response) {
        Long uid = user.getUid();
        //查询关系表中状态为2的
        List<User> list= SqlUtil.select(User.class, "SELECT u.* FROM `qq_user` u INNER JOIN `qq_relation` r ON u.uid = r.fid WHERE r.uid =? AND r.`status` = 2", uid);
        Message<List<User>> listMessage = new Message<>();
        listMessage.setType(MessageType.INFO_RESULT);
        listMessage.setCode(1);
        listMessage.setData(list);
        listMessage.setMes("获取信息成功");
        return new BaseResponse(listMessage);
    }

    @Override
    public BaseResponse modNickname(User user, Response response) {
        int update = userDao.update(user);

//        List<User> users = userDao.queryFriends(user.getUid());
//        for (User user1 : users) {
//            if(response.isOnline(user1.getUid())){
//                List<User> usersF = userDao.queryFriends(user1.getUid());
//
//               response.flushList(user1.getUid(),usersF);
//            }
//        }
        Message<Object> objectMessage = new Message<>();
        objectMessage.setType(MessageType.MOD_NICKNAME_RESULT);
        objectMessage.setCode(1);
        return new BaseResponse();
    }

}
