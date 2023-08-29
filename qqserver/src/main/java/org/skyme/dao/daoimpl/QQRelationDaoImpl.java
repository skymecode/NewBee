package org.skyme.dao.daoimpl;

import org.skyme.dao.QQRelationDao;
import org.skyme.dao.jdbc.SqlUtil;
import org.skyme.entity.QQRelation;

import java.util.List;

/**
 * @author:Skyme
 * @create: 2023-08-29 15:51
 * @Description:
 */
public class QQRelationDaoImpl implements QQRelationDao {
    @Override
    public List<QQRelation> queryRelation(Long uid, Long fid, Long fid1, Long uid1) {
        List<QQRelation> select = SqlUtil.select(QQRelation.class, "SELECT * FROM qq_relation WHERE (uid=? and fid=?) or (uid=? AND fid=?)", uid, fid, fid1, uid1);
        return select;

    }

    @Override
    public int insert(QQRelation qqRelation) {
        int insert = SqlUtil.insert(qqRelation);
        return insert;
    }

    @Override
    public int update(QQRelation relation) {
        int update = SqlUtil.update(relation);
        return update;
    }
}
