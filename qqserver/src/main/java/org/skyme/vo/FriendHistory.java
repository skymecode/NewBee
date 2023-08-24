package org.skyme.vo;

import org.skyme.entity.QQMessage;

import java.io.Serializable;
import java.util.List;

/**
 * @author:Skyme
 * @create: 2023-08-18 15:48
 * @Description:
 */
public class FriendHistory implements Serializable {
    private List<QQMessage> list;

    private Long sendId;//接收者

    private Long fromId;//发送者

    public List<QQMessage> getList() {
        return list;
    }

    public void setList(List<QQMessage> list) {
        this.list = list;
    }

    public Long getSendId() {
        return sendId;
    }

    public void setSendId(Long sendId) {
        this.sendId = sendId;
    }

    public Long getFromId() {
        return fromId;
    }

    public void setFromId(Long fromId) {
        this.fromId = fromId;
    }
}
