package org.skyme.dao.daoimpl;

import org.skyme.dao.QQGroupMessageDao;
import org.skyme.dao.jdbc.SqlUtil;
import org.skyme.entity.QQGroupMessage;
import org.skyme.entity.QQGroupMessageStatus;

import java.util.List;

/**
 * @author:Skyme
 * @create: 2023-08-29 16:41
 * @Description:
 */
public class QQGroupMessageDaoImp implements QQGroupMessageDao {
    @Override
    public List<QQGroupMessage> selectById(Long gid) {
        List<QQGroupMessage> messages = SqlUtil.select(QQGroupMessage.class, "SELECT * FROM `qq_group_message`  g LEFT  JOIN qq_user q on g.g_from_uid=q.uid where g.g_gid=? ORDER BY g_create_time asc", gid);
        return messages;
    }

    @Override
    public int update(QQGroupMessageStatus qqGroupMessageStatus) {
        int update = SqlUtil.update(qqGroupMessageStatus);
        return update;
    }
}
