package org.skyme.controller;

import org.skyme.core.Request;
import org.skyme.core.Response;
import org.skyme.service.UserService;
import org.skyme.service.serviceimpl.UserServiceImpl;
import org.skyme.vo.BaseResponse;

import java.io.IOException;

/**
 * @author:Skyme
 * @create: 2023-08-17 10:42
 * @Description:
 */
public class UserController {

private UserService userService=new UserServiceImpl();
    public BaseResponse register(Request request, Response response){
       return userService.register(request,response);
    }

    public BaseResponse login(Request request, Response response){
        return userService.login(request,response);
    }

    public BaseResponse queryFriends(Request request, Response response){
        return userService.queryFriends(request,response);
    }
    public BaseResponse logout(Request request, Response response){
        return userService.logout(request,response);
    }


    public BaseResponse deleteFriend(Request request,Response response) throws IOException {
        return userService.deleteFriend(request, response);

    }
    public BaseResponse queryLikeUser(Request request,Response response) throws IOException {
        return userService.queryLikeUsers(request, response);

    }
    public BaseResponse addFriend(Request request,Response response) throws IOException {
        return userService.addFriend(request, response);

    }

    public BaseResponse accept(Request request,Response response) throws IOException {
        return userService.acceptFriend(request, response);
    }

    public BaseResponse refuse(Request request,Response response){
        return userService.refuseFriend(request, response);
    }
    public BaseResponse info(Request request,Response response){
        return userService.queryInfo(request, response);
    }

}
