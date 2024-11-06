package com.example.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerStnAdapter extends RecyclerView.Adapter<RecyclerStnAdapter.ViewHolder> {

    private ArrayList<StnItem> items = new ArrayList<>();
    private static OnItemClickListener sListener = null;

    // item 클릭이벤트
    public interface OnItemClickListener{
        void onItemClick(View v, int pos);

    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.sListener = listener;
    }
    //

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView stnNoView;
        TextView stnNmView;
        LinearLayout goBusArrival;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            stnNoView = itemView.findViewById(R.id.stnNo);
            stnNmView = itemView.findViewById(R.id.stnNm);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        if(sListener != null){
                            sListener.onItemClick(view, pos);
                        }
                    }
                }
            });

        }

        public void onBind(StnItem stnitem){
            stnNoView.setText(stnitem.getNodeno());
            stnNmView.setText(stnitem.getNodenm());
        }
    }

    @NonNull
    @Override
    public RecyclerStnAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bus_stn_item,parent,false);
        RecyclerStnAdapter.ViewHolder viewHolder = new RecyclerStnAdapter.ViewHolder(view);


        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBind(items.get(position));


    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public void setstnList(ArrayList<StnItem> list){
        this.items = list;
    }

}