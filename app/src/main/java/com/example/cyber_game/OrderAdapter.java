package com.example.cyber_game;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList;

    public OrderAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_admin, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order currentOrder = orderList.get(position);

        // Hiển thị mã đơn (cắt bớt cho đỡ dài) và số tiền
        holder.tvOrderId.setText("Mã: " + currentOrder.getOrderId().substring(0, 8) + "...");
        String formattedPrice = String.format("%,d", currentOrder.getTotalPrice()).replace(",", ".") + "đ";
        holder.tvPrice.setText(formattedPrice);

        // XỬ LÝ KHI BẤM NÚT HOÀN THÀNH
        holder.btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference db = FirebaseDatabase.getInstance().getReference();

                // 1. Đổi trạng thái đơn thành "completed" (Đã hoàn thành)
                db.child("Orders").child(currentOrder.getOrderId()).child("status").setValue("completed");

                // 2. Lấy Doanh Thu hiện tại, cộng thêm tiền đơn này, rồi lưu lại
                DatabaseReference revRef = db.child("System").child("Revenue");
                revRef.get().addOnSuccessListener(snapshot -> {
                    long currentRevenue = 0;
                    if (snapshot.exists() && snapshot.getValue() != null) {
                        currentRevenue = snapshot.getValue(Long.class);
                    }
                    // Ghi đè doanh thu mới lên mạng
                    revRef.setValue(currentRevenue + currentOrder.getTotalPrice());

                    Toast.makeText(v.getContext(), "Đã chốt đơn và cộng tiền!", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    public int getItemCount() { return orderList.size(); }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvPrice;
        Button btnComplete;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvPrice = itemView.findViewById(R.id.tvOrderPrice);
            btnComplete = itemView.findViewById(R.id.btnCompleteOrder);
        }
    }
}