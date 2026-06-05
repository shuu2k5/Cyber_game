package com.example.cyber_game;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etPhone, etEmail, etPassword, etConfirmPass;
    private Button btnRegister;
    private TextView btnBack, tvLogin, tvTogglePassword, tvToggleConfirmPass;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private boolean isPasswordVisible = false;
    private boolean isConfirmVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        etFullName         = findViewById(R.id.etFullName);
        etPhone            = findViewById(R.id.etPhone);
        etEmail            = findViewById(R.id.etEmail);
        etPassword         = findViewById(R.id.etPassword);
        etConfirmPass      = findViewById(R.id.etConfirmPass);
        btnRegister        = findViewById(R.id.btnRegister);
        btnBack            = findViewById(R.id.btnBack);
        tvLogin            = findViewById(R.id.tvLogin);
        progressBar        = findViewById(R.id.progressBar);
        tvTogglePassword   = findViewById(R.id.tvTogglePassword);
        tvToggleConfirmPass = findViewById(R.id.tvToggleConfirmPass);

        btnBack.setOnClickListener(v -> finish());
        tvLogin.setOnClickListener(v -> finish());
        btnRegister.setOnClickListener(v -> doRegister());

        // Toggle mật khẩu
        tvTogglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                etPassword.setInputType(
                        android.text.InputType.TYPE_CLASS_TEXT |
                                android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                tvTogglePassword.setText("👁");
                isPasswordVisible = false;
            } else {
                etPassword.setInputType(
                        android.text.InputType.TYPE_CLASS_TEXT |
                                android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                tvTogglePassword.setText("🙈");
                isPasswordVisible = true;
            }
            etPassword.setSelection(etPassword.getText().length());
        });

        // Toggle xác nhận mật khẩu
        tvToggleConfirmPass.setOnClickListener(v -> {
            if (isConfirmVisible) {
                etConfirmPass.setInputType(
                        android.text.InputType.TYPE_CLASS_TEXT |
                                android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                tvToggleConfirmPass.setText("👁");
                isConfirmVisible = false;
            } else {
                etConfirmPass.setInputType(
                        android.text.InputType.TYPE_CLASS_TEXT |
                                android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                tvToggleConfirmPass.setText("🙈");
                isConfirmVisible = true;
            }
            etConfirmPass.setSelection(etConfirmPass.getText().length());
        });
    }

    private void doRegister() {
        String fullName = etFullName.getText().toString().trim();
        String phone    = etPhone.getText().toString().trim();
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirm  = etConfirmPass.getText().toString().trim();

        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Vui lòng nhập họ và tên");
            etFullName.requestFocus(); return;
        }
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Vui lòng nhập số điện thoại");
            etPhone.requestFocus(); return;
        }
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Vui lòng nhập email");
            etEmail.requestFocus(); return;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            etPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            etPassword.requestFocus(); return;
        }
        if (!password.equals(confirm)) {
            etConfirmPass.setError("Mật khẩu xác nhận không khớp");
            etConfirmPass.requestFocus(); return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    // Lưu thông tin user vào Realtime Database
                    String uid = mAuth.getCurrentUser().getUid();
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference("users").child(uid);

                    java.util.HashMap<String, Object> userMap = new java.util.HashMap<>();
                    userMap.put("fullName", fullName);
                    userMap.put("phone", phone);
                    userMap.put("email", email);
                    userMap.put("ranking", "Thành viên");
                    userMap.put("role", "user");
                    userMap.put("userId", String.valueOf((int)(Math.random() * 9000) + 1000));
                    db.setValue(userMap).addOnSuccessListener(unused -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnRegister.setEnabled(true);
                    Toast.makeText(this, "Đăng ký thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });

    }
}