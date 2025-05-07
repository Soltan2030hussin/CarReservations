package com.example.carreservations;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ClientBookingsActivity extends AppCompatActivity {

    EditText etBookingSearch;
    Button btnSearchBooking;
    TableLayout tableBookings;
    String URL_GET_BOOKINGS = "http://smarthostsite-002-site13.jtempurl.com/get_bookings_by_user.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_bookings);



        etBookingSearch = findViewById(R.id.etBookingSearch);
        btnSearchBooking = findViewById(R.id.btnSearchBooking);
        tableBookings = findViewById(R.id.tableBookings);

        btnSearchBooking.setOnClickListener(view -> {
            String id = etBookingSearch.getText().toString().trim();
            fetchBookings(id);
        });

        fetchBookings("");
    }

    private void fetchBookings(String bookingId) {
        tableBookings.removeAllViews();

        StringRequest request = new StringRequest(Request.Method.POST, URL_GET_BOOKINGS,
                response -> {
                    try {
                        JSONArray arr = new JSONArray(response);
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject booking = arr.getJSONObject(i);

                            TableRow row = new TableRow(this);
                            row.setLayoutParams(new TableRow.LayoutParams(
                                    TableRow.LayoutParams.MATCH_PARENT,
                                    TableRow.LayoutParams.WRAP_CONTENT));
                            row.setPadding(16, 16, 16, 16);

                            LinearLayout layout = new LinearLayout(this);
                            layout.setOrientation(LinearLayout.VERTICAL);
                            layout.setLayoutParams(new TableRow.LayoutParams(
                                    TableRow.LayoutParams.MATCH_PARENT,
                                    TableRow.LayoutParams.WRAP_CONTENT));
                            layout.setBackgroundResource(R.drawable.background_booking_card);
                            layout.setPadding(16, 16, 16, 16);

                            // معلومات الحجز
                            TextView tvInfo = new TextView(this);
                            tvInfo.setText("Booking ID: " + booking.getString("booking_id") +
                                    "\nStatus: " + booking.getString("status") +
                                    "\nCar: " + booking.optString("car_model", "N/A") +
                                    "\nPrice: " + booking.optString("price_per_day", "0") + " SAR/day");
                            tvInfo.setTextSize(16);
                            layout.addView(tvInfo);

                            // صورة السيارة
                            ImageView carImage = new ImageView(this);
                            carImage.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT, 400));
                            carImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            carImage.setPadding(0, 16, 0, 16);

                            String imageUrl = "http://smarthostsite-002-site13.jtempurl.com/" + booking.optString("image_url", "");
                            Picasso.get().load(imageUrl).placeholder(R.drawable.car_placeholder).into(carImage);
                            layout.addView(carImage);

                            // أزرار QR و Details
                            LinearLayout buttonLayout = new LinearLayout(this);
                            buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
                            buttonLayout.setGravity(Gravity.CENTER);
                            buttonLayout.setPadding(0, 10, 0, 0);

                            String bookingID = booking.getString("booking_id");

                            Button btnDetails = new Button(this);
                            btnDetails.setText("DETAILS");
                            btnDetails.setBackgroundResource(R.drawable.btn_green);
                            btnDetails.setTextColor(0xFFFFFFFF);
                            btnDetails.setPadding(30, 10, 30, 10);
                            btnDetails.setOnClickListener(v -> {
                                Intent intent = new Intent(this, BookingDetailsActivity.class);
                                intent.putExtra("booking_id", bookingID);
                                startActivity(intent);
                            });

                            Button btnQR = new Button(this);
                            btnQR.setText("SHOW QR");
                            btnQR.setBackgroundResource(R.drawable.btn_blue);
                            btnQR.setTextColor(0xFFFFFFFF);
                            btnQR.setPadding(30, 10, 30, 10);
                            btnQR.setOnClickListener(v -> {
                                Intent intent = new Intent(this, ShowQrActivity.class);
                                intent.putExtra("booking_id", bookingID);
                                startActivity(intent);
                            });

                            buttonLayout.addView(btnQR);
                            buttonLayout.addView(btnDetails);
                            layout.addView(buttonLayout);

                            row.addView(layout);
                            tableBookings.addView(row);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
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
