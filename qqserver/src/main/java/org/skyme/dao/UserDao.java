package org.skyme.dao;
import org.skyme.entity.QQRelation;
import org.skyme.entity.User;

import java.util.List;
import java.util.Map;

public interface UserDao {
    int insert(User user);

    int update(User user);
    //查询用户
    User queryUserByUsername(String username);

    User queryUserById(Long uid);
    //查询好友
    List<User> queryFriends(Long uid);
    //
    List<Map<String, Object>> queryCounts(Long fid,Long uid);


    List<QQRelation> queryRelation(Long uid, Long fid, Long fid1, Long uid1);


    List<User> queryLikeNickName(String nickName);
}
