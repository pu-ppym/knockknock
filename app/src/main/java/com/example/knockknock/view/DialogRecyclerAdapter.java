package com.example.knockknock.view;

import static android.os.Build.VERSION_CODES.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.knockknock.R;

import java.util.ArrayList;

public class DialogRecyclerAdapter extends RecyclerView.Adapter<DialogRecyclerAdapter.ViewHolder> {
    private ArrayList<String> bData = null;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ViewHolder(View itemView) {
            super(itemView);
//            textView = itemView.findViewById(R.textViewRecyclerItem); // 각 item View에 대해
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String topic = bData.get(getAdapterPosition());

                }
            });
        }
    }
    public DialogRecyclerAdapter(ArrayList<String> list) {
       bData = list; // 입력받은 list를 저장
    }

    @Override
    public DialogRecyclerAdapter.ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        Context context = parent.getContext(); // parent로부터 content 받음
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = inflater.inflate(R.layout.recyclerview_topic_item, parent, false);
        // 각 item의 View는 이전에 정의했던 item layout을 불러옴
//        DialogRecyclerAdapter.ViewHolder vh = new DialogRecyclerAdapter.ViewHolder(view);
//        return vh; // ViewHolder 반환
        return null;
    }

    @Override
    public void onBindViewHolder(DialogRecyclerAdapter.ViewHolder holder, int position) {
        String text = bData.get(position); // 어떤 포지션의 텍스트인지 조회
        holder.textView.setText(text); // 해당 포지션의 View item에 텍스트 입힘
    }

    @Override
    public int getItemCount() {
        return bData.size();
    }
}
