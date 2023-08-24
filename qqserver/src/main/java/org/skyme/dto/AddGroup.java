package org.skyme.dto;

import java.io.Serializable;

/**
 * @author:Skyme
 * @create: 2023-08-22 15:18
 * @Description:
 */
public class AddGroup implements Serializable {
    private String groupName;

    private Long uid;

    public AddGroup() {
    }

    public AddGroup(String groupName, Long uid) {
        this.groupName = groupName;
        this.uid = uid;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }
}
