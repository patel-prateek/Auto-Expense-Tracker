package com.example.myapplication;

import androidx.annotation.Nullable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NotificationActivity extends AppCompatActivity implements OnPendingExpenseClickListener {
    String TAG = "ActivityNotification";
    private static final int ADD_EXPENSE_REQUEST_CODE = 1;
    private RecyclerView pendingExpensesRecyclerView;
    private PendingExpenseAdapter pendingExpensesAdapter;
    private List<PendingExpenseClass> pendingExpenses;

    private PendingExpenseClass pendingExpense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        pendingExpensesRecyclerView = findViewById(R.id.recycler_view_pending_expenses);
        pendingExpensesRecyclerView.setLayoutManager(new LinearLayoutManager(this));



        // Fetch the pending expenses from the database
        ExpenseDbHelper dbHelper = new ExpenseDbHelper(this);

        //TEST DATA FOR DATABASECHECKING
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//        try {
//            Date date1 = dateFormat.parse("05/04/2024");
//            Date date2 = dateFormat.parse("12/04/2024");
//            Date date3 = dateFormat.parse("22/04/2024");//Thu Apr 18 19:28:28 GMT+05:30 2024
//            dbHelper.addPendingExpense(new PendingExpenseClass(50.0, date1));
//            dbHelper.addPendingExpense(new PendingExpenseClass(30.0, date1));
//            dbHelper.addPendingExpense(new PendingExpenseClass(25.0, date2));
//            dbHelper.addPendingExpense(new PendingExpenseClass(40.0, date3));
//        } catch (ParseException e) {
//            Log.e(TAG, "onCreate: ",e );
//        }
        pendingExpenses = dbHelper.getAllPendingExpenses();
        dbHelper.close();

        // Set up the adapter for the pending expenses RecyclerView
        pendingExpensesAdapter = new PendingExpenseAdapter(pendingExpenses);
        pendingExpensesAdapter.setOnPendingExpenseClickListener(this);
        pendingExpensesRecyclerView.setAdapter(pendingExpensesAdapter);
    }

    @Override
    public void onAddExpenseClick(PendingExpenseClass pendingExpense) {
        Log.d("PendingExpenseAdapter", "onAddExpenseClick: inside");

        Intent intent = new Intent(this, AddExpenseActivity.class);
        this.pendingExpense = pendingExpense;
        intent.putExtra("id" , pendingExpense.getId());
        intent.putExtra("date", pendingExpense.getDate());
        intent.putExtra("amount", pendingExpense.getAmount());
        startActivityForResult(intent, ADD_EXPENSE_REQUEST_CODE);
    }

    @Override
    public void onDeleteExpenseClick(PendingExpenseClass pendingExpense) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning");
        builder.setMessage("Are you sure you want to delete selected Expenses?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            // Perform delete operation
            ExpenseDbHelper dbHelper = new ExpenseDbHelper(this);
            dbHelper.deletePendingExpense(pendingExpense.getId());
            dbHelper.close();
            pendingExpenses.remove(pendingExpense);
            pendingExpensesAdapter.notifyDataSetChanged();
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            // Do nothing
            dialog.dismiss();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_EXPENSE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "onActivityResult: ");
            pendingExpenses.remove(pendingExpense);
            pendingExpensesAdapter.notifyDataSetChanged();
        }
    }
}

