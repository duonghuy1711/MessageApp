package com.example.du_an_boxchat;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.permissionx.guolindev.PermissionX;
import com.zegocloud.uikit.prebuilt.call.config.ZegoNotificationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;
import android.Manifest;


public class calling_class extends AppCompatActivity {

    EditText editText;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling_class);

        editText = findViewById(R.id.TextID);
        btn = findViewById(R.id.callLogin);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Yêu cầu quyền SYSTEM_ALERT_WINDOW trước khi chuyển sang màn hình Profile
                PermissionX.init(calling_class.this)
                        .permissions(Manifest.permission.SYSTEM_ALERT_WINDOW)
                        .onExplainRequestReason((scope, deniedList) -> {
                            String message = "We need your consent for the following permissions in order to use the offline call function properly";
                            scope.showRequestReasonDialog(deniedList, message, "Allow", "Deny");
                        })
                        .request((allGranted, grantedList, deniedList) -> {
                            if (allGranted) {
                                startProfileActivity(editText.getText().toString().trim());
                            } else {
                                Toast.makeText(calling_class.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void startProfileActivity(String userId) {
        // Khởi tạo dịch vụ cuộc gọi trước khi chuyển sang màn hình Profile
        startmyservice(userId);

        Intent intent = new Intent(calling_class.this, Profile.class);
        intent.putExtra("callerId", userId); // Truyền ID của người gọi sang màn hình Profile
        startActivity(intent);
    }

    private void startmyservice(String userId) {
        Application application = getApplication(); // Android's application context
        long appID = 981743212;   // yourAppID
        String appSign = "43141b8df08018d4b2f5691df7ba712246881e40504304fd8b0d12b3de92b8d6";  // yourAppSign

        ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig = new ZegoUIKitPrebuiltCallInvitationConfig();

        ZegoUIKitPrebuiltCallInvitationService.init(application, appID, appSign, userId, userId, callInvitationConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ZegoUIKitPrebuiltCallInvitationService.unInit();
    }
}