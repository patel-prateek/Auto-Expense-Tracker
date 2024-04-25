package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.util.Calendar;
import android.util.Log;

import java.sql.Date;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ExpenseDbHelper extends SQLiteOpenHelper {
    String TAG = "ExpenseDbHelper";
    private static final String DATABASE_NAME = "expenses.db";
    private static final int DATABASE_VERSION = 3;
    private static final String TABLE_NAME = "expenses";
    private static final String PE_TABLE_NAME = "pending_expenses";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_CATEGORY = "category";

    public ExpenseDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " TEXT PRIMARY KEY, " +
                COLUMN_DATE + " INTEGER, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_AMOUNT + " REAL, " +
                COLUMN_CATEGORY + " TEXT)";
        db.execSQL(createTableQuery);

        String pending_createTableQuery = "CREATE TABLE " + PE_TABLE_NAME + " (" +
                COLUMN_ID + " TEXT PRIMARY KEY, " +
                COLUMN_DATE + " INTEGER, " +
                COLUMN_AMOUNT + " REAL) ";
        db.execSQL(pending_createTableQuery);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PE_TABLE_NAME);
        onCreate(db);
    }




    //CREATE
    public void addExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, expense.getId().toString());
        values.put(COLUMN_DATE, expense.getDate().getTime());
        values.put(COLUMN_DESCRIPTION, expense.getDescription());
        values.put(COLUMN_AMOUNT, expense.getAmount());
        values.put(COLUMN_CATEGORY, expense.getCategory());

        long rowId = db.insert(TABLE_NAME, null, values);
        if (rowId == -1) {
            // Error occurred while inserting the expense
            Log.e("ExpenseDbHelper", "Failed to add expense: " + expense);
        } else {
            // Expense was inserted successfully
            Log.d("ExpenseDbHelper", "Expense added successfully: " + expense);

            // Verify that the expense was added by querying the database
            String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";
            Cursor cursor = db.rawQuery(selectQuery, new String[] { expense.getId().toString() });
            if (cursor.moveToFirst()) {
                Expense retrievedExpense = new Expense(
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                        new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DATE)))
                );
                retrievedExpense.setId(UUID.fromString(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID))));

                if (expense.getId().equals(retrievedExpense.getId())) {
                    Log.d("ExpenseDbHelper", "Expense added and retrieved successfully: " + expense);
                } else {
                    Log.e("ExpenseDbHelper", expense.toString());
                    Log.e("ExpenseDbHelper", retrievedExpense.toString());

                }
            } else {
                Log.e("ExpenseDbHelper", "Failed to retrieve the added expense.");
            }
            cursor.close();
        }
        db.close();
    }


    public void addPendingExpense(PendingExpenseClass pendingExpense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, pendingExpense.getId().toString());
        values.put(COLUMN_DATE, pendingExpense.getDate().getTime());
        values.put(COLUMN_AMOUNT, pendingExpense.getAmount());

        long rowId = db.insert(PE_TABLE_NAME, null, values);
        if (rowId == -1) {
            // Error occurred while inserting the expense
            Log.e("ExpenseDbHelper", "Failed to add expense: " + pendingExpense);
        } else {
            // Expense was inserted successfully
            Log.d("ExpenseDbHelper", "Expense added successfully: " + pendingExpense);

            // Verify that the expense was added by querying the database
            String selectQuery = "SELECT * FROM " + PE_TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";
            Cursor cursor = db.rawQuery(selectQuery, new String[] { pendingExpense.getId().toString() });
            if (cursor.moveToFirst()) {
                PendingExpenseClass retrievedExpense = new PendingExpenseClass(
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)),
                        new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DATE)))
                );
                retrievedExpense.setId(UUID.fromString(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID))));

                if (pendingExpense.getId().equals(retrievedExpense.getId())) {
                    Log.d("ExpenseDbHelper", "Pending Expense added and retrieved successfully: " + pendingExpense);
                } else {
                    Log.e("ExpenseDbHelper", pendingExpense.toString());
                    Log.e("ExpenseDbHelper", retrievedExpense.toString());

                }
            } else {
                Log.e("ExpenseDbHelper", "Failed to retrieve the added expense.");
            }
            cursor.close();
        }
        db.close();
    }


    //READ for saved expenses
    public double getNeed() {
        double need=0;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY));
                if(Objects.equals(category, "need")){
                    need+=amount;
//                    Log.d(TAG, "getNeed: " + amount + " " + category);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        return need;
    }

    public double getWant() {
        double want=0;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY));
                if(Objects.equals(category, "want")){
                    want+=amount;
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        return want;
    }




    //READ for PENDING EXPENSES
    public List<PendingExpenseClass> getAllPendingExpenses() {
        List<PendingExpenseClass> expenses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + PE_TABLE_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                UUID id = UUID.fromString(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT));
                long dateTime = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DATE));
                Date date = new Date(dateTime);

                PendingExpenseClass pendingExpense = new PendingExpenseClass( id, amount, date);
                expenses.add(pendingExpense);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return expenses;
    }


    public List<Expense> getExpensesByMonth(int month , int year) {
        List<Expense> expenses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String startDate = year + "-" + String.format("%02d", month) + "-01";

        // Calculate the last day of the desired month
        GregorianCalendar calendar = new GregorianCalendar(year, month - 1, 1);
        int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        String endDate = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", lastDay);

        String query = "SELECT * FROM " + TABLE_NAME +
                " WHERE datetime(" + COLUMN_DATE + "/1000, 'unixepoch') BETWEEN '" + startDate + "' AND '" + endDate + "'";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                UUID id = UUID.fromString(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                long dateTime = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DATE));
                Date date = new Date(dateTime);

                Expense expense = new Expense( id, amount, category, description, date);
                expenses.add(expense);
            } while (cursor.moveToNext());
        }else{
            Log.d(TAG, "getExpensesByMonth: empty");
        }

        cursor.close();
        return expenses;

    }



    //DELETE
    public void deleteExpense(List<UUID> checkedData) {
        SQLiteDatabase db = this.getWritableDatabase();

        for (int i = 0; i < checkedData.size(); i++) {
            UUID uuid = checkedData.get(i);
            db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[] { uuid.toString() });
        }
        db.close();
    }

    public void deletePendingExpense(UUID uuid) {
        SQLiteDatabase db = this.getWritableDatabase();
//        UUID uuid = pendingExpense.getId();
        db.delete(PE_TABLE_NAME, COLUMN_ID + " = ?", new String[] { uuid.toString() });
        db.close();
    }


    public void printAllExpenses() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY));

                // Log or print the data
                Log.d("SQLiteData", "ID: " + id + ", Date: " + date + ", Description: " + description + ", Amount: " + amount + ", Category: " + category);
//                ID: 8, Date: 1702664048287, Description: shhshs, Amount: 49964.0, Category: WANT
            } while (cursor.moveToNext());
        }

        cursor.close();
    }



}