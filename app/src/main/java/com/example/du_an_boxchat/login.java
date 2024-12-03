package com.example.du_an_boxchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Map;

public class login extends AppCompatActivity {
    TextView logsignup, txtForgotpassword;
    Button btn;
    EditText email, password;
    FirebaseAuth auth;
    String emailPattern = "[a-zA-z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        btn = findViewById(R.id.logbutton);
        email = findViewById(R.id.editTextLogEmail);
        password = findViewById(R.id.editTextLogPassword);
        logsignup = findViewById(R.id.logsignup);
        txtForgotpassword = findViewById(R.id.txtForgotPassword);

        logsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login.this, registration.class);
                startActivity(intent);
                finish();
            }
        });

        btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String Email = email.getText().toString();
                String pass = password.getText().toString();

                if(TextUtils.isEmpty(Email)){
                    Toast.makeText(login.this,"Enter the email", Toast.LENGTH_SHORT).show();
                    return;
                } else if(TextUtils.isEmpty(pass)){
                    Toast.makeText(login.this,"Enter the password", Toast.LENGTH_SHORT).show();
                    return;
                } else if(!Email.matches(emailPattern)){
                    email.setError("Give Proper Email Address");
                    return;
                } else if(pass.length() < 6){
                    password.setError("More than six Characters");
                    Toast.makeText(login.this,"Password Needs To Be Longer Then Six Characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog.show(); // Hiển thị ProgressDialog trước khi đăng nhập

//                auth.signInWithEmailAndPassword(Email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        progressDialog.dismiss(); // Đóng ProgressDialog sau khi đăng nhập
//
//                        if(task.isSuccessful()) {
//                            try {
//                                Intent intent = new Intent(login.this, MainActivity.class);
//                                startActivity(intent);
//                                finish();
//                            } catch (Exception e){
//                                Toast.makeText(login.this,e.getMessage(),Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            Toast.makeText(login.this, task.getException().getMessage(),Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });

                auth.signInWithEmailAndPassword(Email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss(); // Đóng ProgressDialog sau khi đăng nhập

                        if(task.isSuccessful()) {
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            if (currentUser != null) {
                                String userId = currentUser.getUid();
                                String userEmail = currentUser.getEmail();

                                // Tạo một bản ghi cho người dùng trong Firestore
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                DocumentReference userRef = db.collection("users").document(userId);

                                Map<String, Object> user = new HashMap<>();
                                user.put("userId", userId);
                                user.put("email", userEmail);

                                userRef.set(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("Firestore", "DocumentSnapshot added with ID: " + userId);
                                                Intent intent = new Intent(login.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("Firestore", "Error adding document", e);
                                                Toast.makeText(login.this, "Error adding document to Firestore", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(login.this, "User not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(login.this, task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        txtForgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login.this, ForgotPassword.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
