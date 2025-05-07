package com.example.carreservations;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ClientPanelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_panel);
    }
    public void goAddCar(View view) {
        Intent intent = new Intent(this, AddCarActivity.class);
        startActivity(intent);
    }
    public void bookCar(View view) {
        Intent intent = new Intent(this, ClientBookCarActivity.class);
        startActivity(intent);
    }
    public void myBookings(View view) {
        Intent intent = new Intent(this, ClientBookingsActivity.class);
        startActivity(intent);
    }
    public void manageMyBookingCar(View view) {
        Intent intent = new Intent(this, AddCarActivity.class);
        startActivity(intent);
    }
}