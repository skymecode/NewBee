package org.skyme.service;

import org.skyme.core.Request;
import org.skyme.core.Response;
import org.skyme.vo.BaseResponse;

public interface GroupService {
    BaseResponse queryGroup(Request request, Response response);

    BaseResponse messageHistory(Request request, Response response);

    BaseResponse send(Request request, Response response);
    BaseResponse addGroup(Request request, Response response);

    BaseResponse quitGroup(Request request, Response response);
}
