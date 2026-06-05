package com.example.cyber_game;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private List<ChatMessage> messages;
    private String currentUid;

    public ChatAdapter(List<ChatMessage> messages, String currentUid) {
        this.messages   = messages;
        this.currentUid = currentUid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage msg = messages.get(position);
        holder.tvMessage.setText(msg.text);
        holder.tvTime.setText(msg.time);

        if ("user".equals(msg.sender)) {
            // Tin nhắn của user — bên phải, màu xanh
            holder.container.setGravity(Gravity.END);
            holder.tvMessage.setBackgroundResource(R.drawable.bg_button_primary);
            holder.tvMessage.setTextColor(0xFFFFFFFF);
        } else {
            // Tin nhắn của admin — bên trái, màu xám
            holder.container.setGravity(Gravity.START);
            holder.tvMessage.setBackgroundColor(0xFF2A2F4A);
            holder.tvMessage.setTextColor(0xFFFFFFFF);
        }
    }

    @Override
    public int getItemCount() { return messages.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout container;
        TextView tvMessage, tvTime;
        ViewHolder(View view) {
            super(view);
            container = view.findViewById(R.id.messageContainer);
            tvMessage = view.findViewById(R.id.tvMessage);
            tvTime    = view.findViewById(R.id.tvTime);
        }
    }
}