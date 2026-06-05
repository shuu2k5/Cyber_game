package com.example.cyber_game;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class fragment_person extends Fragment {
    private TextView tvUsername, tvRanking;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_person, container, false);

        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);

        tvUsername = view.findViewById(R.id.tvUsername);
        tvRanking  = view.findViewById(R.id.tvRanking);

        // Load thông tin từ Firebase
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name    = snapshot.child("fullName").getValue(String.class);
                String ranking = snapshot.child("ranking").getValue(String.class);
                if (name != null)    tvUsername.setText(name);
                if (ranking != null) tvRanking.setText("Ranking: " + ranking);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        // Bấm vào tên → đổi tên
        tvUsername.setOnClickListener(v -> showEditNameDialog());

        LinearLayout rowSupport = view.findViewById(R.id.rowSupport);

        rowSupport.setOnClickListener(v -> {
            userRef.child("role").get().addOnSuccessListener(snapshot -> {
                String role = snapshot.getValue(String.class);
                if ("admin".equals(role)) {
                    startActivity(new Intent(getActivity(), AdminSupportActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), SupportChatActivity.class));
                }
            });

        });

        // Đổi mật khẩu
        LinearLayout rowChangePass = view.findViewById(R.id.rowChangePass);
        rowChangePass.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
        });

        // Thông tin chung
        LinearLayout rowInfo = view.findViewById(R.id.rowGeneralInfo);
        rowInfo.setOnClickListener(v -> {
            BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.layout_bottom_sheet_info, null);
            dialog.setContentView(dialogView);
            ImageView btnClose = dialogView.findViewById(R.id.btnClose);
            btnClose.setOnClickListener(x -> dialog.dismiss());
            dialog.show();
        });

        // Đăng xuất
        LinearLayout rowLogout = view.findViewById(R.id.rowLogout);
        rowLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Đăng xuất")
                    .setMessage("Bạn có chắc muốn đăng xuất không?")
                    .setPositiveButton("Đăng xuất", (dialog, which) -> {
                        mAuth.signOut();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        return view;
    }

    private void showEditNameDialog() {
        EditText input = new EditText(getContext());
        input.setHint("Nhập tên mới");
        input.setText(tvUsername.getText());

        new AlertDialog.Builder(getContext())
                .setTitle("Đổi tên")
                .setView(input)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String newName = input.getText().toString().trim();
                    if (newName.isEmpty()) {
                        Toast.makeText(getContext(), "Tên không được để trống", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    userRef.child("fullName").setValue(newName)
                            .addOnSuccessListener(unused ->
                                    Toast.makeText(getContext(), "Đã cập nhật tên!", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e ->
                                    Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}