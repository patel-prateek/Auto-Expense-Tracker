package com.example.myapplication;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PendingExpenseAdapter extends RecyclerView.Adapter<PendingExpenseAdapter.PendingExpenseViewHolder> {
    private static OnPendingExpenseClickListener listener;

    static String TAG = "PendingExpenseAdapter";
    private List<PendingExpenseClass> pendingExpenseList;
    public PendingExpenseAdapter(List<PendingExpenseClass> pendingExpenseList) {
        this.pendingExpenseList = pendingExpenseList;
//        ExpenseDbHelper dbHelper = new ExpenseDbHelper(this);
//        this.pendingExpenseList = dbHelper.getAllExpenses();
    }

    @NonNull
    @Override
    public PendingExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pending_expense, parent, false);
        return new PendingExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingExpenseViewHolder holder, int position) {
        PendingExpenseClass pendingExpense = pendingExpenseList.get(position);
        holder.bind(pendingExpense);
    }

    @Override
    public int getItemCount() {
        return pendingExpenseList.size();
    }

    public void setOnPendingExpenseClickListener(OnPendingExpenseClickListener listener) {
        this.listener = listener;
    }

    static class PendingExpenseViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAmount;
        private TextView tvDate;
        private MaterialButton btnAdd;
        private MaterialButton btnDelete;

        PendingExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAmount = itemView.findViewById(R.id.pending_expense_amount_text_view);
            tvDate = itemView.findViewById(R.id.pending_expense_date_text_view);
            btnAdd = itemView.findViewById(R.id.pending_enpense_add_buttow);
            btnDelete = itemView.findViewById(R.id.pending_enpense_delete_button);
        }

        void bind(PendingExpenseClass pendingExpense) {
            tvAmount.setText(String.format(Locale.getDefault(), "Amount: $%.2f", pendingExpense.getAmount()));

            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            String formattedDate = dateFormat.format(pendingExpense.getDate());
            tvDate.setText(formattedDate);

            btnAdd.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddExpenseClick(pendingExpense);
                }
//                Log.d(TAG, "bind: ADD clicked for " + pendingExpense.getAmount());
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteExpenseClick(pendingExpense);
                }
//                Log.d(TAG, "bind: delete clicked for " + pendingExpense.getAmount());
            });
        }
    }
}

