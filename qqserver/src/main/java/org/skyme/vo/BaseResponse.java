package org.skyme.vo;

import org.skyme.core.Message;

import java.io.Serializable;

/**
 * @author:Skyme
 * @create: 2023-08-17 16:30
 * @Description:
 */
public class BaseResponse implements Serializable {
    private Message message;

    public BaseResponse(Message message) {
        this.message = message;
    }

    public BaseResponse() {
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
