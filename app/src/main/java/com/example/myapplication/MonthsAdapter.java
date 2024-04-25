package com.example.myapplication;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MonthsAdapter extends RecyclerView.Adapter<MonthsAdapter.ViewHolder> {

    private List<String> monthNames;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private static OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    MonthsAdapter(List<String> monthNames) {
        this.monthNames = monthNames;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_month, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(monthNames.get(position));
    }

    @Override
    public int getItemCount() {
        return monthNames.size();
    }



    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.main_activity_month_name_textview);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d("new", "onClick: viewholder");
            int position = getAdapterPosition();
            if (listener != null && position != RecyclerView.NO_POSITION) {
                Log.d("new", "onClick: inside if");
                listener.onItemClick(position);
            }
        }
    }

}