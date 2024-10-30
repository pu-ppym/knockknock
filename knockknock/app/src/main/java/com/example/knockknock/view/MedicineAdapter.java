package com.example.knockknock.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.knockknock.R;
import com.example.knockknock.model.MedicineModel;

import java.util.List;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder>{
    private List<MedicineModel> medicines;

    public MedicineAdapter(List<MedicineModel> medicines) {
        this.medicines = medicines;
    }

    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicine, parent, false);
        return new MedicineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineViewHolder holder, int position) {
        MedicineModel medicine = medicines.get(position);
        holder.medNameTextView.setText(medicine.getMed_name()); // 약 이름을 텍스트뷰에 설정
    }

    @Override
    public int getItemCount() {
        return medicines.size();
    }

    public static class MedicineViewHolder extends RecyclerView.ViewHolder {
        TextView medNameTextView;

        public MedicineViewHolder(@NonNull View itemView) {
            super(itemView);
            medNameTextView = itemView.findViewById(R.id.medNameTextView);
        }
    }
}
