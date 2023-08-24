package org.skyme.dto;

import java.io.Serializable;

/**
 * @author:Skyme
 * @create: 2023-08-22 16:48
 * @Description:
 */
public class RemoveGroup implements Serializable {
    private Long gid;
    private Long uid;

    public RemoveGroup() {
    }

    public RemoveGroup(Long gid, Long uid) {
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
