package com.example.cyber_game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class fragment_home extends Fragment{
    private TableLayout layoutAdmin1;
    private LinearLayout layoutUser, layoutAdmin2;
    private EditText edtIDusers, edtTakeMoney;
    private Button btnNap;
    private TextView tvPendingOrders, tvRevenue;
    private View boxPendingOrders;
    private android.os.Handler timeHandler = new android.os.Handler();
    private Runnable timeRunnable;
    private MaterialCardView ClickMoney;
    private long lastClickTime = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_home,
                container,
                false
        );

        TextView txtUserId = view.findViewById(R.id.textView22);

        // Ánh xạ giao diện
        tvPendingOrders = view.findViewById(R.id.edtOrder);

        tvRevenue = view.findViewById(R.id.edtmoney);
        boxPendingOrders = view.findViewById(R.id.edtOrder);

        layoutAdmin1 = view.findViewById(R.id.layout_admin);
        layoutAdmin2 = view.findViewById(R.id.layout_admin2);
        layoutUser = view.findViewById(R.id.layout_users);

        edtIDusers = view.findViewById(R.id.edtIDusers);
        edtTakeMoney = view.findViewById(R.id.edtTakeMoney);
        btnNap = view.findViewById(R.id.btnNap);

        ClickMoney = view.findViewById(R.id.ClickMoney);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(user.getUid())
                    .child("userId")
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        txtUserId.setText("ID: " + snapshot.getValue());
                    });

            FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(user.getUid())
                    .child("role")
                    .get()
                    .addOnSuccessListener(snapshot -> {

                        String role = snapshot.getValue(String.class);

                        if ("admin".equals(role)) {
                            // --- HIỂN THỊ GIAO DIỆN ADMIN ---
                            layoutAdmin1.setVisibility(View.VISIBLE);
                            layoutAdmin2.setVisibility(View.VISIBLE);
                            layoutUser.setVisibility(View.GONE);

                            countPendingOrders();
                            listenToRevenue();
                            setupPendingOrdersClick();

                            btnNap.setOnClickListener(v -> {
                                String targetId = edtIDusers.getText().toString().trim();
                                String amountStr = edtTakeMoney.getText().toString().trim();

                                // Kiểm tra xem Admin có để trống ô nào không
                                if (targetId.isEmpty() || amountStr.isEmpty()) {
                                    Toast.makeText(getContext(), "Vui lòng nhập Số ID và Số tiền!", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                long amountToTopup = Long.parseLong(amountStr);

                                // Lên Firebase lùng sục đúng cái "userId" mà Admin vừa nhập
                                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                                usersRef.orderByChild("userId").equalTo(targetId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            // Tìm thấy chính xác User đó rồi! Bắt đầu cộng tiền
                                            for (DataSnapshot userSnap : snapshot.getChildren()) {

                                                // 1. Cộng tiền
                                                long currentBalance = userSnap.hasChild("balance") ? userSnap.child("balance").getValue(Long.class) : 0;
                                                long newBalance = currentBalance + amountToTopup;

                                                // 2. Quy đổi ra giờ (Tỷ giá: 10.000đ = 60 phút)
                                                long addedMinutes = (amountToTopup * 60) / 10000;
                                                long currentMinutes = userSnap.hasChild("remainingMinutes") ? userSnap.child("remainingMinutes").getValue(Long.class) : 0;
                                                long newMinutes = currentMinutes + addedMinutes;

                                                // 3. Đẩy thông tin mới cập nhật lên Firebase
                                                userSnap.getRef().child("balance").setValue(newBalance);
                                                userSnap.getRef().child("remainingMinutes").setValue(newMinutes);

                                                Toast.makeText(getContext(), "Đã nạp " + amountStr + "đ cho ID: " + targetId, Toast.LENGTH_SHORT).show();

                                                // Nạp xong thì xóa chữ trên màn hình đi để sẵn sàng nạp cho người tiếp theo
                                                edtIDusers.setText("");
                                                edtTakeMoney.setText("");
                                            }
                                        } else {
                                            // Cảnh báo nếu Admin gõ nhầm ID không tồn tại
                                            Toast.makeText(getContext(), "Không tìm thấy người chơi có ID: " + targetId, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                                });
                            });
                            ClickMoney.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Lấy thời gian hiện tại lúc ngón tay vừa chạm vào
                                    long currentClickTime = System.currentTimeMillis();

                                    // Nếu khoảng cách giữa 2 lần bấm < 300 mili-giây thì kích hoạt
                                    if (currentClickTime - lastClickTime < 300) {

                                        // Trỏ lên Firebase và ép biến Revenue về số 0
                                        DatabaseReference revRef = FirebaseDatabase.getInstance().getReference("System").child("Revenue");
                                        revRef.setValue(0).addOnSuccessListener(aVoid -> {
                                            Toast.makeText(getContext(), "Đã lấy tiền doanh thu thành công!", Toast.LENGTH_SHORT).show();
                                        });

                                    }

                                    // Lưu lại mốc thời gian của lần bấm này để so sánh cho lần sau
                                    lastClickTime = currentClickTime;
                                }
                            });

                        } else {
                            // --- HIỂN THỊ GIAO DIỆN USER ---
                            layoutAdmin1.setVisibility(View.GONE);
                            layoutAdmin2.setVisibility(View.GONE);
                            layoutUser.setVisibility(View.VISIBLE);

                            TextView tvRemainingBalance = view.findViewById(R.id.edtMoneyEnd);
                            TextView tvRemainingTime = view.findViewById(R.id.edtTimeEnd);
                            TextView tvUsedTime = view.findViewById(R.id.edtTimeUsers);

                            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());

                            // 1. LẮNG NGHE FIREBASE (Chỉ làm nhiệm vụ in ra màn hình)
                            myRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snap) {
                                    if (snap.exists()) {
                                        // 1.1 Hiển thị Tiền còn lại
                                        long balance = snap.hasChild("balance") ? snap.child("balance").getValue(Long.class) : 0;
                                        String formattedBal = String.format("%,d", balance).replace(",", ".");
                                        if (tvRemainingBalance != null) tvRemainingBalance.setText(formattedBal);

                                        // 1.2 Hiển thị Thời gian còn lại
                                        long totalMinutes = snap.hasChild("remainingMinutes") ? snap.child("remainingMinutes").getValue(Long.class) : 0;
                                        long hours = totalMinutes / 60;
                                        long mins = totalMinutes % 60;
                                        if (tvRemainingTime != null) tvRemainingTime.setText(String.format("%d:%02d", hours, mins));

                                        // 1.3 💥 MỚI: Hiển thị Thời gian đã dùng
                                        long usedTotalMins = snap.hasChild("usedMinutes") ? snap.child("usedMinutes").getValue(Long.class) : 0;
                                        long usedHours = usedTotalMins / 60;
                                        long usedMins = usedTotalMins % 60;
                                        if (tvUsedTime != null) tvUsedTime.setText(String.format("%d:%02d", usedHours, usedMins));
                                    }
                                }
                                @Override public void onCancelled(@NonNull DatabaseError error) {}
                            });

                            // 2. KÍCH HOẠT ĐỒNG HỒ (Xử lý cùng lúc 2 biến)
                            timeRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    // Tải toàn bộ ví của User về để tính toán
                                    myRef.get().addOnSuccessListener(snapUser -> {
                                        if (snapUser.exists()) {
                                            long currentMins = snapUser.hasChild("remainingMinutes") ? snapUser.child("remainingMinutes").getValue(Long.class) : 0;
                                            long usedMins = snapUser.hasChild("usedMinutes") ? snapUser.child("usedMinutes").getValue(Long.class) : 0;

                                            // Nếu còn tiền (còn giờ)
                                            if (currentMins > 0) {
                                                // Trừ 1 phút thời gian còn lại
                                                myRef.child("remainingMinutes").setValue(currentMins - 1);

                                                // Cộng 1 phút vào thời gian đã dùng
                                                myRef.child("usedMinutes").setValue(usedMins + 1);
                                            }
                                        }
                                    });
                                    // Vòng lặp đếm ngược mỗi 60 giây (60000 mili-giây)
                                    timeHandler.postDelayed(this, 60000);
                                }
                            };
                            timeHandler.postDelayed(timeRunnable, 60000);
                        }
                    })
                    .addOnFailureListener(e -> {
                        layoutAdmin1.setVisibility(View.GONE);
                        layoutUser.setVisibility(View.VISIBLE);
                    });
        }

        return view;

    }

    // 1. Hàm đếm đơn hàng (Giữ nguyên của bạn)
    private void countPendingOrders() {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Orders");
        ordersRef.orderByChild("status").equalTo("pending")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long pendingCount = snapshot.getChildrenCount();
                        if(tvPendingOrders != null){
                            tvPendingOrders.setText(String.valueOf(pendingCount));
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    // 2. Hàm lắng nghe và cập nhật Doanh Thu
    private void listenToRevenue() {
        DatabaseReference revRef = FirebaseDatabase.getInstance().getReference("System").child("Revenue");
        revRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long rev = 0;
                if (snapshot.exists() && snapshot.getValue() != null) {
                    rev = snapshot.getValue(Long.class);
                }
                String formattedRev = String.format("%,d", rev).replace(",", ".") + "đ";

                if (tvRevenue != null) {
                    tvRevenue.setText(formattedRev);
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // 3. Hàm xử lý khi bấm vào Khung Đơn Đang Chờ
    private void setupPendingOrdersClick() {
        if (boxPendingOrders == null) return;

        boxPendingOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.layout_bottom_sheet_orders, null);
                bottomSheetDialog.setContentView(dialogView);

                RecyclerView rcvOrders = dialogView.findViewById(R.id.rcvPendingOrders);
                rcvOrders.setLayoutManager(new LinearLayoutManager(getContext()));

                // Tải danh sách đơn liên tục
                DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Orders");
                ordersRef.orderByChild("status").equalTo("pending").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Order> currentOrders = new ArrayList<>();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            Order order = data.getValue(Order.class);
                            if (order != null) currentOrders.add(order);
                        }

                        // Đẩy dữ liệu vào Adapter để hiển thị
                        OrderAdapter adapter = new OrderAdapter(currentOrders);
                        rcvOrders.setAdapter(adapter);

                        // Tự động đóng bảng nếu đã hoàn thành hết đơn
                        if (currentOrders.isEmpty() && bottomSheetDialog.isShowing()) {
                            bottomSheetDialog.dismiss();
                            Toast.makeText(getContext(), "Không còn đơn hàng nào đang chờ!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });

                bottomSheetDialog.show();
            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Xóa đồng hồ khi người dùng chuyển sang tab khác để tiết kiệm pin
        if (timeHandler != null && timeRunnable != null) {
            timeHandler.removeCallbacks(timeRunnable);
        }
    }
}