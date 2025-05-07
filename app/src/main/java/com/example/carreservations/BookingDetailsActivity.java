package com.example.carreservations;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class BookingDetailsActivity extends AppCompatActivity {

    TextView tvBookingDetails;
    String URL_GET_BOOKING_DETAILS = "http://smarthostsite-002-site13.jtempurl.com/get_booking_details.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);

        tvBookingDetails = findViewById(R.id.tvBookingDetails);

        String bookingId = getIntent().getStringExtra("booking_id");
        if (bookingId != null) {
            fetchBookingDetails(bookingId);
        } else {
            Toast.makeText(this, "No booking ID found", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchBookingDetails(String bookingId) {
        StringRequest request = new StringRequest(Request.Method.POST, URL_GET_BOOKING_DETAILS,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            JSONObject booking = obj.getJSONObject("booking");

                            String details = "Booking ID: " + booking.getString("booking_id") +
                                    "\nCar Model: " + booking.getString("car_model") +
                                    "\nStart Date: " + booking.getString("start_date") +
                                    "\nEnd Date: " + booking.getString("end_date") +
                                    "\nPickup Location: " + booking.getString("pickup_location") +
                                    "\nDropoff Location: " + booking.getString("dropoff_location") +
                                    "\nStatus: " + booking.getString("status");

                            tvBookingDetails.setText(details);
                        } else {
                            Toast.makeText(this, "No booking details found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Parsing Error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error loading details", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("booking_id", bookingId);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }
}
