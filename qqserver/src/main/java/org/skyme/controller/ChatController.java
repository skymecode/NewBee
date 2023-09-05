package org.skyme.controller;

import org.skyme.core.Message;
import org.skyme.core.Request;
import org.skyme.core.Response;
import org.skyme.entity.QQMessage;
import org.skyme.service.ChatService;
import org.skyme.service.serviceimpl.ChatServiceImpl;
import org.skyme.vo.BaseResponse;

/**
 * @author:Skyme
 * @create: 2023-08-18 09:24
 * @Description:
 */
public class ChatController {
    private ChatService chatService=new ChatServiceImpl();
    public BaseResponse sendToFriend(Request request, Response response){
        return chatService.sendToFriend(request,response);
    }
    public BaseResponse friendHistory(Request request, Response response){
        Message message = request.getMessage();
        QQMessage data = (QQMessage) message.getData();
        return chatService.friendHistory(data,response);
    }

    public BaseResponse historyFriend(Request request,Response response){
        Message message=request.getMessage();
        QQMessage data = (QQMessage) message.getData();
        return chatService.historyFriend(data,response);
    }


}
