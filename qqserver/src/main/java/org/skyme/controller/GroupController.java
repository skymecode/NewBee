package org.skyme.controller;

import org.skyme.core.Message;
import org.skyme.core.Request;
import org.skyme.core.Response;
import org.skyme.dto.GroupUser;
import org.skyme.dto.RemoveGroup;
import org.skyme.entity.QQGroupMessage;
import org.skyme.entity.User;
import org.skyme.service.GroupService;
import org.skyme.service.serviceimpl.GroupServiceImpl;
import org.skyme.vo.BaseResponse;

/**
 * @author:Skyme
 * @create: 2023-08-21 00:06
 * @Description:
 */
public class GroupController {
    private GroupService groupService=new GroupServiceImpl();
    public BaseResponse queryGroup(Request request, Response response){
        Message message = request.getMessage();
        User user= (User) message.getData();
        return groupService.queryGroup(user,response);
    }

    public BaseResponse messageHistory(Request request, Response response){
        Message message = request.getMessage();
        GroupUser user = (GroupUser) message.getData();

        return groupService.messageHistory(user,response);
    }
    public BaseResponse send(Request request, Response response){
        Message message = request.getMessage();
        QQGroupMessage groupMessage = (QQGroupMessage) message.getData();
        return groupService.send(groupMessage,response);
    }
    public BaseResponse addGroup(Request request, Response response){

        return groupService.addGroup(request,response);
    }
    public BaseResponse quitGroup(Request request, Response response){
        Message message = request.getMessage();
        RemoveGroup removeGroup = (RemoveGroup) message.getData();

        return groupService.quitGroup(removeGroup,response);
    }
    public BaseResponse queryMembers(Request request, Response response){
        Message message = request.getMessage();
        Long  gid = (Long) message.getData();

        return groupService.queryMembers(gid,response);
    }


}
