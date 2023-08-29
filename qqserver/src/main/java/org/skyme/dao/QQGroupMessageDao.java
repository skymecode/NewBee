package org.skyme.dao;

import org.skyme.entity.QQGroupMessage;
import org.skyme.entity.QQGroupMessageStatus;

import java.util.List;

public interface QQGroupMessageDao {
    List<QQGroupMessage> selectById(Long gid);

    int update(QQGroupMessageStatus qqGroupMessageStatus);
}
