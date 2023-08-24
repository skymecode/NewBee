package org.skyme.service.serviceimpl;

import org.skyme.core.Request;
import org.skyme.core.Response;
import org.skyme.dao.SqlUtil;
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

    @Override
    public BaseResponse queryGroup(Request request, Response response) {
        System.out.println("获取群");
        Message message = request.getMessage();
       User user= (User) message.getDate();
        List<QQGroup> groupList = SqlUtil.select(QQGroup.class, "select g.* from qq_group g left JOIN qq_group_relation u on g.gid=u.g_gid where u.g_uid=? and u.`status`=1", user.getUid());
        List<GroupList> list=new ArrayList<>();
        for (QQGroup qqGroup : groupList) {
            List<Map<String, Object>> select1 = SqlUtil.select("select count(*) from qq_group_message_status WHERE g_m_gid=? AND g_m_uid=? AND `status`=0 ", qqGroup.getGid(),user.getUid() );
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
            GroupList groupList1 = new GroupList();
            groupList1.setGroup(qqGroup);
            groupList1.setNums(nums);
            list.add(groupList1);
        }
        System.out.println("群组的大小:"+list.size());
        Message<List<GroupList>> listMessage = new Message<>();
        listMessage.setDate(list);
        listMessage.setType(MessageType.GROUP_LIST_RESULT);
        return new BaseResponse(listMessage);
    }

    @Override
    public BaseResponse messageHistory(Request request, Response response) {
        Message message = request.getMessage();
        GroupUser user = (GroupUser) message.getDate();
        Long gid = user.getGid();
        Long uid = user.getUid();
        List<QQGroupMessage> messages = SqlUtil.select(QQGroupMessage.class, "SELECT * FROM `qq_group_message`  g LEFT  JOIN qq_user q on g.g_from_uid=q.uid where g.g_gid=? ORDER BY g_create_time asc", gid);
        //顺带把这个消息刷新成已读
        List<QQGroupMessageStatus> statusList = SqlUtil.select(QQGroupMessageStatus.class, "select * from qq_group_message_status where g_m_uid=? and g_m_gid=? ", uid, gid);
        for (QQGroupMessageStatus qqGroupMessageStatus : statusList) {
            qqGroupMessageStatus.setStatus(1);
            SqlUtil.update(qqGroupMessageStatus);
        }
        Message<List<QQGroupMessage>> listMessage = new Message<>();
        listMessage.setDate(messages);
        listMessage.setType(MessageType.GROUP_MESSAGE_HISTORY_RESULT);
        listMessage.setCode(1);
        listMessage.setMes("查询群聊成功");
        return new BaseResponse(listMessage);
    }
    public BaseResponse send(Request request, Response response) {
        Message message = request.getMessage();
        QQGroupMessage groupMessage = (QQGroupMessage) message.getDate();
        User user = groupMessage.getUser();
        System.out.println("当前发送消息的用户"+user.getNickname());
        String gContent = groupMessage.getgContent();
        Long gid = groupMessage.getGid();
        int i = SqlUtil.insertRetrunID(groupMessage);

        System.out.println("生成的id"+groupMessage.getgMid());
        QQGroupMessageStatus qqGroupMessageStatus = new QQGroupMessageStatus();
        //拿到所有群成员
        List<User> userList = SqlUtil.select(User.class, "SELECT b.* FROM `qq_group_relation` q RIGHT JOIN qq_user b ON q.g_uid=b.uid where q.g_gid=? ", gid);
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
            System.out.println("群成员:"+groupMember.getNickname());
            System.out.println("id:"+groupMember.getUid());
            qqGroupMessageStatus1.setgMUid(groupMember.getUid());
            statusList.add(qqGroupMessageStatus1);

        }
        System.out.println("开始批量插入");
        SqlUtil.batchInsert(statusList);
        //拿到发的人和发的群
        Long uid = user.getUid();
        message.setType(MessageType.GROUP_SEND_RESULT);
        //让所有在线的群成员都去刷新状态
        for (User user1 : userList) {
            if(response.isOnline(user1.getUid())&& !Objects.equals(user1.getUid(), user.getUid())){
                System.out.println("发送给群成员"+user1.getNickname());
                response.sendGroupMessage(user1.getUid(),message);
            }
        }
        return new BaseResponse(message);
    }
    public BaseResponse addGroup(Request request, Response response) {
        Message message = request.getMessage();
        AddGroup addGroup= (AddGroup) message.getDate();
        Message message1 = new Message();
        message1.setType(MessageType.CREATE_OR_JOIN_GROUP_RESULT);
        message1.setCode(1);
        if(message.getType()==MessageType.CREATE_GROUP){
            //先判断群名称重复没有
            List<QQGroup> qqGroup1 = SqlUtil.select(QQGroup.class, "select * from qq_group where g_name=?", addGroup.getGroupName());
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
                int insert1 = SqlUtil.insert(qqGroupRelation);
                message1.setMes("创建成功");
            }else {
                message1.setDate(null);
                message1.setCode(0);
                message1.setMes("创建群失败,群名称已经存在!");
            }

        }else{
            //数据库搜索名称匹配的群
            QQGroup qqGroup = SqlUtil.selectOne(QQGroup.class, "select * from qq_group where g_name=?", addGroup.getGroupName());
            //如果有直接进群
            if(qqGroup != null){
                //判断是否已经进过这个群了
                QQGroupRelation qqGroupRelation1 = SqlUtil.selectOne(QQGroupRelation.class, "select * from qq_group where g_gid=? and g_uid=?", qqGroup.getGid(), addGroup.getUid());
                if(qqGroupRelation1!=null){
                    qqGroupRelation1.setStatus(1);
                    SqlUtil.update(qqGroupRelation1);
                }else{
                    //进群
                    QQGroupRelation qqGroupRelation = new QQGroupRelation();
                    qqGroupRelation.setGGid(qqGroup.getGid());
                    qqGroupRelation.setStatus(1);
                    qqGroupRelation.setGUid(addGroup.getUid());

                    int insert = SqlUtil.insert(qqGroupRelation);
                }
                message1.setCode(2);
                message1.setMes("加入成功");

            }else{
                //没有返回未查找到
                message1.setDate(null);
                message1.setCode(0);
                message1.setMes("未找到该群");
            }
        }
        return new BaseResponse(message1);

    }

    @Override
    public BaseResponse quitGroup(Request request, Response response) {
        Message message = request.getMessage();
       RemoveGroup removeGroup = (RemoveGroup) message.getDate();
        Long gid = removeGroup.getGid();
        Long uid = removeGroup.getUid();
       QQGroupRelation qqGroupRelation = SqlUtil.selectOne(QQGroupRelation.class, "select * from qq_group_relation where g_gid=? and g_uid =?", gid, uid);
       qqGroupRelation.setStatus(0);
        SqlUtil.update(qqGroupRelation);
        Message<Object> result= new Message<>();
        result.setType(MessageType.QUIT_GROUP_RESULT);
        result.setCode(1);
        result.setMes("退出成功");
        result.setDate(null);
        return new BaseResponse(result);
    }
}
