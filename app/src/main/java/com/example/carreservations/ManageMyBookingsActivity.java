package com.example.carreservations;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.*;

public class ManageMyBookingsActivity extends AppCompatActivity {

    EditText etBookingSearch;
    Button btnSearchBooking;
    TableLayout tableBookings;
    String URL_GET_MY_BOOKINGS = "http://smarthostsite-002-site13.jtempurl.com/get_my_bookings.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_my_bookings);

        etBookingSearch = findViewById(R.id.etBookingSearch);
        btnSearchBooking = findViewById(R.id.btnSearchBooking);
        tableBookings = findViewById(R.id.tableBookings);

        btnSearchBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchBookings(etBookingSearch.getText().toString().trim());
            }
        });

        fetchBookings(""); // أول مرة يعرض كل الحجوزات
    }

    private void fetchBookings(String bookingId) {
        tableBookings.removeAllViews();

        StringRequest request = new StringRequest(Request.Method.POST, URL_GET_MY_BOOKINGS,
                response -> {
                    try {
                        JSONArray arr = new JSONArray(response);
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject booking = arr.getJSONObject(i);
                            TableRow row = new TableRow(this);

                            TextView tvInfo = new TextView(this);
                            tvInfo.setText("Booking ID: " + booking.optString("booking_id", "N/A")
                                    + "\nStatus: " + booking.optString("status", "N/A"));
                            tvInfo.setPadding(16, 16, 16, 16);

                            Button btnDetails = new Button(this);
                            btnDetails.setText("Details");
                            btnDetails.setOnClickListener(v -> {
                                Intent intent = new Intent(this, BookingDetailsActivity.class);
                                intent.putExtra("booking_id", booking.optString("booking_id", ""));
                                startActivity(intent);
                            });

                            Button btnQR = new Button(this);
                            btnQR.setText("Show QR");
                            btnQR.setOnClickListener(v -> {
                                Intent intent = new Intent(this, ShowQrActivity.class);
                                intent.putExtra("booking_id", booking.optString("booking_id", ""));
                                startActivity(intent);
                            });

                            row.addView(tvInfo);
                            row.addView(btnDetails);
                            row.addView(btnQR);

                            tableBookings.addView(row);
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Parsing Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error loading bookings", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("user_id", PublicData.UserID);
                map.put("booking_id", bookingId);
                return map;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

}
