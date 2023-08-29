package org.skyme.controller;

import org.skyme.core.Message;
import org.skyme.core.Request;
import org.skyme.core.Response;
import org.skyme.dto.AddFriend;
import org.skyme.entity.QQRelation;
import org.skyme.entity.User;
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
        Message message = request.getMessage();
        User date = (User) message.getData();
       return userService.register(date,response);
    }

    public BaseResponse login(Request request, Response response){
        Message message = request.getMessage();
        User data = (User) message.getData();
        return userService.login(data,response);
    }

    public BaseResponse queryFriends(Request request, Response response){
        Message message = request.getMessage();
        User data = (User) message.getData();
        return userService.queryFriends(data,response);
    }
    public BaseResponse logout(Request request, Response response){
        Message message = request.getMessage();
        Long uid = (Long) message.getData();
        return userService.logout(uid,response);
    }


    public BaseResponse deleteFriend(Request request,Response response) throws IOException {
        Message message = request.getMessage();
        QQRelation qqRelation = (QQRelation) message.getData();
        return userService.deleteFriend(qqRelation, response);

    }
    public BaseResponse queryLikeUser(Request request,Response response) throws IOException {
        Message message = request.getMessage();
        String nickName = (String) message.getData();
        return userService.queryLikeUsers(nickName, response);

    }
    public BaseResponse addFriend(Request request,Response response) throws IOException {
        Message message = request.getMessage();
        AddFriend add = (AddFriend) message.getData();
        return userService.addFriend(add, response);

    }

    public BaseResponse accept(Request request,Response response) throws IOException {
        return userService.acceptFriend(request, response);
    }

    public BaseResponse refuse(Request request,Response response){
        return userService.refuseFriend(request, response);
    }
    public BaseResponse info(Request request,Response response){
        Message<User> message = request.getMessage();
        User user = message.getData();
        return userService.queryInfo(user, response);
    }
    public BaseResponse modNickname(Request request,Response response){
        System.out.println("执行到controller");
        Message<User> message = request.getMessage();
        User user = message.getData();
        return userService.modNickname(user, response);
    }

}
