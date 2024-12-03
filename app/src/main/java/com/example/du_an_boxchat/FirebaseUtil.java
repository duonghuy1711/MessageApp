package com.example.du_an_boxchat;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseUtil {

    public static Task<Users> currentUserDetails() {
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(currentUserId());
        return docRef.get().continueWith(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Chuyển đổi DocumentSnapshot thành đối tượng Users và trả về
                    return document.toObject(Users.class);
                } else {
                    // Document không tồn tại, xử lý tương ứng (ví dụ: trả về null)
                    return null;
                }
            } else {
                // Xử lý khi có lỗi xảy ra trong quá trình lấy dữ liệu
                Exception e = task.getException();
                Log.w(TAG, "Error getting document: " + e);
                return null;
            }
        });
    }


    // Sửa phương thức để lấy dữ liệu của người dùng khác trong phòng trò chuyện
    public static Task<Users> getOtherUserFromChatroom(String otherUserId) {
        return allUserCollectionReference().document(otherUserId).get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            return documentSnapshot.toObject(Users.class);
                        }
                    }
                    return null;
                });
    }

    public static String currentUserId(){
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public static boolean isLoggedIn(){
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public static CollectionReference allUserCollectionReference(){
        return FirebaseFirestore.getInstance().collection("users");
    }

    public static DocumentReference getChatroomReference(String chatroomId){
        return FirebaseFirestore.getInstance().collection("chatsroom").document(chatroomId);
    }

    public static CollectionReference getChatroomMessageReference(String chatroomId){
        return getChatroomReference(chatroomId).collection("chats");
    }

    public static String getChatroomId(String userId1, String userId2){
        if(userId1.hashCode() < userId2.hashCode()){
            return userId1 + "_" + userId2;
        } else {
            return userId2 + "_" + userId1;
        }
    }

    public static CollectionReference allChatroomCollectionReference(){
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }

    public static void logout(){
        FirebaseAuth.getInstance().signOut();
    }

    public static StorageReference getCurrentProfilePicStorageRef(){
        return FirebaseStorage.getInstance().getReference().child("profile_pic").child(currentUserId());
    }

    public static StorageReference getOtherProfilePicStorageRef(String otherUserId){
        return FirebaseStorage.getInstance().getReference().child("profile_pic").child(otherUserId);
    }
}
