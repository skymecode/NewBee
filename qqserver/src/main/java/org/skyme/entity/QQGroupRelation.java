package org.skyme.entity;

import org.skyme.annotation.Column;
import org.skyme.annotation.Id;
import org.skyme.annotation.Table;

import java.io.Serializable;

/**
 * (QqGroupRelation)实体类
 *
 * @author makejava
 * @since 2023-08-21 17:18:10
 */
@Table("qq_group_relation")
public class QQGroupRelation implements Serializable {

    @Id("g_rid")
    private Long gRid;
    @Column("g_gid")
    private Long gGid;
    @Column("g_uid")
    private Long gUid;
    @Column
    private Integer status;


    public Long getGRid() {
        return gRid;
    }

    public void setGRid(Long gRid) {
        this.gRid = gRid;
    }

    public Long getGGid() {
        return gGid;
    }

    public void setGGid(Long gGid) {
        this.gGid = gGid;
    }

    public Long getGUid() {
        return gUid;
    }

    public void setGUid(Long gUid) {
        this.gUid = gUid;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}

