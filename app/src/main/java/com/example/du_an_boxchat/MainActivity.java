package com.example.du_an_boxchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.du_an_boxchat.chatWin;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    RecyclerView mainUserRecyclerView;
    UserAdapter adapter;
    FirebaseDatabase database;
    ArrayList<Users> usersArrayList;
    ImageView imglogout;
    ImageView cumbut, setbut;
    ImageView telephone;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        cumbut = findViewById(R.id.camBut);
        setbut = findViewById(R.id.settingBut);

        telephone = findViewById(R.id.icontelephone);

        DatabaseReference reference = database.getReference().child("user");

        usersArrayList = new ArrayList<>();

        mainUserRecyclerView = findViewById(R.id.mainUserRecyclerView);
        mainUserRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdapter(MainActivity.this,usersArrayList);
        mainUserRecyclerView.setAdapter(adapter);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    usersArrayList.add(users);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Lỗi xảy ra khi hủy lắng nghe dữ liệu từ Firebase: " + error.getMessage());
            }
        });
        imglogout = findViewById(R.id.logoutimg);

        imglogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFinishing()) { // Kiểm tra xem activity có đang kết thúc không
                    Dialog dialog = new Dialog(MainActivity.this, R.style.dialog);
                    dialog.setContentView(R.layout.dialog_layout);
                    Button yes, no;
                    yes = dialog.findViewById(R.id.yesbtn);
                    no = dialog.findViewById(R.id.nobtn);
                    yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(MainActivity.this, login.class);
                            startActivity(intent);
                            finish();
                        }
                    });

                    no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show(); // Chỉ hiển thị dialog nếu activity vẫn tồn tại
                }
            }
        });

        setbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, setting.class);
                startActivity(intent);
            }
        });

        cumbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 10);
            }
        });

        if(auth.getCurrentUser() == null) {
            Intent intent = new Intent(MainActivity.this, login.class);
            startActivity(intent);
        }

        telephone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, calling_class.class);
                startActivity(intent);
            }
        });

        getToken();
    }

    void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                String token = task.getResult();
                Log.d("FCM", "Token FCM: " + token); // In ra token FCM trong logcat để kiểm tra
                // Cập nhật token FCM cho người dùng hiện tại
                updateFCMToken(token);
            } else {
                Log.e("FCM", "Lỗi khi lấy token FCM: " + task.getException()); // In ra lỗi nếu có
            }
        });
    }

    // Phương thức để cập nhật token FCM cho người dùng hiện tại trong cơ sở dữ liệu Firebase
    void updateFCMToken(String token) {
        DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(auth.getCurrentUser().getUid());
        currentUserRef.child("fcmToken").setValue(token).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("FCM", "Token FCM đã được cập nhật thành công cho người dùng hiện tại");
                } else {
                    Log.e("FCM", "Lỗi khi cập nhật token FCM cho người dùng hiện tại: " + task.getException());
                }
            }
        });
    }
}