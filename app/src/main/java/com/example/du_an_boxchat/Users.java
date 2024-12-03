package com.example.du_an_boxchat;

public class Users {
    String profilepic, mail, userName, password, userId, stattus;
    private String fcmToken;

    public  Users(){}

    public Users(String userId, String userName, String mail, String password, String profilepic, String status){
        this.userId = userId;
        this.userName = userName;
        this.mail = mail;
        this.password = password;
        this.profilepic = profilepic;
        this.stattus = status;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStattus() {
        return stattus;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public void setStattus(String stattus) {
        this.stattus = stattus;
    }
}
