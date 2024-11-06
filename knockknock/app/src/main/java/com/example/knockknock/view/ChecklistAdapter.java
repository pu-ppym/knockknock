package com.example.knockknock.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.knockknock.R;

import java.util.ArrayList;
import java.util.List;

public class ChecklistAdapter extends RecyclerView.Adapter<ChecklistAdapter.ChecklistViewHolder> {
    private final String[] listItems;
    private final int[] icons;
    private final boolean[] checkedStatus;
    private final List<String> checkedItems;

    public ChecklistAdapter(String[] listItems, int[] icons, boolean[] checkedStatus) {
        this.listItems = listItems;
        this.icons = icons;
        this.checkedStatus = checkedStatus;
        this.checkedItems = new ArrayList<>();
    }

    @Override
    public ChecklistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_with_icon, parent, false);
        return new ChecklistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChecklistViewHolder holder, int position) {
        String item = listItems[position];
        holder.textView.setText(item);
        holder.imageView.setImageResource(icons[position]);
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(checkedStatus[position]);


        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            checkedStatus[position] = isChecked;
            if (isChecked) {
                checkedItems.add(item);
            } else {
                checkedItems.remove(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItems.length;
    }

    public List<String> getCheckedItems() {
        return checkedItems;
    }

    public static class ChecklistViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        CheckBox checkBox;
        TextView textView;

        public ChecklistViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_icon);
            checkBox = itemView.findViewById(R.id.item_checkbox);
            textView = itemView.findViewById(R.id.item_text);
        }
    }
}
