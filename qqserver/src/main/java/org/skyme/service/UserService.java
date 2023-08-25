package org.skyme.service;

import org.skyme.core.Request;
import org.skyme.core.Response;
import org.skyme.vo.BaseResponse;

import java.io.IOException;

public interface UserService {
    public BaseResponse register(Request request,Response response);

    BaseResponse login(Request request, Response response);

    BaseResponse queryFriends(Request request, Response response);

    BaseResponse logout(Request request, Response response);

    BaseResponse deleteFriend(Request request, Response response) throws IOException;

    BaseResponse queryLikeUsers(Request request, Response response);

    BaseResponse addFriend(Request request, Response response) throws IOException;

    BaseResponse acceptFriend(Request request, Response response) throws IOException;

    BaseResponse refuseFriend(Request request, Response response);

    BaseResponse queryInfo(Request request, Response response);
}
