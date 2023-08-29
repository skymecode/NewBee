package org.skyme.dao.daoimpl;

import org.skyme.dao.QQGroupMessageStatusDao;
import org.skyme.dao.jdbc.SqlUtil;
import org.skyme.entity.QQGroupMessageStatus;

import java.util.List;

/**
 * @author:Skyme
 * @create: 2023-08-29 16:45
 * @Description:
 */
public class QQGroupMessageDaoImpl implements QQGroupMessageStatusDao {
    @Override
    public List<QQGroupMessageStatus> selectByStatus(Long uid, Long gid) {
        List<QQGroupMessageStatus> statusList = SqlUtil.select(QQGroupMessageStatus.class, "select * from qq_group_message_status where g_m_uid=? and g_m_gid=? ", uid, gid);
        return statusList;
    }
}
