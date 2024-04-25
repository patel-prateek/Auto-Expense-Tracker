package com.example.myapplication;

import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.text.DateFormat;
import java.util.UUID;


public class ExpenseViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
    String TAG = "ExpenseViewHolder";
    private final TextView expenseNameTextView;
    private UUID id;
    private final TextView expenseAmountTextView;
    private final TextView expenseDateTextView;
    private final TextView expenseCategoryTextView;
    private final CheckBox checkBox;
    public boolean isSelected = false;

    ExpensesAdapter expensesAdapter = MonthlyExpensesDetails.expensesAdapter;

    public ExpenseViewHolder(View itemView) {
        super(itemView);
        expenseNameTextView = itemView.findViewById(R.id.expense_name_text_view);
        expenseAmountTextView = itemView.findViewById(R.id.expense_amount_text_view);
        expenseDateTextView = itemView.findViewById(R.id.expense_date_text_view);
        expenseCategoryTextView = itemView.findViewById(R.id.expense_category_text_view);
        checkBox = itemView.findViewById(R.id.checkbox);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    // Add the data to the checkedData list
                    MonthlyExpensesDetails.checkedData.add(id);
                } else {
                    // Remove the data from the checkedData list
                    MonthlyExpensesDetails.checkedData.remove(id);
                }
            }
        });
        itemView.setOnLongClickListener(this);
        itemView.setOnClickListener(this);
    }

    public void bind(Expense expense) {
        id = expense.getId();
        expenseNameTextView.setText(expense.getDescription());
        expenseAmountTextView.setText(String.format("Rs %.2f", expense.getAmount()));
        expenseDateTextView.setText(DateFormat.getDateInstance().format(expense.getDate()));
        expenseCategoryTextView.setText(expense.getCategory());
        if(expensesAdapter.isSelectionModeEnabled){
            checkBox.setVisibility(View.VISIBLE);
        }else{
            checkBox.setVisibility(View.INVISIBLE);
            checkBox.setChecked(false);
        }
    }


    @Override
    public boolean onLongClick(View view) {
        Log.d(TAG, "onLongClick: ");
        expensesAdapter.isSelectionModeEnabled = true;
        isSelected = true;
        MonthlyExpensesDetails.del.setVisibility(View.VISIBLE);
        expensesAdapter.notifyDataSetChanged();
        return false;
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: ");
        isSelected = !isSelected;

    }
}


