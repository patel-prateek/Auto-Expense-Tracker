package com.example.myapplication;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


public class MonthlyExpensesDetails extends AppCompatActivity {
    String TAG = "MonthlyExpensesDetailsTAG";
    ExpenseDbHelper dbHelper;
    static ExpensesAdapter expensesAdapter;
    static public Button del;
    static public List<UUID> checkedData = new ArrayList<>();
    List<Expense> expenses;
    int desiredMonth;
    int yearName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monthly_expenses_detail);
        Log.d(TAG, "onCreate: inside monthly expense");
        // Get the month name and expenses from the intent extras
        String monthName = getIntent().getStringExtra("MONTH_NAME");
        yearName = getIntent().getIntExtra("YEAR_NAME",1);

        dbHelper = new ExpenseDbHelper(this);
        // Prompt the user to enter the desired month
        assert monthName != null;
        desiredMonth = getMonthNumber(monthName);
        expenses = dbHelper.getExpensesByMonth(desiredMonth , yearName);
        double need = 0;
        double want = 0;
        for (int i = 0; i < expenses.size(); i++) {
            Log.d(TAG, expenses.get(i).getCategory().toLowerCase());
            if(Objects.equals(expenses.get(i).getCategory().toLowerCase(), "need")){
                need+=expenses.get(i).getAmount();
            }else{
                want+=expenses.get(i).getAmount();
            }
        }
        Log.d(TAG, "onCreate: " + yearName);


        // Find the views in the layout
        TextView monthNameTextView = findViewById(R.id.month_name_text_view);
        RecyclerView expensesRecyclerView = findViewById(R.id.expenses_recycler_view);
        del = findViewById(R.id.delete_button);
        del.setOnClickListener(v -> showWarningDialog());
        TextView wantAmount = findViewById(R.id.want_amount_month_detail);
        TextView needAmount = findViewById(R.id.need_amount_month_detail);
        wantAmount.setText(String.valueOf(want));
        needAmount.setText(String.valueOf(need));


        // Set the month name in the TextView
        monthNameTextView.setText(monthName);

        // Sort the expenses in descending order of dates
        assert expenses != null;
        expenses.sort(Comparator.comparing(Expense::getDate).reversed());

        // Set up the RecyclerView
        expensesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        expensesAdapter = new ExpensesAdapter(expenses);
        expensesRecyclerView.setAdapter(expensesAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        expensesAdapter.isSelectionModeEnabled = false;
        checkedData.clear();
    }

    private void showWarningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning");
        builder.setMessage("Are you sure you want to delete selected Expenses?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            // Perform delete operation
            deleteEverything();
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            // Do nothing
            dialog.dismiss();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteEverything() {
        // Your code to delete everything goes here
        // For example, you could clear a database, delete files, etc.
        Log.d("new", String.valueOf(checkedData.get(0)));
        ExpenseDbHelper dbHelper = new ExpenseDbHelper(this);
        dbHelper.deleteExpense(checkedData);
        expensesAdapter.isSelectionModeEnabled = false;
        checkedData.clear();
        expensesAdapter.expenses = dbHelper.getExpensesByMonth(desiredMonth , yearName);
        dbHelper.close();
        expensesAdapter.notifyDataSetChanged();
    }

    public static int getMonthNumber(String monthName) {
        switch (monthName) {
            case "January":
                return 1;
            case "February":
                return 2;
            case "March":
                return 3;
            case "April":
                return 4;
            case "May":
                return 5;
            case "June":
                return 6;
            case "July":
                return 7;
            case "August":
                return 8;
            case "September":
                return 9;
            case "October":
                return 10;
            case "November":
                return 11;
            case "December":
                return 12;
            default:
                return 0;
        }
    }


}
