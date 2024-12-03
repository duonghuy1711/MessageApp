package com.example.du_an_boxchat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class AndoirdUtil {

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void passUserModelAsIntent(Intent intent, Users model) {
        intent.putExtra("username", model.getUserName());
        intent.putExtra("phone", model.getMail());
        intent.putExtra("userId", model.getUserId());
        intent.putExtra("fcmToken", model.getFcmToken());
    }

    public static Users getUserModelFromIntent(Intent intent) {
        Users userModel = new Users();
        userModel.setUserName(intent.getStringExtra("username"));
        userModel.setMail(intent.getStringExtra("phone"));
        userModel.setUserId(intent.getStringExtra("userId"));
        userModel.setFcmToken(intent.getStringExtra("fcmToken"));
        return userModel;
    }

    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView) {
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }
}
