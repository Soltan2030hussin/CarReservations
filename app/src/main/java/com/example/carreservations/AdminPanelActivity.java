package com.example.carreservations;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AdminPanelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);
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

}