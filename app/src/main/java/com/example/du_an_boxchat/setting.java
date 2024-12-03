package com.example.du_an_boxchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class setting extends AppCompatActivity {
    ImageView setprofile;
    EditText setname, setstatus;
    Button donebut;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    String email, password;
    Uri setImageUri;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        setprofile = findViewById(R.id.settingprofile);
        setname = findViewById(R.id.settingname);
        setstatus = findViewById(R.id.settingstatus);
        donebut = findViewById(R.id.donebutt);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving...");
        progressDialog.setCancelable(false);

        DatabaseReference reference = database.getReference().child("user").child(auth.getUid());
        StorageReference storageReference = storage.getReference().child("upload").child(auth.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    email = snapshot.child("mail").getValue(String.class);
                    password = snapshot.child("password").getValue(String.class);
                    String name = snapshot.child("userName").getValue(String.class);
                    String profile = snapshot.child("profilepic").getValue(String.class);
                    String status = snapshot.child("status").getValue(String.class);
                    if (name != null) setname.setText(name);
                    if (status != null) setstatus.setText(status);
                    if (profile != null) {
                        if (!profile.isEmpty()) {
                            Picasso.get().load(profile).into(setprofile);
                        } else {
                            // Xử lý khi đường dẫn ảnh rỗng
                        }
                    } else {
                        // Xử lý khi không có đường dẫn ảnh
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra trong quá trình đọc dữ liệu từ Firebase
            }
        });


        setprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
            }
        });

        donebut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();

                String name = setname.getText().toString();
                String status = setstatus.getText().toString();

                if (setImageUri != null) {
                    storageReference.putFile(setImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String finalimageUri = uri.toString();
                                        Users users = new Users(auth.getUid(), name, email, password, finalimageUri, status);
                                        reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(setting.this, "Data Is Saved", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(setting.this, MainActivity.class));
                                                    finish();
                                                } else {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(setting.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                });
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(setting.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    // No image selected, only update other fields
                    Users users = new Users(auth.getUid(), name, email, password, "", status);
                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                Toast.makeText(setting.this, "Data Is Saved", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(setting.this, MainActivity.class));
                                finish();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(setting.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                reference.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(setting.this, "Status updated successfully", Toast.LENGTH_SHORT).show();
                            // Đặt lại dữ liệu trên giao diện người dùng nếu cần thiết
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(setting.this, "Failed to update status", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            setImageUri = data.getData();
            setprofile.setImageURI(setImageUri);
        }
    }
}
