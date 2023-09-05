package org.skyme.dao.daoimpl;

import org.skyme.dao.QQMessageDao;
import org.skyme.dao.jdbc.SqlUtil;
import org.skyme.entity.QQMessage;

import java.util.List;

/**
 * @author:Skyme
 * @create: 2023-08-29 17:05
 * @Description:
 */
public class QQMessageDaoImpl implements QQMessageDao {
    @Override
    public List<QQMessage> select(Long sendUid, Long fromUid, Long fromUid1, Long sendUid1) {

                 List<QQMessage> select = SqlUtil.select(QQMessage.class, "SELECT * from (SELECT * FROM `qq_message` WHERE (send_uid=? and from_uid=?) or (send_uid=? AND from_uid=?) ORDER BY send_time DESC LIMIT 4) AS  s  ORDER BY s.mid asc", sendUid, fromUid, fromUid, sendUid);
                 return  select;
    }

    @Override
    public int update(QQMessage qqMessage) {
        int update = SqlUtil.update(qqMessage);
        return update;
    }

    @Override
    public List<QQMessage> selectAll(Long sendUid, Long fromUid, Long fromUid1, Long sendUid1) {
        List<QQMessage> select = SqlUtil.select(QQMessage.class, "SELECT * from (SELECT * FROM `qq_message` WHERE (send_uid=? and from_uid=?) or (send_uid=? AND from_uid=?) ORDER BY send_time DESC) AS  s  ORDER BY s.mid asc", sendUid, fromUid, fromUid, sendUid);
        return  select;
    }
}
