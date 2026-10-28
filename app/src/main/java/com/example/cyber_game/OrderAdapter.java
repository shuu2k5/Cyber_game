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
import com.google.firebase.database.ServerValue;

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
        holder.btnComplete.setEnabled(true);
        Order currentOrder = orderList.get(position);

        // Hiển thị mã đơn (cắt bớt cho đỡ dài) và số tiền
        holder.tvOrderId.setText("Mã: " + currentOrder.getOrderId().substring(0, 8) + "...");
        String formattedPrice = String.format("%,d", currentOrder.getTotalPrice()).replace(",", ".") + "đ";
        holder.tvPrice.setText(formattedPrice);

        // XỬ LÝ KHI BẤM NÚT HOÀN THÀNH
        holder.btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.btnComplete.setEnabled(false);   // chống admin bấm 2 lần, cộng giờ 2 lần
                DatabaseReference db = FirebaseDatabase.getInstance().getReference();

// 1. Đánh dấu đơn đã hoàn thành
                db.child("Orders").child(currentOrder.getOrderId()).child("status").setValue("completed");

// 2. Cộng doanh thu
                db.child("System").child("Revenue").setValue(ServerValue.increment(currentOrder.getTotalPrice()));

// 3. Nếu đơn có gói giờ chơi thì cộng phút cho người đặt
                if (currentOrder.getUid() != null && currentOrder.getTopupMinutes() > 0) {
                    db.child("users").child(currentOrder.getUid())
                            .child("remainingMinutes").setValue(ServerValue.increment(currentOrder.getTopupMinutes()));
                }

                Toast.makeText(v.getContext(), "Đã duyệt đơn!", Toast.LENGTH_SHORT).show();
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