package com.example.cyber_game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class fragment_service extends Fragment {

    private RecyclerView rcvProducts;
    private ProductAdapter adapter;
    private List<product> productList;
    private List<product> myCart = new ArrayList<>();
    private long totalCartPrice = 0;
    private DatabaseReference databaseReference;
    Button btnPay;

    @NonNull
    @Override
    public View onCreateView (@NonNull LayoutInflater inflater,
                              @Nullable ViewGroup container,
                              @Nullable Bundle saveInstanceState){
        View view = inflater.inflate(R.layout.fragment_service, container, false);

        // Ánh xạ giao diện
        MaterialCardView btnFood = view.findViewById(R.id.btnfood);
        MaterialCardView btnWater = view.findViewById(R.id.btnwater);
        MaterialCardView btnMoney = view.findViewById(R.id.btnmoney);
        MaterialCardView btnCombo = view.findViewById(R.id.btncombo);
        rcvProducts = view.findViewById(R.id.edtproduct);
        btnPay = view.findViewById(R.id.btnpay);

        // Cài đặt RecyclerView (Chia 2 cột)
        rcvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        productList = new ArrayList<>();

        // Trỏ sẵn tới kho "Products" trên Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("Products");

        // Đẩy menu mẫu lên Firebase nếu chưa có
        upMenu();

        // Cấu hình Adapter lắng nghe sự thay đổi của từng món ăn
        adapter = new ProductAdapter(productList, new ProductAdapter.OnCartChangeListener() {
            @Override
            public void onCartUpdated(product updatedProduct) {
                // 1. KIỂM TRA XEM MÓN NÀY ĐÃ CÓ TRONG GIỎ CHƯA
                boolean isAlreadyInCart = false;
                for (int i = 0; i < myCart.size(); i++) {
                    if (myCart.get(i).getName().equals(updatedProduct.getName())) {
                        if (updatedProduct.getQuantity() > 0) {
                            myCart.set(i, updatedProduct); // Cập nhật số lượng mới
                        } else {
                            myCart.remove(i); // Nếu khách trừ về 0 thì đuổi khỏi giỏ
                        }
                        isAlreadyInCart = true;
                        break;
                    }
                }

                // 2. NẾU MÓN MỚI TINH THÌ THÊM VÀO GIỎ
                if (!isAlreadyInCart && updatedProduct.getQuantity() > 0) {
                    myCart.add(updatedProduct);
                }

                // 3. TÍNH TOÁN LẠI TỔNG TIỀN TỪ myCart (Giỏ hàng tổng)
                totalCartPrice = 0;
                int totalItems = 0;

                for (product p : myCart) {
                    totalItems += p.getQuantity();
                    long price = Long.parseLong(p.getPrice().replaceAll("[^0-9]", ""));
                    totalCartPrice += (price * p.getQuantity());
                }

                // 4. CẬP NHẬT CHỮ TRÊN NÚT GIỎ HÀNG
                String formattedPrice = String.format("%,d", totalCartPrice).replace(",", ".") + "đ";
                if (totalItems > 0) {
                    btnPay.setText("GIỎ HÀNG (" + totalItems + ") - " + formattedPrice);
                } else {
                    btnPay.setText("XEM GIỎ HÀNG");
                }
            }
        });
        rcvProducts.setAdapter(adapter);

        // Mặc định lúc mới vào màn hình sẽ tự động tải danh sách Đồ ăn luôn cho đẹp mắt
        loadProductsFromFirebase("food");

        // Bắt sự kiện Click cho nút ĐỒ ĂN
        btnFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rcvProducts.setVisibility(View.VISIBLE);
                loadProductsFromFirebase("food");
            }
        });

        // Bắt sự kiện Click cho nút ĐỒ UỐNG
        btnWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rcvProducts.setVisibility(View.VISIBLE);
                loadProductsFromFirebase("drink");
            }
        });

        // Bắt sự kiện Click cho nút NẠP TIỀN
        btnMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rcvProducts.setVisibility(View.VISIBLE);
                loadProductsFromFirebase("money");
            }
        });

        // Bắt sự kiện Click cho nút COMBO
        btnCombo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rcvProducts.setVisibility(View.VISIBLE);
                loadProductsFromFirebase("combo");
            }
        });

        // --- MỞ BẢNG TRƯỢT BOTTOM SHEET ---
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myCart.isEmpty()) {
                    Toast.makeText(getContext(), "Giỏ hàng đang trống, hãy chọn món nhé!", Toast.LENGTH_SHORT).show();
                    return;
                }

                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.layout_bottom_sheet_cart, null);
                bottomSheetDialog.setContentView(dialogView);

                RecyclerView rcvCartItems = dialogView.findViewById(R.id.rcvCartItems);
                rcvCartItems.setLayoutManager(new LinearLayoutManager(getContext()));
                CartAdapter cartAdapter = new CartAdapter(myCart);
                rcvCartItems.setAdapter(cartAdapter);

                TextView tvTotalPrice = dialogView.findViewById(R.id.tvTotalPrice);
                String formattedTotal = String.format("%,d", totalCartPrice).replace(",", ".") + "đ";
                tvTotalPrice.setText(formattedTotal);

                ImageView btnCloseCart = dialogView.findViewById(R.id.btnCloseCart);
                btnCloseCart.setOnClickListener(view1 -> bottomSheetDialog.dismiss());

                // XỬ LÝ NÚT XÓA TẤT CẢ
                TextView btnClearCart = dialogView.findViewById(R.id.btnClearCart);
                btnClearCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Đưa số lượng tất cả các món đang hiển thị về 0 triệt để
                        for (product p : productList) {
                            p.setQuantity(0);
                        }
                        adapter.notifyDataSetChanged();

                        myCart.clear();
                        totalCartPrice = 0;
                        btnPay.setText("XEM GIỎ HÀNG");
                        bottomSheetDialog.dismiss();
                        Toast.makeText(getContext(), "Đã làm trống giỏ hàng!", Toast.LENGTH_SHORT).show();
                    }
                });

                // XỬ LÝ NÚT ĐẶT HÀNG
                Button btnCheckout = dialogView.findViewById(R.id.btnCheckout);
                btnCheckout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Orders");
                        String orderId = ordersRef.push().getKey();

                        java.util.HashMap<String, Object> orderMap = new java.util.HashMap<>();
                        orderMap.put("orderId", orderId);
                        orderMap.put("totalPrice", totalCartPrice);
                        orderMap.put("status", "pending");
                        orderMap.put("items", myCart);

                        ordersRef.child(orderId).setValue(orderMap).addOnSuccessListener(aVoid -> {
                            for (product p : productList) {
                                p.setQuantity(0);
                            }
                            adapter.notifyDataSetChanged();
                            myCart.clear();
                            totalCartPrice = 0;
                            btnPay.setText("XEM GIỎ HÀNG");
                            bottomSheetDialog.dismiss();

                            Toast.makeText(getContext(), "Đã gửi đơn hàng cho Quản lý!", Toast.LENGTH_SHORT).show();
                        });
                    }
                });
                bottomSheetDialog.show();
            }
        });

        return view;
    }

    // HÀM TẢI DỮ LIỆU VÀ ĐỒNG BỘ MƯỢT MÀ
    private void loadProductsFromFirebase(String categoryTarget) {
        databaseReference.orderByChild("category").equalTo(categoryTarget)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productList.clear();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            product products = dataSnapshot.getValue(product.class);
                            if (products != null) {
                                productList.add(products);
                            }
                        }

                        // 💥 ĐOẠN ĐỒNG BỘ: Đối chiếu danh sách mới tải với Giỏ hàng tổng
                        for (product p : productList) {
                            p.setQuantity(0); // Mặc định đưa số lượng về 0
                            for (product cartItem : myCart) {
                                if (p.getName().equals(cartItem.getName())) {
                                    // Nếu món ăn này đang nằm trong giỏ, gán lại số lượng tương ứng
                                    p.setQuantity(cartItem.getQuantity());
                                    break;
                                }
                            }
                        }

                        // Cập nhật giao diện sau khi đã đồng bộ số lượng xong
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void upMenu(){
        checkAndAddProduct(new product("Mì Tôm Trứng Xúc Xích", "35.000VNĐ", R.drawable.ic_launcher_background, "food"));
        checkAndAddProduct(new product("Cơm Rang Dưa Bò", "45.000VNĐ", R.drawable.ic_launcher_background, "food"));
        checkAndAddProduct(new product("Bánh Mì Pate", "20.000VNĐ", R.drawable.ic_launcher_background, "food"));

        checkAndAddProduct(new product("Sting Dâu Đỏ", "15.000VNĐ", R.drawable.ic_launcher_background, "drink"));
        checkAndAddProduct(new product("Monster Energy", "35.000VNĐ", R.drawable.ic_launcher_background, "drink"));
        checkAndAddProduct(new product("Nước Suối Aquafina", "10.000VNĐ", R.drawable.ic_launcher_background, "drink"));

        checkAndAddProduct(new product("1 giờ", "8.000VNĐ", R.drawable.ic_launcher_background, "money"));
        checkAndAddProduct(new product("2 giờ", "12.000VNĐ", R.drawable.ic_launcher_background, "money"));
        checkAndAddProduct(new product("5 giờ", "30.000VNĐ", R.drawable.ic_launcher_background, "money"));

        checkAndAddProduct(new product("Bánh mì + Sting Dâu Đỏ", "35.000VNĐ", R.drawable.ic_launcher_background, "combo"));
        checkAndAddProduct(new product("Combo sáng/tối (8 giờ)", "50.000VNĐ", R.drawable.ic_launcher_background, "combo"));
    }

    private void checkAndAddProduct(product newProduct) {
        databaseReference.orderByChild("name").equalTo(newProduct.getName())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            databaseReference.push().setValue(newProduct);
                        }
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}