package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SMSReceiver extends BroadcastReceiver {
    private static final String TAG = "SMSReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: ");
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);

                for (SmsMessage message : messages) {
                    String messageBody = message.getMessageBody();
                    String sender = message.getOriginatingAddress();
                    String transactionText = messageBody.replaceAll("\\r?\\n", " ");
                    Log.d(TAG, transactionText);
                    // Process the message body to extract the expense details
//                    Log.d(TAG, "onReceive: " + messageBody);
                    try {
                        processExpenseDetails(context, transactionText);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private void processExpenseDetails(Context context, String messageBody) throws ParseException {
        // Extract the amount and date from the message body
        Log.d(TAG, "processExpenseDetails: inside");
        String bank = "SBI";

        double amount = extractAmount(messageBody , bank);
        Date date = extractDate(messageBody , bank);

        Log.d(TAG, "processExpenseDetails: " + amount + date);

        if (amount > 0 && date != null) {
            // Create a new Expense object and save it to the database
            PendingExpenseClass pendingExpense = new PendingExpenseClass(amount, date);
            saveExpenseToDatabase(context, pendingExpense);

            // Display a notification to the user
            showExpenseNotification(context, pendingExpense);
        }
    }

    private double extractAmount(String messageBody ,  String bank) {
        // Implement your logic to extract the amount from the message body
        // You can use regular expressions or string manipulation
        double amount = 0;
        Pattern pattern = Pattern.compile("\\b(\\d+\\.\\d+)\\b");
        Matcher matcher = pattern.matcher(messageBody);
        if (matcher.find()) {
            amount = Double.parseDouble(Objects.requireNonNull(matcher.group(1)));
        }
        return amount;
    }

    private Date extractDate(String transactionText ,  String bank) {
        // Implement your logic to extract the date from the message body
        // You can use regular expressions or string manipulation



        Log.d(TAG, "extractDate: inside");
        Pattern pattern1 = Pattern.compile("\\b(\\d{2}\\w{3}\\d{2})\\b"); // Pattern for 23Apr24 format
        Pattern pattern2 = Pattern.compile("\\b(\\d{2}-\\w{3}-\\d{2})\\b"); // Pattern for 21-Apr-24 format
        Pattern pattern3 = Pattern.compile("\\b(\\d{2}-\\d{2}-\\d{4})\\b");
        Pattern pattern4 = Pattern.compile("\\b(\\d{2}-\\d{2}-\\d{2})\\b");

        SimpleDateFormat sdf3 = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat sdf1 = new SimpleDateFormat("ddMMMyy");
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MMM-yy");
        SimpleDateFormat sdf4 = new SimpleDateFormat("dd-MM-yy");

        Matcher matcher1 = pattern1.matcher(transactionText);
        Matcher matcher2 = pattern2.matcher(transactionText);
        Matcher matcher3 = pattern3.matcher(transactionText);
        Matcher matcher4 = pattern4.matcher(transactionText);


        while (matcher1.find()) {
            String dateStr = matcher1.group(1);
            try {
                Date date = sdf1.parse(dateStr);
                return date;
            } catch (ParseException e) {
                Log.d(TAG , "Error parsing date: 1" + e.getMessage());
            }
        }

        while (matcher2.find()) {
            String dateStr = matcher2.group(1);
            try {
                Date date = sdf2.parse(dateStr);
                return date;
            } catch (ParseException e) {
                Log.d(TAG , "Error parsing date: 2" + e.getMessage());
            }
        }

        while (matcher3.find()) {
            String dateStr = matcher3.group(1);
            try {
                Date date = sdf3.parse(dateStr);
                return date;
            } catch (ParseException e) {
                Log.d(TAG , "Error parsing date: 3" + e.getMessage());
            }
        }

        while (matcher4.find()) {
            String dateStr = matcher4.group(1);
            try {
                Date date = sdf4.parse(dateStr);
                return date;
            } catch (ParseException e) {
                Log.d(TAG , "Error parsing date: 4" + e.getMessage());
            }
        }
        return null;
    }

    private void saveExpenseToDatabase(Context context, PendingExpenseClass pendingExpense) {
        // Implement your logic to save the expense to the database
        Log.d(TAG, "saveExpenseToDatabase: ");
        ExpenseDbHelper dbHelper = new ExpenseDbHelper(context);
        dbHelper.addPendingExpense(pendingExpense);
        List<PendingExpenseClass> temp = dbHelper.getAllPendingExpenses();
        for (int i = 0; i < temp.size(); i++) {
            Log.d(TAG, temp.get(i).toString());
        }
        dbHelper.close();
    }

    private void showExpenseNotification(Context context, PendingExpenseClass pendingExpense) {
        NotificationHelper.sendNotification(context, "Pending Expense", "");
    }

}