package com.example.du_an_boxchat;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;

import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

import java.util.Collections;

public class Profile extends AppCompatActivity {

    TextView caller;
    EditText targetuser;
    ZegoSendCallInvitationButton callbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        caller = findViewById(R.id.textView);
        targetuser = findViewById(R.id.editId);
        callbtn = findViewById(R.id.callBtn);

        String callerId = getIntent().getStringExtra("callerId");
        caller.setText("You Name: " + callerId);


        targetuser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                startvideocall(targetuser.getText().toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void startvideocall(String targetuserid) {
        callbtn.setIsVideoCall(true);
        callbtn.setResourceID("zego_uikit_call"); // Please fill in the resource ID name that has been configured in the ZEGOCLOUD's console here.

        String targetUserName = targetuser.getText().toString().trim();

        ZegoUIKitUser targetUser = new ZegoUIKitUser(targetuserid, targetUserName);
        callbtn.setInvitees(Collections.singletonList(targetUser));
    }
}