package org.skyme.dto;

import java.io.Serializable;

/**
 * @author:Skyme
 * @create: 2023-08-21 21:56
 * @Description:群和成员之间的DTO
 */

public class GroupUser implements Serializable {
    private Long gid;
    private Long uid;

    public GroupUser() {
    }

    public GroupUser(Long gid, Long uid) {
        this.gid = gid;
        this.uid = uid;
    }

    public Long getGid() {
        return gid;
    }

    public void setGid(Long gid) {
        this.gid = gid;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }
}
