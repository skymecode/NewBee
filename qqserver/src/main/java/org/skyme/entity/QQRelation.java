package org.skyme.entity;

import org.skyme.annotation.Column;
import org.skyme.annotation.Id;
import org.skyme.annotation.Table;

import java.io.Serializable;

/**
 * (QqRelation)实体类
 *
 * @author makejava
 * @since 2023-08-19 15:52:26
 */

@Table("qq_relation")
public class QQRelation implements Serializable {

    @Id
    private Long rid;
    @Column
    private Long uid;
    @Column
    private Long fid;
    @Column
    private Integer status;


    public Long getRid() {
        return rid;
    }

    public void setRid(Long rid) {
        this.rid = rid;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Long getFid() {
        return fid;
    }

    public void setFid(Long fid) {
        this.fid = fid;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}

