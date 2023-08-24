package org.skyme.vo;

import org.skyme.entity.User;

import java.io.Serializable;

/**
 * @author:Skyme
 * @create: 2023-08-21 15:04
 * @Description:
 */
public class FriendList implements Serializable {

    private User user;

    private Long nums;

    public FriendList() {
    }

    public FriendList(User user, Long nums) {
        this.user = user;
        this.nums = nums;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getNums() {
        return nums;
    }

    public void setNums(Long nums) {
        this.nums = nums;
    }
}
