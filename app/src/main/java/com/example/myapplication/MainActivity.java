package com.example.myapplication;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks , MonthsAdapter.OnItemClickListener {

    private static final String TAG = "MainActivitytag";
    List<String> monthNames;
    Spinner yearSpinner;
    private static final int REQUEST_CODE = 123;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{
            android.Manifest.permission.POST_NOTIFICATIONS,
            android.Manifest.permission.RECEIVE_SMS,
            android.Manifest.permission.READ_SMS,
            android.Manifest.permission.VIBRATE
    };
    private RecyclerView recyclerView;
    private MonthsAdapter monthsAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        requestPermissions();


        //FILLING TOTAL NEED AND WANT
        TextView want_amount_text_view = findViewById(R.id.want_amount_text_view);
        TextView need_amount_text_view = findViewById(R.id.need_amount_text_view);
        ExpenseDbHelper dbHelper = new ExpenseDbHelper(this);
        double need = dbHelper.getNeed();
        double want = dbHelper.getWant();
        need_amount_text_view.setText(String.format("Rs %.2f", need));
        want_amount_text_view.setText(String.format("Rs %.2f", want));
        dbHelper.close();





        //ADD NEW EXPENSE
        Button addExpenseButton = findViewById(R.id.button_add_expense);
        addExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: button vlicked");
                Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
                intent.putExtra("date", (String) null);
                intent.putExtra("id", (UUID) null);
                intent.putExtra("amount", (String) null);
                startActivity(intent);
            }
        });


        //NAVIGATE TO NOTIFICATION ACTIVITY
        Button fabOpenNotifications = findViewById(R.id.fab_open_notifications);
        fabOpenNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NotificationActivity.class));
            }
        });


        monthNames = new ArrayList<>(Arrays.asList(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        ));



        // Set up the RecyclerView for months
        recyclerView = findViewById(R.id.month_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        monthsAdapter = new MonthsAdapter(monthNames);
        monthsAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(monthsAdapter);




        //YEAR setting and selection
        yearSpinner = findViewById(R.id.year_spinner);
        ArrayList<String> yearList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.year_array)));
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        if (!yearList.contains(String.valueOf(currentYear))) {
            yearList.add(String.valueOf(currentYear));
        }
        yearList.sort(Collections.reverseOrder());
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, yearList);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);


        //TEST DATA FOR DATABASECHECKING
//        ExpenseDbHelper dbHelper = new ExpenseDbHelper(this);
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//        try {
//            Date date1 = dateFormat.parse("05/04/2024");
//            Date date2 = dateFormat.parse("12/04/2024");
//            Date date3 = dateFormat.parse("22/04/2024");//Thu Apr 18 19:28:28 GMT+05:30 2024
//            dbHelper.addExpense(new Expense(50.0, "need", "Lunch", date1));
//            dbHelper.addExpense(new Expense(30.0, "need", "Bus fare", date1));
//            dbHelper.addExpense(new Expense(25.0, "want", "Movie tickets", date2));
//            dbHelper.addExpense(new Expense(40.0, "need", "Dinner", date3));
//        } catch (ParseException e) {
//            Log.e(TAG, "onCreate: ",e );
//        }finally {
//            dbHelper.close();
//        }

    }




    private void requestPermissions() {
        Log.d(TAG, "requestPermissions: into it");
        if (!EasyPermissions.hasPermissions(this, REQUIRED_PERMISSIONS)) {
            Log.d(TAG, "requestPermissions: into if");
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs access to your notifications, SMS, and vibrate permissions.",
                    REQUEST_CODE,
                    REQUIRED_PERMISSIONS
            );
        } else {
            Log.d(TAG, "requestPermissions: into else");
            // Permissions already granted, proceed with the app's functionality
//            initializeApp(1);
        }
    }


    @Override
    public void onPermissionsGranted(int i, @NonNull List<String> list) {
        Log.d(TAG, "requestPermissions: into per granter");
//        initializeApp(1);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "requestPermissions: into per denied");
        showPermissionDeniedMessage();
    }

    private void showPermissionDeniedMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissions Required");
        builder.setMessage("Some features of the app won't work until you grant all the required permissions. Please grant the necessary permissions to enjoy the full functionality of the app.");

        // Add a positive button to request permissions again
        builder.setPositiveButton("Grant Permissions", (dialog, which) -> requestPermissions());

        // Add a negative button to dismiss the dialog
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onItemClick(int position) {
            String selectedMonth = monthNames.get(position);
            String selectedYearString = (String) yearSpinner.getSelectedItem();
            int selectedYear = Integer.parseInt(selectedYearString);
            Intent intent = new Intent(MainActivity.this, MonthlyExpensesDetails.class);
            intent.putExtra("MONTH_NAME", selectedMonth);
            intent.putExtra("YEAR_NAME", selectedYear);
            startActivity(intent);
    }
}
