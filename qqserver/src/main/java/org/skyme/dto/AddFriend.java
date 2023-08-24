package org.skyme.dto;


import org.skyme.entity.User;

import java.io.Serializable;

/**
 * @author:Skyme
 * @create: 2023-08-20 16:09
 * @Description:
 */
public class AddFriend implements Serializable {
    private User fromUser;

    private User sendUser;

    public User getFromUser() {
        return fromUser;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    public User getSendUser() {
        return sendUser;
    }

    public void setSendUser(User sendUser) {
        this.sendUser = sendUser;
    }
}

