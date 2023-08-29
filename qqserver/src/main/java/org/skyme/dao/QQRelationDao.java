package org.skyme.dao;

import org.skyme.entity.QQRelation;

import java.util.List;

public interface QQRelationDao {
    List<QQRelation> queryRelation(Long uid, Long fid, Long fid1, Long uid1);

    int insert(QQRelation qqRelation);

    int update(QQRelation relation);
}
