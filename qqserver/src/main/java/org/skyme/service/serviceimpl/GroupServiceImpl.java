package org.skyme.service.serviceimpl;

import org.skyme.core.Message;
import org.skyme.core.MessageType;
import org.skyme.core.Request;
import org.skyme.core.Response;
import org.skyme.dao.QQGroupDao;
import org.skyme.dao.QQGroupMessageDao;
import org.skyme.dao.QQGroupMessageStatusDao;
import org.skyme.dao.QQGroupRelationDao;
import org.skyme.dao.daoimpl.QQGroupDaoImpl;
import org.skyme.dao.daoimpl.QQGroupMessageDaoImp;
import org.skyme.dao.daoimpl.QQGroupMessageDaoImpl;
import org.skyme.dao.daoimpl.QQGroupRelationDaoImpl;
import org.skyme.dao.jdbc.SqlUtil;
import org.skyme.dto.*;
import org.skyme.entity.*;
import org.skyme.service.GroupService;
import org.skyme.vo.BaseResponse;
import org.skyme.vo.GroupList;

import java.util.*;

/**
 * @author:Skyme
 * @create: 2023-08-21 00:07
 * @Description:
 */
public class GroupServiceImpl implements GroupService {

    private QQGroupDao qqGroupDao=new QQGroupDaoImpl();

    private QQGroupMessageDao groupMessageDao = new QQGroupMessageDaoImp();

    private QQGroupMessageStatusDao groupStatusDao = new QQGroupMessageDaoImpl();

    private QQGroupRelationDao groupRelationDao=new QQGroupRelationDaoImpl();

    @Override
    public BaseResponse queryGroup(User user, Response response) {

        List<QQGroup> groupList = qqGroupDao.selectByUid(user.getUid());
//        List<QQGroup> groupList = SqlUtil.select(QQGroup.class, "select g.* from qq_group g left JOIN qq_group_relation u on g.gid=u.g_gid where u.g_uid=? and u.`status`=1", user.getUid());
        List<GroupList> list=new ArrayList<>();
        for (QQGroup qqGroup : groupList) {
            List<Map<String, Object>> select1= qqGroupDao.selectCount(qqGroup.getGid(),user.getUid());
//            List<Map<String, Object>> select1 = SqlUtil.select("select count(*) from qq_group_message_status WHERE g_m_gid=? AND g_m_uid=? AND `status`=0 ", qqGroup.getGid(),user.getUid() );
            Long nums= 0L;
            for (Map<String, Object> stringObjectMap : select1) {
                Set<Map.Entry<String, Object>> entries = stringObjectMap.entrySet();
                for (Map.Entry<String, Object> entry : entries) {

                    if(entry.getValue().getClass() == int.class || entry.getValue().getClass()==Integer.class || entry.getValue().getClass()==Long.class){
                        nums = (Long) entry.getValue();

                    }
                }
            }
            GroupList groupList1 = new GroupList();
            groupList1.setGroup(qqGroup);
            groupList1.setNums(nums);
            list.add(groupList1);
        }

        Message<List<GroupList>> listMessage = new Message<>();
        listMessage.setData(list);
        listMessage.setType(MessageType.GROUP_LIST_RESULT);
        return new BaseResponse(listMessage);
    }

    @Override
    public BaseResponse messageHistory(GroupUser user, Response response) {

        Long gid = user.getGid();
        Long uid = user.getUid();
        List<QQGroupMessage> messages=groupMessageDao.selectById(gid);
//        List<QQGroupMessage> messages = SqlUtil.select(QQGroupMessage.class, "SELECT * FROM `qq_group_message`  g LEFT  JOIN qq_user q on g.g_from_uid=q.uid where g.g_gid=? ORDER BY g_create_time asc", gid);
        //顺带把这个消息刷新成已读
        List<QQGroupMessageStatus> statusList =groupStatusDao.selectByStatus(uid, gid);
//        List<QQGroupMessageStatus> statusList = SqlUtil.select(QQGroupMessageStatus.class, "select * from qq_group_message_status where g_m_uid=? and g_m_gid=? ", uid, gid);
        for (QQGroupMessageStatus qqGroupMessageStatus : statusList) {
            qqGroupMessageStatus.setStatus(1);
            int i=groupMessageDao.update(qqGroupMessageStatus);
//            SqlUtil.update(qqGroupMessageStatus);
        }
        Message<List<QQGroupMessage>> listMessage = new Message<>();
        listMessage.setData(messages);
        listMessage.setType(MessageType.GROUP_MESSAGE_HISTORY_RESULT);
        listMessage.setCode(1);
        listMessage.setMes("查询群聊成功");
        return new BaseResponse(listMessage);
    }
    public BaseResponse send(QQGroupMessage groupMessage, Response response) {

        User user = groupMessage.getUser();

        String gContent = groupMessage.getgContent();
        Long gid = groupMessage.getGid();

        int i = SqlUtil.insertRetrunID(groupMessage);


        QQGroupMessageStatus qqGroupMessageStatus = new QQGroupMessageStatus();
        //拿到所有群成员
        List<User> userList = qqGroupDao.selectAllUsers(gid);
//        List<User> userList = SqlUtil.select(User.class, "SELECT b.* FROM `qq_group_relation` q RIGHT JOIN qq_user b ON q.g_uid=b.uid where q.g_gid=? ", gid);
        ArrayList<QQGroupMessageStatus> statusList = new ArrayList<>();
        for (User groupMember : userList) {
            QQGroupMessageStatus qqGroupMessageStatus1 = new QQGroupMessageStatus();
            qqGroupMessageStatus1.setgMMid(groupMessage.getgMid());
            qqGroupMessageStatus1.setgMGid(gid);
            if(!groupMember.getUid().equals( user.getUid())){
                qqGroupMessageStatus1.setStatus(0);
            }else{
                qqGroupMessageStatus1.setStatus(1);
            }

            qqGroupMessageStatus1.setgMUid(groupMember.getUid());
            statusList.add(qqGroupMessageStatus1);

        }

        SqlUtil.batchInsert(statusList);
        //拿到发的人和发的群
        Long uid = user.getUid();
        Message message = new Message();
        message.setType(MessageType.GROUP_SEND_RESULT);
        message.setData(groupMessage);
        message.setCode(1);
        //让所有在线的群成员都去刷新状态
        for (User user1 : userList) {
            if(response.isOnline(user1.getUid())&& !Objects.equals(user1.getUid(), user.getUid())){

                response.sendGroupMessage(user1.getUid(),message);
            }
        }
        return new BaseResponse(message);
    }
    public BaseResponse addGroup(Request request, Response response) {
        Message message = request.getMessage();
        AddGroup addGroup= (AddGroup) message.getData();
        Message message1 = new Message();
        message1.setType(MessageType.CREATE_OR_JOIN_GROUP_RESULT);
        message1.setCode(1);
        if(message.getType()==MessageType.CREATE_GROUP){
            //先判断群名称重复没有
            List<QQGroup> qqGroup1 =qqGroupDao.selectByName(addGroup.getGroupName());
//            List<QQGroup> qqGroup1 = SqlUtil.select(QQGroup.class, "select * from qq_group where g_name=?", addGroup.getGroupName());
            if(qqGroup1.size() == 0){
            QQGroup qqGroup = new QQGroup();
            qqGroup.setGName(addGroup.getGroupName());
            qqGroup.setStatus(1);
            qqGroup.setGOwner(addGroup.getUid());
            //直接创建群
            int insert = SqlUtil.insertRetrunID(qqGroup);
                QQGroupRelation qqGroupRelation = new QQGroupRelation();
                qqGroupRelation.setGGid(qqGroup.getGid());
                qqGroupRelation.setStatus(1);
                qqGroupRelation.setGUid(addGroup.getUid());
                int insert1 =groupRelationDao.insert(qqGroupRelation);
//                int insert1 = SqlUtil.insert(qqGroupRelation);
                message1.setMes("创建成功");
            }else {
                message1.setData(null);
                message1.setCode(0);
                message1.setMes("创建群失败,群名称已经存在!");
            }
        }else{
            //数据库搜索名称匹配的群
            try {
                List<QQGroup> qqGroups = qqGroupDao.selectByName(addGroup.getGroupName());
                QQGroup qqGroup=qqGroups.get(0);
//                QQGroup qqGroup = SqlUtil.selectOne(QQGroup.class, "select * from qq_group where g_name=?", addGroup.getGroupName());
                //如果有直接进群
                if(qqGroup != null){
                    //判断是否已经进过这个群了
                    try {
                        QQGroupRelation qqGroupRelation1 = groupRelationDao.selectOne(qqGroup.getGid(), addGroup.getUid());
                        if(qqGroupRelation1!=null){
                            qqGroupRelation1.setStatus(1);
                            SqlUtil.update(qqGroupRelation1);
                        }
                    }catch (IndexOutOfBoundsException e){
                        //进群
                        QQGroupRelation qqGroupRelation = new QQGroupRelation();
                        qqGroupRelation.setGGid(qqGroup.getGid());
                        qqGroupRelation.setStatus(1);
                        qqGroupRelation.setGUid(addGroup.getUid());
                        int insert = groupRelationDao.insert(qqGroupRelation);
//                        int insert = SqlUtil.insert(qqGroupRelation);
                    }
                    message1.setCode(2);
                    message1.setMes("加入成功");
                }
            }catch (IndexOutOfBoundsException e){

                e.printStackTrace();
                //没有返回未查找到
                message1.setData(null);
                message1.setCode(0);
                message1.setMes("未找到该群");
            }
        }
        return new BaseResponse(message1);

    }

    @Override
    public BaseResponse quitGroup(RemoveGroup removeGroup, Response response) {

        Long gid = removeGroup.getGid();
        Long uid = removeGroup.getUid();
        QQGroupRelation qqGroupRelation = groupRelationDao.selectOne(gid, uid);
//       QQGroupRelation qqGroupRelation = SqlUtil.selectOne(QQGroupRelation.class, "select * from qq_group_relation where g_gid=? and g_uid =?", gid, uid);
       qqGroupRelation.setStatus(0);
       int i=groupRelationDao.update(qqGroupRelation);
        Message<Object> result= new Message<>();
        result.setType(MessageType.QUIT_GROUP_RESULT);
        result.setCode(1);
        result.setMes("退出成功");
        result.setData(null);
        return new BaseResponse(result);
    }

    @Override
    public BaseResponse queryMembers(Long gid, Response response) {
        //获取群成员
        List<User> list=qqGroupDao.selectMembers(gid);
        Message<List<User>> listMessage = new Message<>();
        listMessage.setData(list);
        listMessage.setType(MessageType.QUERY_GROUP_MEMBER_RESULT);
        listMessage.setCode(1);
        listMessage.setMes("获取群成员成功");
        return new BaseResponse(listMessage);
    }
}
