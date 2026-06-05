package com.example.cyber_game;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<product> productList;
    private OnCartChangeListener listener;

    public ProductAdapter(List<product> productList) {
        this.productList = productList;
    }

    // 💥 1. SỬA LẠI BỘ ĐÀM: Chỉ nhận đúng 1 món ăn vừa bị thay đổi
    public interface OnCartChangeListener {
        void onCartUpdated(product updatedProduct);
    }

    public ProductAdapter(List<product> productList, OnCartChangeListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customlistview, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        product currentProduct = productList.get(position);

        holder.tvName.setText(currentProduct.getName());
        holder.tvPrice.setText(currentProduct.getPrice());
        holder.imgProduct.setImageResource(R.drawable.ic_launcher_background);

        // 1. Hiển thị số lượng hiện tại của món ăn
        holder.tvQuantity.setText(String.valueOf(currentProduct.getQuantity()));

        // 2. Bấm nút Mũi tên Phải (Cộng 1)
        holder.btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newQuantity = currentProduct.getQuantity() + 1;
                currentProduct.setQuantity(newQuantity);
                holder.tvQuantity.setText(String.valueOf(newQuantity));

                // 💥 2. BÁO CÁO: Gửi đúng cái món khách vừa tăng số lượng
                if (listener != null) {
                    listener.onCartUpdated(currentProduct);
                }
            }
        });

        // 3. Bấm nút Mũi tên Trái (Trừ 1, giới hạn không cho rớt xuống âm)
        holder.btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQty = currentProduct.getQuantity();
                if (currentQty > 0) {
                    int newQuantity = currentQty - 1;
                    currentProduct.setQuantity(newQuantity);
                    holder.tvQuantity.setText(String.valueOf(newQuantity));

                    // 💥 3. BÁO CÁO: Gửi đúng cái món khách vừa giảm số lượng
                    if (listener != null) {
                        listener.onCartUpdated(currentProduct);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvName, tvPrice;
        ImageView btnLeft, btnRight;
        TextView tvQuantity;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);

            btnLeft = itemView.findViewById(R.id.btnLeft);
            btnRight = itemView.findViewById(R.id.btnRight);
            tvQuantity = itemView.findViewById(R.id.edtSL);
        }
    }
}