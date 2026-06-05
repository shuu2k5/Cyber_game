package com.example.cyber_game;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<product> cartList;

    public CartAdapter(List<product> cartList) {
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        product p = cartList.get(position);

        holder.tvName.setText(p.getName());
        holder.tvQuantity.setText("x" + p.getQuantity());

        // Tính thành tiền cho từng món (Giá x Số lượng)
        long price = Long.parseLong(p.getPrice().replaceAll("[^0-9]", ""));
        long itemTotal = price * p.getQuantity();

        String formattedItemTotal = String.format("%,d", itemTotal).replace(",", ".") + "đ";
        holder.tvPrice.setText(formattedItemTotal);
    }

    @Override
    public int getItemCount() {
        return cartList != null ? cartList.size() : 0;
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQuantity, tvPrice;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCartItemName);
            tvQuantity = itemView.findViewById(R.id.tvCartItemQuantity);
            tvPrice = itemView.findViewById(R.id.tvCartItemPrice);
        }
    }
}