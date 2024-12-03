package com.example.du_an_boxchat;

import android.graphics.Bitmap;
import android.net.Uri;

public class Message {
    private String message;
    private String attachmentPath;
    public String imageBase64;

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public Message() {
    }

    public Message(String message) {
        this.message = message;
    }

    public Message(String message, String attachmentPath) {
        this.message = message;
        this.attachmentPath = attachmentPath;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }
}
