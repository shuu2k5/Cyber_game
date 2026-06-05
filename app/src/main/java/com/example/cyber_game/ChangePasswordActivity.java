package com.example.cyber_game;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText etOldPassword, etNewPassword, etConfirmNewPassword;
    private Button btnConfirm;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        mAuth = FirebaseAuth.getInstance();

        etOldPassword        = findViewById(R.id.etOldPassword);
        etNewPassword        = findViewById(R.id.etNewPassword);
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword);
        btnConfirm           = findViewById(R.id.btnConfirmChange);

        TextView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        btnConfirm.setOnClickListener(v -> doChangePassword());
    }

    private void doChangePassword() {
        String oldPass     = etOldPassword.getText().toString().trim();
        String newPass     = etNewPassword.getText().toString().trim();
        String confirmPass = etConfirmNewPassword.getText().toString().trim();

        if (TextUtils.isEmpty(oldPass)) {
            etOldPassword.setError("Nhập mật khẩu cũ");
            etOldPassword.requestFocus(); return;
        }
        if (TextUtils.isEmpty(newPass) || newPass.length() < 6) {
            etNewPassword.setError("Mật khẩu mới ít nhất 6 ký tự");
            etNewPassword.requestFocus(); return;
        }
        if (!newPass.equals(confirmPass)) {
            etConfirmNewPassword.setError("Mật khẩu xác nhận không khớp");
            etConfirmNewPassword.requestFocus(); return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        String email = user.getEmail();

        // Xác thực mật khẩu cũ trước
        AuthCredential credential = EmailAuthProvider.getCredential(email, oldPass);
        user.reauthenticate(credential)
                .addOnSuccessListener(unused -> {
                    // Đổi sang mật khẩu mới
                    user.updatePassword(newPass)
                            .addOnSuccessListener(u -> {
                                Toast.makeText(this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show());
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Mật khẩu cũ không đúng!", Toast.LENGTH_SHORT).show());
    }
}