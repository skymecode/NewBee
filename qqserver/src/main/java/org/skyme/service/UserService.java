package org.skyme.service;

import org.skyme.core.Request;
import org.skyme.core.Response;
import org.skyme.dto.AddFriend;
import org.skyme.entity.QQRelation;
import org.skyme.vo.BaseResponse;
import org.skyme.entity.User;

import java.io.IOException;

public interface UserService {
    public BaseResponse register(User data,Response response);

    BaseResponse login(User data, Response response);

    BaseResponse queryFriends(User data, Response response);

    BaseResponse logout(Long uid, Response response);

    BaseResponse deleteFriend(QQRelation qqRelation, Response response) throws IOException;

    BaseResponse queryLikeUsers(String nickName, Response response);

    BaseResponse addFriend(AddFriend add, Response response) throws IOException;

    BaseResponse acceptFriend(Request request, Response response) throws IOException;

    BaseResponse refuseFriend(Request request, Response response);

    BaseResponse queryInfo(User user, Response response);

    BaseResponse modNickname(User user, Response response);
}
