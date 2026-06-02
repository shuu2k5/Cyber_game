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

    // Nhận danh sách các món ăn truyền vào
    public ProductAdapter(List<product> productList) {
        this.productList = productList;
    }

    // Bước 1: Nặn ra cái khuôn từ file XML
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customlistview, parent, false);
        return new ProductViewHolder(view);
    }

    // Bước 2: Đổ dữ liệu từ danh sách vào khuôn
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        product product = productList.get(position);

        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(product.getPrice());

        // Tạm thời gán 1 ảnh mặc định để test, sau này bạn có thể truyền ảnh thật
        holder.imgProduct.setImageResource(R.drawable.ic_launcher_background);
    }

    // Trả về số lượng món ăn đang có
    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    // Ánh xạ các thành phần bên trong cái khuôn
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvName, tvPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
        }
    }
}