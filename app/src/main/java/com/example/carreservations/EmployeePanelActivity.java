package com.example.carreservations;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class EmployeePanelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_panel);
    }
    public void goManageUsers(View view) {
        Intent intent = new Intent(this, ManageUsersActivity.class);
        startActivity(intent);
    }
    public void goManageRentalLoaction(View view) {
        Intent intent = new Intent(this, ManageLocationsActivity.class);
        startActivity(intent);
    }
    public void goManageCities(View view) {
        Intent intent = new Intent(this, ManageCitiesActivity.class);
        startActivity(intent);
    }
    public void goAddCar(View view) {
        Intent intent = new Intent(this, AddCarActivity.class);
        startActivity(intent);
    }
    public void goManageCar(View view) {
        Intent intent = new Intent(this, ManageCarsActivity.class);
        startActivity(intent);
    }
}