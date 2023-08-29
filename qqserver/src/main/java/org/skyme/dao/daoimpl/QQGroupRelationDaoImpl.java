package org.skyme.dao.daoimpl;

import org.skyme.dao.QQGroupRelationDao;
import org.skyme.dao.jdbc.SqlUtil;
import org.skyme.entity.QQGroupRelation;

/**
 * @author:Skyme
 * @create: 2023-08-29 16:56
 * @Description:
 */
public class QQGroupRelationDaoImpl implements QQGroupRelationDao {
    @Override
    public int insert(QQGroupRelation qqGroupRelation) {
        int insert1 = SqlUtil.insert(qqGroupRelation);
        return  insert1;
    }

    @Override
    public QQGroupRelation selectOne(Long gid, Long uid) {
        QQGroupRelation qqGroupRelation1 = SqlUtil.selectOne(QQGroupRelation.class, "select * from qq_group_relation where g_gid=? and g_uid=?", gid, uid);
        return qqGroupRelation1;
    }

    @Override
    public int update(QQGroupRelation qqGroupRelation) {
        int update = SqlUtil.update(qqGroupRelation);
        return update;
    }
}
