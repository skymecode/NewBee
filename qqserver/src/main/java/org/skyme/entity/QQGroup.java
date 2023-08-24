package org.skyme.entity;

import org.skyme.annotation.Column;
import org.skyme.annotation.Id;
import org.skyme.annotation.Table;

import java.io.Serializable;

/**
 * (QqGroup)实体类
 *
 * @author makejava
 * @since 2023-08-21 17:17:48
 */
@Table("qq_group")
public class QQGroup implements Serializable {

    @Id("gid")
    private Long gid;
    @Column("g_name")
    private String gName;
    @Column("g_owner")
    private Long gOwner;
    @Column
    private Integer status;


    public Long getGid() {
        return gid;
    }

    public void setGid(Long gid) {
        this.gid = gid;
    }

    public String getGName() {
        return gName;
    }

    public void setGName(String gName) {
        this.gName = gName;
    }

    public Long getGOwner() {
        return gOwner;
    }

    public void setGOwner(Long gOwner) {
        this.gOwner = gOwner;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}

