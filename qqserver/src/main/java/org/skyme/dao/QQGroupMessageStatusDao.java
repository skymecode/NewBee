package org.skyme.dao;

import org.skyme.entity.QQGroupMessageStatus;

import java.util.List;

public interface QQGroupMessageStatusDao {
    List<QQGroupMessageStatus> selectByStatus(Long uid, Long gid);
}
