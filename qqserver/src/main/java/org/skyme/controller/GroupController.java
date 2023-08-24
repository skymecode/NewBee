package org.skyme.controller;

import org.skyme.core.Request;
import org.skyme.core.Response;
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

        return groupService.queryGroup(request,response);
    }

    public BaseResponse messageHistory(Request request, Response response){

        return groupService.messageHistory(request,response);
    }
    public BaseResponse send(Request request, Response response){

        return groupService.send(request,response);
    }
    public BaseResponse addGroup(Request request, Response response){

        return groupService.addGroup(request,response);
    }
    public BaseResponse quitGroup(Request request, Response response){

        return groupService.quitGroup(request,response);
    }


}
