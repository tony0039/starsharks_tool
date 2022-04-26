package com.g.bean;

import com.g.model.UserEntity;

import java.util.Date;

public class UserBean {
    private String id;
    private String userName;
    private Boolean isUnlock=false;
    private Date addTime;
    public UserBean(UserEntity user){
        this.id=user.getId();
        this.userName=user.getUserName();
        this.addTime=user.getAddTime();
    }

    public Boolean getUnlock() {
        return isUnlock;
    }

    public void setUnlock(Boolean unlock) {
        isUnlock = unlock;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
