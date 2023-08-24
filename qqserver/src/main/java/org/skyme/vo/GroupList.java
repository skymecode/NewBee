package org.skyme.vo;

import org.skyme.entity.QQGroup;

import java.io.Serializable;

/**
 * @author:Skyme
 * @create: 2023-08-21 17:21
 * @Description:
 */
public class GroupList implements Serializable {

    private QQGroup group;

    private Long nums;

    public GroupList() {
    }

    public GroupList(QQGroup group, Long nums) {
        this.group = group;
        this.nums = nums;
    }

    public QQGroup getGroup() {
        return group;
    }

    public void setGroup(QQGroup group) {
        this.group = group;
    }

    public Long getNums() {
        return nums;
    }

    public void setNums(Long nums) {
        this.nums = nums;
    }
}
