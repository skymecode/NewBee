package org.skyme.service;

import org.skyme.core.Request;
import org.skyme.core.Response;
import org.skyme.dto.GroupUser;
import org.skyme.dto.RemoveGroup;
import org.skyme.entity.QQGroupMessage;
import org.skyme.vo.BaseResponse;
import org.skyme.entity.User;

public interface GroupService {
    BaseResponse queryGroup(User user, Response response);

    BaseResponse messageHistory(GroupUser user, Response response);

    BaseResponse send(QQGroupMessage qqGroupMessage, Response response);
    BaseResponse addGroup(Request request, Response response);

    BaseResponse quitGroup(RemoveGroup removeGroup, Response response);

    BaseResponse queryMembers(Long gid, Response response);
}
