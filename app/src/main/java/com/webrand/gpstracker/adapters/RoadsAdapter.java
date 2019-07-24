package com.webrand.gpstracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.webrand.gpstracker.R;
import com.webrand.gpstracker.models.TrackerInfo;

import java.util.ArrayList;

public class RoadsAdapter extends RecyclerView.Adapter<RoadsAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<TrackerInfo> trackerinfoArrayList;


    public RoadsAdapter(Context ctx, ArrayList<TrackerInfo> trackerinfoArrayList){
        inflater = LayoutInflater.from(ctx);
        this.trackerinfoArrayList = trackerinfoArrayList;
    }

    @NonNull
    @Override
    public RoadsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoadsAdapter.MyViewHolder holder, int position) {
        holder.txtTime.setText(trackerinfoArrayList.get(position).getTime());
        holder.txtDistance.setText(trackerinfoArrayList.get(position).getDistance());
        holder.txtDate.setText(trackerinfoArrayList.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return trackerinfoArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView txtTime,txtDate,txtDistance;

        MyViewHolder(View itemView) {
            super(itemView);
            txtTime = itemView.findViewById(R.id.txt_time);
            txtDistance = itemView.findViewById(R.id.txt_distance);
            txtDate = itemView.findViewById(R.id.txt_date);
        }
    }
}