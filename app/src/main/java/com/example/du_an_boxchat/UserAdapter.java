package com.example.du_an_boxchat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Users> usersArrayList;

    public UserAdapter(Context context, ArrayList<Users> usersArrayList) {
        this.context = context;
        this.usersArrayList = usersArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users users = usersArrayList.get(position);

        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(users.getUserId())){
            holder.itemView.setVisibility(View.GONE);
        }

        holder.username.setText(users.getUserName());
        holder.userstatus.setText(users.getStattus());

        String profilePicUrl = users.getProfilepic();
        if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
            Picasso.get().load(profilePicUrl).into(holder.userimg);
        } else {
            // Xử lý khi đường dẫn hình ảnh rỗng hoặc null
            // Ví dụ: Hiển thị một hình ảnh mặc định nếu có
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, chatWin.class);
                intent.putExtra("nameeee", users.getUserName());
                intent.putExtra("reciverImg", profilePicUrl);
                intent.putExtra("uid", users.getUserId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userimg;
        TextView username;
        TextView userstatus;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userimg = itemView.findViewById(R.id.userimg);
            username = itemView.findViewById(R.id.username);
            userstatus = itemView.findViewById(R.id.userstatus);
        }
    }
}
