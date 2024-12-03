package com.example.du_an_boxchat;

import java.util.ArrayList;

public class msgModelclass {
    String message;
    String senderid;
    long timeStamp;
    int type;
    String attachmentPath; // Đường dẫn của tệp tin hoặc ảnh đính kèm
    String fileUri;
    String downloadUrl;
    String imageUrl;
    private boolean sentMessage; // Loại tin nhắn: gửi hoặc nhận

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    private String imageBase64;
    private ArrayList<Users> usersArrayList;

    public msgModelclass() {
    }


    public msgModelclass(String message, String senderid, long timeStamp, String attachmentPath) {
        this.message = message;
        this.senderid = senderid;
        this.timeStamp = timeStamp;
        this.attachmentPath = attachmentPath;
    }

    public msgModelclass(String message, String senderid, long timeStamp, String attachmentPath, String fileUri) {
        this.message = message;
        this.senderid = senderid;
        this.timeStamp = timeStamp;
        this.attachmentPath = attachmentPath;
        this.fileUri = fileUri;
    }


    public ArrayList<Users> getUsersArrayList() {
        return usersArrayList;
    }

    public void setUsersArrayList(ArrayList<Users> usersArrayList) {
        this.usersArrayList = usersArrayList;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }
    public String getFileUri() {
        return fileUri;
    }
    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public boolean isSentMessage() {
        return sentMessage;
    }

    public void setSentMessage(boolean sentMessage) {
        this.sentMessage = sentMessage;
    }


}
