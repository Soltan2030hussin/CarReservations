package com.example.carreservations;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.*;

public class AddCarActivity extends AppCompatActivity {

    Spinner spinnerRegion, spinnerCity, spinnerLocation;
    EditText etCarModel, etCarYear, etCarPrice, etDescription;
    ImageView ivCarImage;
    Button btnSelectMap, btnSelectImage, btnSubmit;
    String selectedCityID = "", selectedLocationID = "", pickupLocation = "", encodedImage = "";
    List<String> cityNames = new ArrayList<>();
    List<String> cityIDs = new ArrayList<>();
    List<String> locationNames = new ArrayList<>();
    List<String> locationIDs = new ArrayList<>();

    static final int PICK_IMAGE = 100, PICK_MAP = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);

        spinnerRegion = findViewById(R.id.spinnerRegion);
        spinnerCity = findViewById(R.id.spinnerCity);
        spinnerLocation = findViewById(R.id.spinnerLocation);
        etCarModel = findViewById(R.id.etCarModel);
        etCarYear = findViewById(R.id.etCarYear);
        etCarPrice = findViewById(R.id.etCarPrice);
        etDescription = findViewById(R.id.etDescription);
        ivCarImage = findViewById(R.id.ivCarImage);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSelectMap = findViewById(R.id.btnSelectMap);
        btnSubmit = findViewById(R.id.btnSubmit);

        ArrayAdapter<String> regionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.saudi_regions));
        spinnerRegion.setAdapter(regionAdapter);

        spinnerRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                fetchCities(regionAdapter.getItem(pos));
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCityID = cityIDs.get(position);
                fetchLocations(selectedCityID);
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLocationID = locationIDs.get(position);
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE);
        });

        btnSelectMap.setOnClickListener(v -> {
            Intent intent = new Intent(this, SelectLocationActivity.class);
            startActivityForResult(intent, PICK_MAP);
        });

        btnSubmit.setOnClickListener(v -> submitCar());
    }

    private void fetchCities(String regionName) {
        cityNames.clear();
        cityIDs.clear();
        StringRequest request = new StringRequest(Request.Method.POST, "http://smarthostsite-002-site13.jtempurl.com/get_cities_by_region.php",
                response -> {
                    try {
                        JSONArray arr = new JSONArray(response);
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            cityNames.add(obj.getString("city_name"));
                            cityIDs.add(obj.getString("city_id"));
                        }
                        spinnerCity.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cityNames));
                    } catch (Exception e) {
                        Toast.makeText(this, "City Parse Error", Toast.LENGTH_SHORT).show();
                    }
                }, error -> Toast.makeText(this, "City Error", Toast.LENGTH_SHORT).show()) {
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("region_name", regionName);
                return map;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    private void fetchLocations(String cityId) {
        locationNames.clear();
        locationIDs.clear();
        StringRequest request = new StringRequest(Request.Method.POST, "http://smarthostsite-002-site13.jtempurl.com/get_rental_locations.php",
                response -> {
                    try {
                        JSONArray arr = new JSONArray(response);
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            locationNames.add(obj.getString("location_name"));
                            locationIDs.add(obj.getString("location_id"));
                        }
                        spinnerLocation.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, locationNames));
                    } catch (Exception e) {
                        Toast.makeText(this, "Location Parse Error", Toast.LENGTH_SHORT).show();
                    }
                }, error -> Toast.makeText(this, "Location Error", Toast.LENGTH_SHORT).show()) {
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("city_id", cityId);
                return map;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    private void submitCar() {
        String model = etCarModel.getText().toString();
        String year = etCarYear.getText().toString();
        String price = etCarPrice.getText().toString();
        String desc = etDescription.getText().toString();

        if (model.isEmpty() || year.isEmpty() || price.isEmpty() || desc.isEmpty() || pickupLocation.isEmpty() || encodedImage.isEmpty()) {
            Toast.makeText(this, "Please fill all fields and select image/map", Toast.LENGTH_LONG).show();
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST, "http://smarthostsite-002-site13.jtempurl.com/add_car.php",
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            Toast.makeText(this, "Car Added Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Error: " + obj.getString("error"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Parse Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this, "Network Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()) {

            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("user_id", PublicData.UserID);
                map.put("location_id", selectedLocationID);
                map.put("pickup_location_details", pickupLocation);
                map.put("car_model", model);
                map.put("car_year", year);
                map.put("price_per_day", price);
                map.put("car_description", desc);
                map.put("image_data", encodedImage);
                return map;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            ivCarImage.setImageURI(imageUri);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
                byte[] imageBytes = baos.toByteArray();
                encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_MAP && resultCode == RESULT_OK && data != null) {
            pickupLocation = data.getStringExtra("latlng");
        }
    }
}
