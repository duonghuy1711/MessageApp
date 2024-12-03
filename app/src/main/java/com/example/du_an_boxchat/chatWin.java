package com.example.du_an_boxchat;

import static com.google.common.io.Files.getFileExtension;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import com.example.du_an_boxchat.Message;
import com.example.du_an_boxchat.msgModelclass;

import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import android.Manifest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import static com.google.common.io.Files.getFileExtension;
import com.example.du_an_boxchat.MainActivity;
import com.example.du_an_boxchat.Constant;
import com.bumptech.glide.Glide;


import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class chatWin extends AppCompatActivity {
    String reciverimg, reciverUid, reciverName,SenderUID;
    CircleImageView profile;
    TextView reciverNName;
    CardView sendbtn;
    EditText textmsg;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    public static String senderImg;
    public static String reciverIImg;
    String senderRoom, reciverRoom;
    RecyclerView mmessangesAdpter;
    ArrayList<msgModelclass> messagessArrayList;
    messagesAdpter messagesAdpter;
    private static final int REQUEST_FILE_PICKER = 102;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_STORAGE_PERMISSION = 101;
    String senderUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    private Uri selectedImageUri; // Thêm biến này để lưu trữ URI của hình ảnh đã chọn
    private ImageView capturedImageView;
    private String attachmentPath = "";
    private ValueEventListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_win);

//        requestStoragePermission();
        String chatroomId = getIntent().getStringExtra("chatroomId");


        capturedImageView = findViewById(R.id.capturedImageView);

        mmessangesAdpter = findViewById(R.id.msgadpter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mmessangesAdpter.setLayoutManager(linearLayoutManager);

        reciverName = getIntent().getStringExtra("nameeee");
        reciverimg = getIntent().getStringExtra("reciverImg");
        reciverUid = getIntent().getStringExtra("uid");

        messagessArrayList = new ArrayList<>();
        messagesAdpter = new messagesAdpter(chatWin.this, messagessArrayList,senderImg, reciverIImg);
        mmessangesAdpter.setAdapter(messagesAdpter);

        sendbtn = findViewById(R.id.sendbtnn);
        textmsg = findViewById(R.id.textmsg);

        profile = findViewById(R.id.profileimgg);
        reciverNName = findViewById(R.id.recivername);

        // Kiểm tra nếu đường dẫn hình ảnh rỗng hoặc null, gán một hình ảnh mặc định
        if (reciverimg != null && !reciverimg.isEmpty()) {
            Picasso.get().load(reciverimg).into(profile);
        } else {
            // Xử lý khi reciverImg rỗng hoặc null, ví dụ: Gán một hình ảnh mặc định
        }

        reciverNName.setText(reciverName);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        SenderUID = firebaseAuth.getUid();

        senderRoom = SenderUID + reciverUid;
        reciverRoom = reciverUid + SenderUID;

        DatabaseReference reference = database.getReference().child("user").child(SenderUID);
        DatabaseReference chatreference = database.getReference().child("chats").child(senderRoom).child("messages");

        chatreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagessArrayList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    msgModelclass messages = dataSnapshot.getValue(msgModelclass.class);
                    messagessArrayList.add(messages);
                }
                messagesAdpter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("profilepic").exists()) { // Kiểm tra xem có tồn tại "profilepic" trong snapshot không
                    senderImg = snapshot.child("profilepic").getValue().toString();
                } else {
                    senderImg = ""; // Hoặc gán giá trị mặc định khác tùy thuộc vào yêu cầu của bạn
                }
                reciverIImg = reciverimg;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = textmsg.getText().toString();
                if(message.isEmpty() && selectedImageUri == null) {
                    Toast.makeText(chatWin.this, "Enter The Message First", Toast.LENGTH_SHORT).show();
                } else {
                    textmsg.setText("");
                    Date date = new Date();
                    if(selectedImageUri != null) {
                        sendImageMessage(selectedImageUri, true); // Gửi từ người gửi
                    }else{
                        msgModelclass messagess = new msgModelclass(message, SenderUID, date.getTime(), attachmentPath);
                        DatabaseReference senderRef = database.getReference().child("chats").child(senderRoom).child("messages").push();
                        senderRef.setValue(messagess).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    DatabaseReference receiverRef = database.getReference().child("chats").child(reciverRoom).child("messages").push();
                                    receiverRef.setValue(messagess).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(!task.isSuccessful()) {

                                                Toast.makeText(chatWin.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(chatWin.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });
        ImageView fileIcon = findViewById(R.id.fileIcon);
        fileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
            }
        });

        ImageView cameraIcon = findViewById(R.id.cameraIcon);
        cameraIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        // Đặt người nghe cho adapter để mở tệp tin khi người dùng nhấn vào đường dẫn
        messagesAdpter.setOnItemClickListener(new messagesAdpter.OnItemClickListener() {
            @Override
            public void onItemClick(String attachmentPath) {
                // Hiển thị hình ảnh trong giao diện của người nhận
                if (attachmentPath != null && !attachmentPath.isEmpty()) {
                    // Sử dụng thư viện Picasso hoặc Glide để tải và hiển thị hình ảnh từ URL
                    Picasso.get().load(attachmentPath).into(capturedImageView); // Hoặc Glide.with(chatWin.this).load(attachmentPath).into(capturedImageView);
                }
            }
        });

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String fcmToken = task.getResult();
                         DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(senderUid);
                         userRef.child("fcmToken").setValue(fcmToken);
                    } else {
                        Log.e("FCM", "Lỗi khi lấy token FCM: " + task.getException());
                    }
                });

        chatreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagessArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    msgModelclass messages = dataSnapshot.getValue(msgModelclass.class);
                    if (messages != null && reciverUid != null && reciverName != null) {
                        messagessArrayList.add(messages);
                    } else {
                        // Xử lý trường hợp có giá trị null
                        Log.e("DataChange", "Có giá trị null trong onDataChange");
                    }
                }
                messagesAdpter.notifyDataSetChanged();

                // Kiểm tra và hiển thị thông báo khi có tin nhắn mới từ người gửi
                if (!messagessArrayList.isEmpty()) {
                    msgModelclass latestMessage = messagessArrayList.get(messagessArrayList.size() - 1);
                    String senderUid = latestMessage.getSenderid();
                    if (senderUid != null && reciverUid != null && senderUid.equals(reciverUid)) {
                        String messageContent = "Bạn có tin nhắn mới từ " + reciverName; // Nội dung thông báo

                        // Lấy token của người nhận từ Firebase Messaging Service
                        FirebaseMessaging.getInstance().getToken()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful() && task.getResult() != null) {
                                        String receiverToken = task.getResult();

                                        // Gọi hàm để hiển thị thông báo
                                        sendNotification(messageContent, receiverToken);
                                    } else {
                                        Log.e("FCM", "Lỗi khi lấy token FCM của người nhận: " + task.getException());
                                    }
                                });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra trong quá trình đọc dữ liệu từ Firebase
            }
        });
    }

    // Gửi tin nhắn với tệp đính kèm (nếu có)
    private void sendMessage(String message, String imageUrl, String fileName) {
        DatabaseReference senderRef = database.getReference().child("chats").child(senderRoom).child("messages").push();
        DatabaseReference receiverRef = database.getReference().child("chats").child(reciverRoom).child("messages").push();

        // Tạo đối tượng tin nhắn với thông tin tin nhắn, đường dẫn hình ảnh và tên file
        msgModelclass senderMessage = new msgModelclass(message, senderUid, System.currentTimeMillis(), fileName, imageUrl);
        senderRef.setValue(senderMessage);

        msgModelclass receiverMessage = new msgModelclass(message, reciverUid, System.currentTimeMillis(), fileName, imageUrl);
        receiverRef.setValue(receiverMessage);

        // Cập nhật tin nhắn cuối cùng của người gửi
        DatabaseReference senderLastMessageRef = database.getReference().child("user").child(senderUid).child("lastMessage");
        senderLastMessageRef.setValue(message);

        // Cập nhật tin nhắn cuối cùng của người nhận
        DatabaseReference receiverLastMessageRef = database.getReference().child("user").child(reciverUid).child("lastMessage");
        receiverLastMessageRef.setValue(message);

        // Xóa nội dung tin nhắn trong ô nhập tin nhắn
        textmsg.setText("");
    }

    private void sendImageMessage(Uri fileUri, boolean isSender) {
        // Tải hình ảnh lên Firebase Storage và gửi tin nhắn với đường dẫn hình ảnh lên Firebase Realtime Database
        uploadImageToFirebaseStorage(fileUri,isSender);
    }


    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
        } else {
            // Quyền đã được cấp, tiến hành các hoạt động liên quan đến truy cập vào bộ nhớ
            // Ví dụ: Mở file picker
            openFilePicker();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Quyền đã được cấp, tiến hành các hoạt động liên quan đến truy cập vào bộ nhớ
                // Ví dụ: Mở file picker
                openFilePicker();
            } else {
                Toast.makeText(this, "Storage permission is required to access files", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(Uri.parse("content://path_to_your_shared_folder"), "image/*");
        startActivityForResult(intent, Constant.REQUEST_CODE_PICK_FILE);
    }
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, Constant.REQUEST_CODE_TAKE_PHOTO);
    }

    private void sendMessageWithImage(String imageUrl, boolean isSender) {
        // Tạo đối tượng tin nhắn mới cho người gửi
        msgModelclass message = new msgModelclass("", isSender ? SenderUID : reciverUid, System.currentTimeMillis(), imageUrl);
        messagessArrayList.add(message);
        messagesAdpter.notifyDataSetChanged();

        // Cuộn danh sách tin nhắn đến cuối để người gửi hoặc người nhận có thể thấy hình ảnh mới
        mmessangesAdpter.scrollToPosition(messagessArrayList.size() - 1);

        // Hiển thị hình ảnh cho người gửi hoặc người nhận tùy thuộc vào giá trị boolean
        if (isSender) {
            displayImageForSender(imageUrl);
        } else {
            displayImageForReceiver(imageUrl);
        }
        msgModelclass senderMessage = new msgModelclass("", SenderUID, System.currentTimeMillis(), imageUrl);
        // Tạo đối tượng tin nhắn mới cho người nhận
        msgModelclass receiverMessage = new msgModelclass("", reciverUid, System.currentTimeMillis(), imageUrl);

        // Thêm tin nhắn của người gửi vào cơ sở dữ liệu
        DatabaseReference senderRef = database.getReference().child("chats").child(senderRoom).child("messages").push();
        senderRef.setValue(senderMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Nếu tin nhắn của người gửi được gửi thành công, gửi tin nhắn cho người nhận
                    DatabaseReference receiverRef = database.getReference().child("chats").child(reciverRoom).child("messages").push();
                    receiverRef.setValue(receiverMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Nếu tin nhắn của cả hai được gửi thành công, hiển thị hình ảnh cho cả người gửi và người nhận
                                displayImageForSender(imageUrl);
                                displayImageForReceiver(imageUrl);
                            } else {
                                // Nếu gửi tin nhắn cho người nhận thất bại, hiển thị thông báo lỗi
                                Toast.makeText(chatWin.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    // Nếu gửi tin nhắn cho người gửi thất bại, hiển thị thông báo lỗi
                    Toast.makeText(chatWin.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                }
            }
        });
        messagessArrayList.add(message);
        messagesAdpter.notifyDataSetChanged();

        // Cuộn danh sách tin nhắn đến cuối để người gửi hoặc người nhận có thể thấy hình ảnh mới
        mmessangesAdpter.scrollToPosition(messagessArrayList.size() - 1);
    }


    private void displayImageForReceiver(String imageUrl) {
        // Tạo một tin nhắn với đường dẫn hình ảnh
        msgModelclass message = new msgModelclass("", reciverUid, System.currentTimeMillis(), imageUrl);
        // Thêm tin nhắn vào danh sách và thông báo adapter rằng dữ liệu đã thay đổi
        messagessArrayList.add(message);
        messagesAdpter.notifyDataSetChanged();
        // Cuộn danh sách tin nhắn đến cuối để người nhận có thể thấy hình ảnh mới
        mmessangesAdpter.scrollToPosition(messagessArrayList.size() - 1);
    }

    private void displayImageForSender(String imageUrl) {
        // Tạo một tin nhắn với đường dẫn hình ảnh
        msgModelclass message = new msgModelclass("", SenderUID, System.currentTimeMillis(), imageUrl);
        // Thêm tin nhắn vào danh sách và thông báo adapter rằng dữ liệu đã thay đổi
        messagessArrayList.add(message);
        messagesAdpter.notifyDataSetChanged();
        // Cuộn danh sách tin nhắn đến cuối để người gửi có thể thấy hình ảnh mới
        mmessangesAdpter.scrollToPosition(messagessArrayList.size() - 1);
    }
    private void uploadImageToFirebaseStorage(Uri fileUri, boolean isSender) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("chat_images/" + UUID.randomUUID().toString());

        storageRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        // Gửi tin nhắn với đường dẫn hình ảnh
                        sendMessageWithImage(imageUrl, isSender);
                    });
                })
                .addOnFailureListener(e -> {
                    // Xử lý khi tải lên thất bại
                    Toast.makeText(chatWin.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    // Upload ảnh lên Firebase Storage
                    uploadFile(selectedImageUri);
                }
            }
        } else if (requestCode == Constant.REQUEST_CODE_TAKE_PHOTO && resultCode == RESULT_OK && data != null) {
            // Xử lý kết quả chụp ảnh từ máy ảnh
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            // Lưu ảnh vào bộ nhớ và lấy đường dẫn Uri
            Uri photoUri = saveImageToStorage(imageBitmap);
            // Upload ảnh lên Firebase Storage
            uploadFile(photoUri);
        }
    }
    private Uri saveImageToStorage(Bitmap bitmap) {
        String fileName = "image_" + System.currentTimeMillis() + ".jpg";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(storageDir, fileName);

        try {
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return FileProvider.getUriForFile(this, "com.example.du_an_boxchat.fileprovider", imageFile);
    }
    private void uploadFile(Uri fileUri) {
        if (fileUri != null) {
            // Tạo tên file duy nhất sử dụng UUID
            String fileName = UUID.randomUUID().toString();

            // Tham chiếu đến nơi bạn muốn lưu trữ tệp tin trên Firebase Storage
            StorageReference fileRef = storageRef.child("chat_images/" + fileName);

            // Upload file lên Firebase Storage
            fileRef.putFile(fileUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Upload thành công, lấy URL của file
                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString();
                            // Lưu URL vào cơ sở dữ liệu Realtime Database của Firebase
                            // Ví dụ:
                            DatabaseReference messageRef = database.getReference().child("chats").child(senderRoom).child("messages").push();
                            String messageId = messageRef.getKey();
                            Map<String, Object> messageData = new HashMap<>();
                            messageData.put("attachmentPath", downloadUrl);
                            messageRef.setValue(messageData);

                            // Hiển thị thông báo hoặc thực hiện các hành động khác tùy thuộc vào nhu cầu của bạn
                            Toast.makeText(chatWin.this, "Upload thành công", Toast.LENGTH_SHORT).show();
                        });
                    })
                    .addOnFailureListener(exception -> {
                        // Xử lý khi upload thất bại
                        Toast.makeText(chatWin.this, "Upload thất bại: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Xử lý khi không có file được chọn
            Toast.makeText(chatWin.this, "Không có file được chọn", Toast.LENGTH_SHORT).show();
        }
    }
    void sendNotification(String message, String receiverToken) {
        if (receiverToken != null && !receiverToken.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject();
                JSONObject notificationObj = new JSONObject();
                notificationObj.put("title", "Thông báo mới");
                notificationObj.put("body", message);

                JSONObject dataObj = new JSONObject();
                // Đính kèm thông tin của người gửi và người nhận vào dữ liệu thông báo
                dataObj.put("senderUid", senderUid);
                dataObj.put("receiverUid", reciverUid);

                jsonObject.put("notification", notificationObj);
                jsonObject.put("data", dataObj);
                jsonObject.put("to", receiverToken);

                // Gọi hàm gửi thông báo API
                callApi(jsonObject);
            } catch (JSONException e) {
                Log.e("FCM", "Lỗi JSONException khi tạo JSON object: " + e.getMessage());
            }
        } else {
            Log.e("FCM", "Token FCM của người nhận không hợp lệ hoặc không tồn tại");
        }
    }

    void callApi(JSONObject jsonObject){
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body =RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", "key=AAAAVHMYIJ8:APA91bEkU0U9VHAQFiqU9zlbpY68UsW1mu6XbYWzmOpE-OIASkQ1WzUxRhqBs4-ruEyRmSwitKBa1PeMMswQ4NsmaDx_2epJm3f_GXzxZWlz3ubRMdbKBm4yqbGUGyDUd3OOC-Psmywg") // Thay YOUR_SERVER_KEY bằng khóa máy chủ FCM của bạn
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // Xử lý khi gặp lỗi
                Log.e("FCM", "Gửi yêu cầu thông báo thất bại: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                // Xử lý khi nhận được phản hồi từ dịch vụ FCM
                if (response.isSuccessful()) {
                    // Xử lý khi gửi thông báo thành công
                    Log.d("FCM", "Thông báo đã được gửi thành công");
                } else {
                    // Xử lý khi gặp lỗi trong quá trình gửi thông báo
                    Log.e("FCM", "Lỗi khi gửi thông báo: " + response.message());
                }
            }
        });
    }
}