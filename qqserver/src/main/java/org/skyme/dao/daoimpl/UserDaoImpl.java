package org.skyme.dao.daoimpl;

import org.skyme.dao.UserDao;
import org.skyme.dao.jdbc.SqlUtil;
import org.skyme.entity.QQRelation;
import org.skyme.entity.User;

import java.util.List;
import java.util.Map;

/**
 * @author:Skyme
 * @create: 2023-08-29 15:03
 * @Description:
 */
public class UserDaoImpl implements UserDao {
    @Override
    public int insert(User user) {
        int insert = SqlUtil.insert(user);
        return insert;
    }

    @Override
    public int update(User user) {
        int update = SqlUtil.update(user);
        return update;
    }

    @Override
    public User queryUserByUsername(String username) {
        User user = SqlUtil.selectOne(User.class, "select * from qq_user where username=?", username);
        return user;
    }

    @Override
    public User queryUserById(Long uid) {
        User user = SqlUtil.selectOne(User.class, "select * from qq_user where uid=?", uid);
        return user;
    }

    @Override
    public List<User> queryFriends(Long uid) {

        List<User> friends= SqlUtil.select(User.class, "SELECT u.* FROM `qq_user` u INNER JOIN `qq_relation` r ON u.uid = r.fid WHERE r.uid =? AND r.`status` = 1;", uid);
        return friends;
    }

    @Override
    public List<Map<String, Object>> queryCounts(Long fid, Long uid) {
        List<Map<String, Object>> select1 = SqlUtil.select("select count(*) from qq_message WHERE from_uid=? AND send_uid=? AND `status`=0 ", fid,uid );
        return  select1;

    }

    @Override
    public List<QQRelation> queryRelation(Long uid, Long fid, Long fid1, Long uid1) {
        List<QQRelation> select = SqlUtil.select(QQRelation.class, "SELECT * FROM qq_relation WHERE (uid=? and fid=?) or (uid=? AND fid=?)", uid, fid, fid1, uid1);
        return select;
    }

    @Override
    public List<User> queryLikeNickName(String nickName) {
        List<User> select = SqlUtil.select(User.class, "select * from qq_user where nickname like ?", nickName);
        return select;
    }
}
