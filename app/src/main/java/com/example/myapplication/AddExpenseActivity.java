package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class AddExpenseActivity extends AppCompatActivity {
    private static final String TAG = "AddExpenseActivityTag";
//    PendingExpenseAdapter adapter = new PendingExpenseAdapter(pendingExpenseList, this);

    private EditText expenseAmountEditText;
    private TextInputLayout expenseCategoryTextInputLayout;
    private AutoCompleteTextView expenseCategoryAutoComplete;
    private EditText expenseDescriptionEditText;
    private DatePicker expenseDatePicker;
    UUID id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        id =(UUID) getIntent().getSerializableExtra("id");
        Date date = (Date) getIntent().getSerializableExtra("date");
        Double amount = (Double) getIntent().getSerializableExtra("amount");

        // Get a reference to the TextInputLayout and AutoCompleteTextView
        expenseCategoryTextInputLayout = findViewById(R.id.expense_category_text_input_layout);
        expenseCategoryAutoComplete = findViewById(R.id.expense_category_auto_complete);
        // Create an array of expense categories
        String[] expenseCategories = {"NEED" , "WANT"};
        // Create an ArrayAdapter to populate the AutoCompleteTextView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, expenseCategories);
        expenseCategoryAutoComplete.setAdapter(adapter);


        expenseAmountEditText = findViewById(R.id.edit_text_expense_amount);
        expenseDescriptionEditText = findViewById(R.id.edit_text_expense_description);
        expenseDatePicker = findViewById(R.id.date_picker_expense_date);

        if(date!=null){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            expenseDatePicker.updateDate(year, month, day);
        }

        if(amount!=null){
            expenseAmountEditText.setText(String.format(String.valueOf(amount)));
        }

        Button saveExpenseButton = findViewById(R.id.button_save_expense);
        saveExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(expenseCategoryAutoComplete.getText().toString().trim()) || TextUtils.isEmpty(expenseDescriptionEditText.getText().toString().trim()) || TextUtils.isEmpty(expenseAmountEditText.getText().toString().trim()) ){
                    Toast.makeText(AddExpenseActivity.this, "Empty Fields Not Allowed", Toast.LENGTH_SHORT).show();
                }else{
                    saveExpense();
                }
//                Log.d(TAG, expenseAmountEditText.getText().toString() + " " + expenseCategoryAutoComplete.getText().toString() + " " + expenseDescriptionEditText.getText().toString() );

            }
        });
    }

    private void saveExpense() {
        double amount = Double.parseDouble(expenseAmountEditText.getText().toString());
        String category = expenseCategoryAutoComplete.getText().toString();
        String description = expenseDescriptionEditText.getText().toString();
        int year = expenseDatePicker.getYear();
        int month = expenseDatePicker.getMonth();
        int day = expenseDatePicker.getDayOfMonth();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        Date date = calendar.getTime();//Thu Apr 18 19:28:28 GMT+05:30 2024



        Expense newExpense = new Expense(amount, category, description, date);
//        Log.d(TAG, "saveExpense: " + date);


        ExpenseDbHelper dbHelper = new ExpenseDbHelper(this);

        try {
            dbHelper.addExpense(newExpense);
            if(id!=null){
                dbHelper.deletePendingExpense(id);
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_OK, resultIntent);
            }
            Toast.makeText(this, "Expense added successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("AddExpenseActivity", "Error saving expense: " + e.getMessage());
            Toast.makeText(this, "Failed to add expense. Please try again.", Toast.LENGTH_SHORT).show();
        } finally {
            dbHelper.close();
        }
        finish();
    }
}