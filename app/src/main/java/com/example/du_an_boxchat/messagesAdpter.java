package com.example.du_an_boxchat;

import static com.example.du_an_boxchat.chatWin.reciverIImg;
import static com.example.du_an_boxchat.chatWin.senderImg;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class messagesAdpter extends RecyclerView.Adapter {
    Context context;
    ArrayList<msgModelclass> messagesAdpterArrayList;
    private static final int ITEM_SEND = 2;
    private static final int ITEM_RECEIVE = 1;

    private OnItemClickListener listener;
    private String senderImageUrl;
    private String receiverImageUrl;

    public messagesAdpter(Context context, ArrayList<msgModelclass> messagesAdpterArrayList,String senderImageUrl, String receiverImageUrl) {
        this.context = context;
        this.messagesAdpterArrayList = messagesAdpterArrayList;
        this.senderImageUrl = senderImageUrl;
        this.receiverImageUrl = receiverImageUrl;
    }

    public interface OnItemClickListener {
        void onItemClick(String attachmentPath);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == ITEM_SEND) {
            view = LayoutInflater.from(context).inflate(R.layout.sender_layout, parent, false);
            return new senderViewHolder(view, this);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.reciver_layout, parent, false);
            return new receiverViewHolder(view, this);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        msgModelclass messages = messagesAdpterArrayList.get(position);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(context).setTitle("Delete")
                        .setMessage("Are you sure you want to delete this message?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                dialogInterface.dismiss();
                            }
                        }).show();
                return false;
            }
        });
        switch (holder.getItemViewType()) {
            case ITEM_SEND:
                senderViewHolder senderHolder = (senderViewHolder) holder;
                // Hiển thị tin nhắn của người gửi
                senderHolder.bind(messages);
                break;
            case ITEM_RECEIVE:
                receiverViewHolder receiverHolder = (receiverViewHolder) holder;
                // Hiển thị tin nhắn của người nhận
                receiverHolder.bind(messages);
                break;
        }

        // Xử lý sự kiện khi nhấn vào ảnh
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kiểm tra nếu đây là một tin nhắn chứa tệp tin đính kèm
                if (messages.getAttachmentPath() != null && !messages.getAttachmentPath().isEmpty()) {
                    // Gọi phương thức onItemClick từ người nghe, chuyển đường dẫn tệp tin
                    listener.onItemClick(messages.getAttachmentPath());
                }
            }
        });
    }

    private void loadImage(String imageUrl, ImageView imageView) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Sử dụng Glide hoặc Picasso để tải hình ảnh từ đường dẫn imageUrl vào ImageView imageView
            Glide.with(context).load(imageUrl).into(imageView);
        } else {
            // Nếu đường dẫn hình ảnh rỗng, bạn có thể hiển thị một hình ảnh mặc định khác
            // Ví dụ:
            // imageView.setImageResource(R.drawable.default_image);
            // Hoặc ẩn ImageView nếu không có hình ảnh nào
            imageView.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return messagesAdpterArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        msgModelclass message = messagesAdpterArrayList.get(position);
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        return (message.getSenderid() != null && message.getSenderid().equals(currentUserUid)) ? ITEM_SEND : ITEM_RECEIVE;
    }

    private String getFormattedTime(long timeStamp) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return formatter.format(new Date(timeStamp));
    }

    static class senderViewHolder extends RecyclerView.ViewHolder {
        ImageView imageForAttachment;
        CircleImageView circleImageView;
        TextView msgtxt;
        TextView timesender;
        messagesAdpter adapter;

        public senderViewHolder(@NonNull View itemView, messagesAdpter adapter) {
            super(itemView);
            this.adapter = adapter;
            circleImageView = itemView.findViewById(R.id.profilerggg);
            msgtxt = itemView.findViewById(R.id.msgsendertyp);
            timesender = itemView.findViewById(R.id.senderTime);
            imageForAttachment = itemView.findViewById(R.id.imageForAttachment);
            Linkify.addLinks(msgtxt, Linkify.WEB_URLS);
        }

        void bind(msgModelclass message) {
            msgtxt.setText(message.getMessage());
            timesender.setText(adapter.getFormattedTime(message.getTimeStamp()));
            // Hiển thị ảnh đại diện của người gửi
            Glide.with(itemView.getContext()).load(senderImg).into(circleImageView);

            if (message.getAttachmentPath() != null && !message.getAttachmentPath().isEmpty()) {
                // Hiển thị ảnh nếu có đính kèm
                Glide.with(itemView.getContext()).load(message.getAttachmentPath()).into(imageForAttachment);
                imageForAttachment.setVisibility(View.VISIBLE);
            } else {
                imageForAttachment.setVisibility(View.GONE);
            }
        }

    }

    static class receiverViewHolder extends RecyclerView.ViewHolder {
        ImageView imageForAttachment;
        CircleImageView circleImageView;
        TextView msgtxt;
        TextView receiverTime;
        messagesAdpter adapter;

        public receiverViewHolder(@NonNull View itemView,  messagesAdpter adapter) {
            super(itemView);
            this.adapter = adapter;
            circleImageView = itemView.findViewById(R.id.pro);
            msgtxt = itemView.findViewById(R.id.recivertextset);
            imageForAttachment = itemView.findViewById(R.id.imageForAttachment);
            receiverTime = itemView.findViewById(R.id.timeTextView);
            Linkify.addLinks(msgtxt, Linkify.WEB_URLS);
        }

        void bind(msgModelclass message) {
            msgtxt.setText(message.getMessage());
            receiverTime.setText(adapter.getFormattedTime(message.getTimeStamp()));
            // Hiển thị ảnh đại diện của người nhận
            Glide.with(itemView.getContext()).load(reciverIImg).into(circleImageView);

            if (message.getAttachmentPath() != null && !message.getAttachmentPath().isEmpty()) {
                // Hiển thị ảnh nếu có đính kèm
                Glide.with(itemView.getContext()).load(message.getAttachmentPath()).into(imageForAttachment);
                imageForAttachment.setVisibility(View.VISIBLE);
            } else {
                imageForAttachment.setVisibility(View.GONE);
            }
        }
    }

}
