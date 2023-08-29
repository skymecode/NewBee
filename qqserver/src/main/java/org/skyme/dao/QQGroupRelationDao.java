package org.skyme.dao;

import org.skyme.entity.QQGroupRelation;

public interface QQGroupRelationDao {
    int insert(QQGroupRelation qqGroupRelation);

    QQGroupRelation selectOne(Long gid, Long uid);

    int update(QQGroupRelation qqGroupRelation);
}
