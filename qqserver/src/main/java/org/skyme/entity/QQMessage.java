package org.skyme.entity;

import org.skyme.annotation.Column;
import org.skyme.annotation.Id;
import org.skyme.annotation.Table;

import java.util.Date;
import java.io.Serializable;

/**
 * (QqMessage)实体类
 *
 * @author makejava
 * @since 2023-08-18 10:04:01
 */
@Table("qq_message")
public class QQMessage implements Serializable {

    @Id
    private Long mid;
    @Column
    private String content;
    @Column("from_uid")
    private Long fromUid;
    @Column("send_uid")
    private Long sendUid;
    @Column
    private Integer status;
    @Column("send_time")
    private String sendtime;


    public Long getMid() {
        return mid;
    }

    public void setMid(Long mid) {
        this.mid = mid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getFromUid() {
        return fromUid;
    }

    public void setFromUid(Long fromUid) {
        this.fromUid = fromUid;
    }

    public Long getSendUid() {
        return sendUid;
    }

    public void setSendUid(Long sendUid) {
        this.sendUid = sendUid;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getSendtime() {
        return sendtime;
    }

    public void setSendtime(String sendtime) {
        this.sendtime = sendtime;
    }
}

