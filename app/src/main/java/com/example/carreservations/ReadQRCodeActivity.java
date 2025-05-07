package com.example.carreservations;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;

public class ReadQRCodeActivity extends AppCompatActivity {

    Button btnStartScan;
    String URL_UPDATE_BOOKING = "http://smarthostsite-002-site13.jtempurl.com/update_booking_status_by_qr.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_qrcode);

        btnStartScan = findViewById(R.id.btnStartScan);

        btnStartScan.setOnClickListener(v -> {
            IntentIntegrator integrator = new IntentIntegrator(ReadQRCodeActivity.this);
            integrator.setOrientationLocked(false);
            integrator.setPrompt("Scan QR Code");
            integrator.initiateScan();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if (result != null && result.getContents() != null) {
            String bookingID = result.getContents(); // QR contains booking ID
            updateBookingStatus(bookingID);
        } else {
            Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateBookingStatus(String bookingID) {
        StringRequest request = new StringRequest(Request.Method.POST, URL_UPDATE_BOOKING,
                response -> Toast.makeText(this, "Car delivered successfully!", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("booking_id", bookingID);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }
}
