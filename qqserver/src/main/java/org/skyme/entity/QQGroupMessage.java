package org.skyme.entity;

import org.skyme.annotation.Column;
import org.skyme.annotation.Id;
import org.skyme.annotation.ManyToOne;
import org.skyme.annotation.Table;

import java.util.Date;
import java.io.Serializable;

/**
 * (QqGroupMessage)实体类
 *
 * @author makejava
 * @since 2023-08-21 17:17:55
 */
@Table("qq_group_message")
public class QQGroupMessage implements Serializable {

    @Id("g_mid")
    private Long gMid;
    @Column("g_content")
    private String gContent;
    @ManyToOne("g_from_uid")
    private User user;

    @Column("g_gid")
    private Long gid;
    @Column("g_create_time")
    private String gCreateTime;


    public Long getgMid() {
        return gMid;
    }

    public void setgMid(Long gMid) {
        this.gMid = gMid;
    }

    public String getgContent() {
        return gContent;
    }

    public void setgContent(String gContent) {
        this.gContent = gContent;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getGid() {
        return gid;
    }

    public void setGid(Long gid) {
        this.gid = gid;
    }

    public String getgCreateTime() {
        return gCreateTime;
    }

    public void setgCreateTime(String gCreateTime) {
        this.gCreateTime = gCreateTime;
    }
}

