package com.example.cyber_game;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminSupportActivity extends AppCompatActivity {

    private LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_support);

        TextView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        container = findViewById(R.id.supportContainer);

        FirebaseDatabase.getInstance().getReference("chats")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        container.removeAllViews();
                        for (DataSnapshot userChat : snapshot.getChildren()) {
                            String uid = userChat.getKey();

                            DataSnapshot messages = userChat.child("messages");
                            String lastMsg = "";
                            String lastTime = "";
                            for (DataSnapshot msg : messages.getChildren()) {
                                lastMsg  = msg.child("text").getValue(String.class);
                                lastTime = msg.child("time").getValue(String.class);
                            }

                            String finalLastMsg  = lastMsg;
                            String finalLastTime = lastTime;
                            String finalUid      = uid;

                            // Lấy tên + email từ users
                            FirebaseDatabase.getInstance().getReference("users").child(uid)
                                    .get().addOnSuccessListener(userSnap -> {
                                        String name  = userSnap.child("fullName").getValue(String.class);
                                        String email = userSnap.child("email").getValue(String.class);

                                        TextView tv = new TextView(AdminSupportActivity.this);
                                        tv.setText("👤 " + name + "\n📧 " + email + "\n💬 " + finalLastMsg + "\n🕐 " + finalLastTime);
                                        tv.setTextColor(0xFFFFFFFF);
                                        tv.setTextSize(13);
                                        tv.setPadding(32, 24, 32, 24);
                                        tv.setBackgroundColor(0xFF1A1F3A);
                                        tv.setClickable(true);
                                        tv.setFocusable(true);

                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.MATCH_PARENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT
                                        );
                                        params.setMargins(0, 0, 0, 16);
                                        tv.setLayoutParams(params);

                                        tv.setOnClickListener(v -> {
                                            Intent intent = new Intent(AdminSupportActivity.this, AdminChatActivity.class);
                                            intent.putExtra("uid", finalUid);
                                            startActivity(intent);
                                        });

                                        container.addView(tv);
                                    });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}