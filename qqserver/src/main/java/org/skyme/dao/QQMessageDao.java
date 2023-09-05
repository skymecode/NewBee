package org.skyme.dao;

import org.skyme.entity.QQMessage;

import java.util.List;

public interface QQMessageDao {
    List<QQMessage> select(Long sendUid, Long fromUid, Long fromUid1, Long sendUid1);

    int update(QQMessage qqMessage);

    List<QQMessage> selectAll(Long sendUid, Long fromUid, Long fromUid1, Long sendUid1);
}
