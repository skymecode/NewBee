package org.skyme.entity;

import org.skyme.annotation.Column;
import org.skyme.annotation.Id;
import org.skyme.annotation.Table;

import java.io.Serializable;

/**
 * (QqGroupMessageStatus)实体类
 *
 * @author makejava
 * @since 2023-08-21 17:18:03
 */
@Table("qq_group_message_status")
public class QQGroupMessageStatus implements Serializable {

    @Id("g_m_sid")
    private Long gMSid;
    @Column("g_m_mid")
    private Long gMMid;
    @Column("g_m_uid")
    private Long gMUid;
    @Column("g_m_gid")
    private Long gMGid;
    @Column
    private Integer status;

    public Long getgMSid() {
        return gMSid;
    }

    public void setgMSid(Long gMSid) {
        this.gMSid = gMSid;
    }

    public Long getgMMid() {
        return gMMid;
    }

    public void setgMMid(Long gMMid) {
        this.gMMid = gMMid;
    }

    public Long getgMUid() {
        return gMUid;
    }

    public void setgMUid(Long gMUid) {
        this.gMUid = gMUid;
    }

    public Long getgMGid() {
        return gMGid;
    }

    public void setgMGid(Long gMGid) {
        this.gMGid = gMGid;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}

