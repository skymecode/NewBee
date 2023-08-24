package org.skyme.entity;

import org.skyme.annotation.Column;
import org.skyme.annotation.Id;
import org.skyme.annotation.Table;

import java.util.Date;
import java.io.Serializable;

/**
 * (QqUser)实体类
 *
 * @author makejava
 * @since 2023-08-17 14:34:09
 */
@Table("qq_user")
public class User implements Serializable {
    private static final long serialVersionUID = -35737123535164608L;


    @Id
    private Long uid;
    @Column
    private String username;
    @Column
    private String password;
    @Column
    private String email;
    @Column
    private String nickname;
    @Column("create_time")
    private String createTime;
    @Column("login_time")
    private String loginTime;
    @Column("online")
    private Integer online;



    public Integer getOnline() {
        return online;
    }

    public void setOnline(Integer online) {
        this.online = online;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }
}

