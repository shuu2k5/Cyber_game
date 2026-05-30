package com.example.cyber_game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;

public class fragment_person extends Fragment {
    @NonNull
    @Override
    public View onCreateView (@NonNull LayoutInflater inflater,
                              @Nullable ViewGroup container,
                              @Nullable Bundle saveInstanceState){
        View view = inflater.inflate(R.layout.fragment_person,container,false);

        LinearLayout rowin4 = view.findViewById(R.id.rowGeneralInfo);
        rowin4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BottomSheetDialog dialog = new BottomSheetDialog(requireContext());

                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.layout_bottom_sheet_info,null);

                dialog.setContentView(dialogView);
                ImageView btnClose = dialogView.findViewById(R.id.btnClose);
                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        return view;
    }
}
