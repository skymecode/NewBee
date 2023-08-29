package org.skyme.dao;

import org.skyme.entity.QQGroup;
import org.skyme.entity.User;

import java.util.List;
import java.util.Map;

public interface QQGroupDao {
    List<QQGroup> selectByUid(Long uid);

    List<Map<String, Object>> selectCount(Long gid, Long uid);

    List<User> selectAllUsers(Long gid);

    List<QQGroup> selectByName(String groupName);

    List<User> selectMembers(Long gid);
}
