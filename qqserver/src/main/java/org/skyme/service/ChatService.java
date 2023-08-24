package org.skyme.service;

import org.skyme.core.Request;
import org.skyme.core.Response;
import org.skyme.vo.BaseResponse;

public interface ChatService {
    BaseResponse sendToFriend(Request request, Response response);

    BaseResponse friendHistory(Request request, Response response);
}
