package org.skyme.core;

import org.skyme.dto.Message;

/**
 * @author:Skyme
 * @create: 2023-08-17 16:13
 * @Description: 请求
 */
public class Request {
    Message message;

    public Request(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
