package com.example.knockknock.view;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.knockknock.R;

import java.util.ArrayList;

public class RecyclerBusAdapter extends RecyclerView.Adapter<RecyclerBusAdapter.ViewHolder> {

    private ArrayList<BusArrivalItem> items = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView arrprecntView;
        TextView arrtimeView;
        TextView routenoView;
        TextView vehicletpView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            arrprecntView = itemView.findViewById(R.id.prevStnCnt);
            arrtimeView = itemView.findViewById(R.id.arrTime);
            routenoView = itemView.findViewById(R.id.routeNo);
            vehicletpView = itemView.findViewById(R.id.vehicleTp);
        }

        public void onBind(BusArrivalItem busArrivalItem) {
            arrprecntView.setText(busArrivalItem.getArrprevstationcnt()+"정류장 전");
            arrtimeView.setText(busArrivalItem.getArrtime());
            routenoView.setText(busArrivalItem.getRouteno());
            vehicletpView.setText(busArrivalItem.getVehicletp());

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bus_arrival_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);

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

    public void setbusArrivalList(ArrayList<BusArrivalItem> list){
        this.items = list;

    }


}