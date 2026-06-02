package com.example.cyber_game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
public class fragment_service extends Fragment {
    @NonNull
    @Override
    public View onCreateView (@NonNull LayoutInflater inflater,
                              @Nullable ViewGroup container,
                              @Nullable Bundle saveInstanceState){
        View view = inflater.inflate(R.layout.fragment_service,container,false);
        RecyclerView rcvproducts = view.findViewById(R.id.edtproduct);

        // 2. Chia lưới 2 cột cho RecyclerView
        // getContext() là lệnh để lấy bối cảnh của Fragment
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        rcvproducts.setLayoutManager(gridLayoutManager);

        // 3. Tạo danh sách "Nguyên liệu" (Dữ liệu giả để test)
        List<product> productList = new ArrayList<>();
        productList.add(new product("Mì Tôm Trứng Xúc Xích", "35.000đ", R.drawable.ic_launcher_background, "food"));
        productList.add(new product("Sting Dâu Đỏ", "15.000đ", R.drawable.ic_launcher_background, "drink"));
        productList.add(new product("Cơm Rang Dưa Bò", "45.000đ", R.drawable.ic_launcher_background, "food"));
        productList.add(new product("Monster Energy", "35.000đ", R.drawable.ic_launcher_background, "drink"));
        productList.add(new product("Bánh Mì Pate", "20.000đ", R.drawable.ic_launcher_background, "food"));
        productList.add(new product("Nước Suối Aquafina", "10.000đ", R.drawable.ic_launcher_background, "drink"));
        productList.add(new product("Mì Tôm Trứng Xúc Xích", "35.000đ", R.drawable.ic_launcher_background, "food"));
        productList.add(new product("Sting Dâu Đỏ", "15.000đ", R.drawable.ic_launcher_background, "drink"));
        productList.add(new product("Cơm Rang Dưa Bò", "45.000đ", R.drawable.ic_launcher_background, "food"));
        productList.add(new product("Monster Energy", "35.000đ", R.drawable.ic_launcher_background, "drink"));
        productList.add(new product("Bánh Mì Pate", "20.000đ", R.drawable.ic_launcher_background, "food"));
        productList.add(new product("Nước Suối Aquafina", "10.000đ", R.drawable.ic_launcher_background, "drink"));

        // 4. Thuê "Người thợ" (Adapter) và giao nguyên liệu cho họ
        ProductAdapter adapter = new ProductAdapter(productList);

        // 5. Đưa người thợ lên sân khấu làm việc!
        rcvproducts.setAdapter(adapter);
        return view;
    }
}
