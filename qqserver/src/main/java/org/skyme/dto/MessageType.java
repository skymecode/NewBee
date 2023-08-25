package org.skyme.dto;

import java.io.Serializable;

public enum MessageType implements Serializable {
    REG("/user/register"),
    REG_RESULT("1"),

    LOGIN("/user/login"),
    LOG_RESULT("2"),
    FRIENDS_LIST("/user/queryFriends"),
    FRIENDSLIST_RESULT("3"),

    SEND("/chat/sendToFriend"),
    SEND_RESULT("4"),
    RECEIVE_RESULT("re"),



    MES_HISTORY("/chat/friendHistory"),
    MES_HISTORY_RESULT("5"),

    LOGOUT("/user/logout"),
    LOGOUT_RESULT("6"),

    DELETE_FRIEND("/user/deleteFriend"),

    DELETE_FRIEND_RESULT("7"),

    DELETEED_RESULT("8"),

    QUERY_LIKE_USER("/user/queryLikeUser"),

    QUERY_LIKE_USER_RESULT("10"),

    ADD_FRIEND("/user/addFriend"),

    ADD_FRIEND_RESULT("9"),

    ADDED_FRIEND("11"),
    ACCEPT_FRIEND("/user/accept"),
    REFUSE_FRIEND("/user/refuse"),

    SUCCESS_ADD_FRIEND_RESULT("12"),//成功加好友

    REFUSED_ADD_FRIEND_RESULT("13"),//被拒绝加好友

    //查找群
    GROUP_LIST("/group/queryGroup"),

    GROUP_LIST_RESULT("group_result"),

    //查询群消息记录
    GROUP_MESSAGE_HISTORY("/group/messageHistory"),

    GROUP_MESSAGE_HISTORY_RESULT("group_message_result"),

    //发送消息
    GROUP_SEND_MESSAGE("/group/send"),

    GROUP_SEND_RESULT("group_send_result"),
    //加群
    APPLY_JOIN_GROUP("/group/addGroup"),
    //创建群聊
    CREATE_GROUP("/group/addGroup"),

    CREATE_OR_JOIN_GROUP_RESULT("create_or_join_group_result"),
    //接受到加群通知
    //同意进群
    //被允许进群通知

    INFO("/user/info"),
    INFO_RESULT("info"),

    NORMAL_RESULT(""), QUIT_GROUP("/group/quitGroup"),QUIT_GROUP_RESULT("quit_group_result");


    private String path;

    MessageType(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "MessageType{" +
                "path='" + path + '\'' +
                '}';
    }
}
