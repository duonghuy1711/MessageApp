package com.example.du_an_boxchat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationService extends FirebaseMessagingService {
    private static final String TAG = "NotificationService";
    private static final String CHANNEL_ID = "boxchat_channel";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = getString(R.string.channel_id);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // Xử lý tin nhắn ở đây
        String message = remoteMessage.getData().get("message");
        String type = remoteMessage.getData().get("type");
        String chatId = remoteMessage.getData().get("chatId");
        String senderImage = remoteMessage.getData().get("senderImage");

        // Gọi phương thức showNotification() để hiển thị thông báo
        showNotification(message, type, senderImage, chatId);
    }

    private void showNotification(String message, String type, String senderImage, String chatId) {
        // Tạo intent để mở chatWin khi click vào thông báo
        Intent intent = new Intent(this, chatWin.class);
        intent.putExtra("chatId", chatId);

        // Tạo pending intent
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Tạo notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("BoxChat")
                .setContentText(message)
                .setSmallIcon(R.drawable.bell)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Hiển thị thông báo
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }
}
