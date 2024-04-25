package com.example.myapplication;

public interface OnPendingExpenseClickListener {
    void onAddExpenseClick(PendingExpenseClass pendingExpense);

    void onDeleteExpenseClick(PendingExpenseClass pendingExpense);
}
