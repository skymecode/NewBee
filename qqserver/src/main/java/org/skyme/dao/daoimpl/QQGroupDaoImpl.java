package org.skyme.dao.daoimpl;

import org.skyme.dao.QQGroupDao;
import org.skyme.dao.jdbc.SqlUtil;
import org.skyme.entity.QQGroup;
import org.skyme.entity.User;

import java.util.List;
import java.util.Map;

/**
 * @author:Skyme
 * @create: 2023-08-29 16:35
 * @Description:
 */
public class QQGroupDaoImpl implements QQGroupDao {
    @Override
    public List<QQGroup> selectByUid(Long uid) {
        List<QQGroup> groupList = SqlUtil.select(QQGroup.class, "select g.* from qq_group g left JOIN qq_group_relation u on g.gid=u.g_gid where u.g_uid=? and u.`status`=1",uid);
        return groupList;
    }

    @Override
    public List<Map<String, Object>> selectCount(Long gid, Long uid) {
        List<Map<String, Object>> select1 = SqlUtil.select("select count(*) from qq_group_message_status WHERE g_m_gid=? AND g_m_uid=? AND `status`=0 ", gid,uid );
        return select1;
    }

    @Override
    public List<User> selectAllUsers(Long gid) {
        List<User> userList = SqlUtil.select(User.class, "SELECT b.* FROM `qq_group_relation` q RIGHT JOIN qq_user b ON q.g_uid=b.uid where q.g_gid=? ", gid);
        return userList;
    }

    @Override
    public List<QQGroup> selectByName(String groupName) {
        List<QQGroup> qqGroup1 = SqlUtil.select(QQGroup.class, "select * from qq_group where g_name=?", groupName);
        return qqGroup1;
    }

    @Override
    public List<User> selectMembers(Long gid) {
        List<User> select = SqlUtil.select(User.class, "SELECT * FROM qq_user q LEFT JOIN qq_group_relation  b ON q.uid=b.g_uid where b.g_gid=?", gid);
        return select;
    }
}
