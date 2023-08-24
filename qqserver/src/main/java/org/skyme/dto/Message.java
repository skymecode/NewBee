package org.skyme.dto;

import java.io.Serializable;

/**
 * @author:Skyme
 * @create: 2023-08-17 10:39
 * @Description:
 */
public class Message<T> implements Serializable {
    private MessageType type;//消息类型


    private int code;//消息状态码


    private String mes;//消息响应内容


    private T date;//消息内容

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public T getDate() {
        return date;
    }

    public void setDate(T date) {
        this.date = date;
    }
}
