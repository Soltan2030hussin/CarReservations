package com.example.carreservations;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class BookCarDetailsActivity extends AppCompatActivity {

    EditText etStartDate, etEndDate, etAmount;
    Button btnSelectReceipt, btnConfirmBooking;
    ImageView ivReceiptPreview;
    String carId;
    double pricePerDay;
    String encodedReceipt = "";

    static final int PICK_RECEIPT = 101;

    Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_car_details);

        etStartDate = findViewById(R.id.etStartDate);
        etEndDate = findViewById(R.id.etEndDate);
        etAmount = findViewById(R.id.etAmount);
        btnSelectReceipt = findViewById(R.id.btnSelectReceipt);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);
        ivReceiptPreview = findViewById(R.id.ivReceiptPreview);

        carId = getIntent().getStringExtra("car_id");
        pricePerDay = Double.parseDouble(PublicData.price_per_day);

        // عرض السعر بالتوست
        Toast.makeText(this, "Price Per Day: " + pricePerDay, Toast.LENGTH_SHORT).show();

        // Date pickers
        etStartDate.setOnClickListener(v -> showDatePickerDialog(etStartDate));
        etEndDate.setOnClickListener(v -> showDatePickerDialog(etEndDate));

        // Auto calculate when end date is picked
        etEndDate.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) calculateAmount();
        });

        btnSelectReceipt.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_RECEIPT);
        });

        btnConfirmBooking.setOnClickListener(v -> confirmBooking());
    }

    private void showDatePickerDialog(EditText target) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, y, m, d) -> {
            String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", y, m + 1, d);
            target.setText(date);

            if (target == etEndDate) calculateAmount();
        }, year, month, day).show();
    }

    private void calculateAmount() {
        try {
            String startStr = etStartDate.getText().toString();
            String endStr = etEndDate.getText().toString();

            if (!startStr.isEmpty() && !endStr.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date startDate = sdf.parse(startStr);
                Date endDate = sdf.parse(endStr);

                if (startDate != null && endDate != null && !endDate.before(startDate)) {
                    long diff = endDate.getTime() - startDate.getTime();
                    int days = (int) (diff / (1000 * 60 * 60 * 24)) + 1;
                    double totalAmount = days * pricePerDay;

                    etAmount.setText(String.format(Locale.getDefault(), "%.2f", totalAmount));

                    Toast.makeText(this, "Price: " + pricePerDay + " x " + days + " days", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "End date must be after start date", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Date error", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmBooking() {
        String startDate = etStartDate.getText().toString().trim();
        String endDate = etEndDate.getText().toString().trim();
        String amount = etAmount.getText().toString().trim();

        if (startDate.isEmpty() || endDate.isEmpty() || encodedReceipt.isEmpty()) {
            Toast.makeText(this, "Fill all fields & select receipt", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST,
                "http://smarthostsite-002-site13.jtempurl.com/add_booking.php",
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            Toast.makeText(this, "Booking Confirmed!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Booking Failed: " + obj.getString("error"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Server Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },

                error -> Toast.makeText(this, "Booking Error", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("user_id", PublicData.UserID);
                map.put("car_id", carId);
                map.put("start_date", startDate);
                map.put("end_date", endDate);
                map.put("amount", amount);
                map.put("receipt_image", encodedReceipt);
                return map;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_RECEIPT && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            ivReceiptPreview.setImageURI(imageUri);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
                byte[] imageBytes = baos.toByteArray();
                encodedReceipt = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
