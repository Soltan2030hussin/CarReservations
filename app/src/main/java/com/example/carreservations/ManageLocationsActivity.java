package com.example.carreservations;

import android.app.AlertDialog;
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

public class ManageLocationsActivity extends AppCompatActivity {

    Spinner spinnerRegion, spinnerCity;
    EditText etLocationName, etLocationDetails;
    Button btnSelectMap, btnAddLocation;
    TableLayout tableLocations;

    String selectedCityID = "";
    String[] regionList;
    List<String> cityNames = new ArrayList<>();
    List<String> cityIDs = new ArrayList<>();

    String URL_GET_CITIES = "http://smarthostsite-002-site13.jtempurl.com/get_cities_by_region.php";
    String URL_ADD_LOCATION = "http://smarthostsite-002-site13.jtempurl.com/add_rental_location.php";
    String URL_GET_LOCATIONS = "http://smarthostsite-002-site13.jtempurl.com/get_rental_locations.php";
    String URL_DELETE_LOCATION = "http://smarthostsite-002-site13.jtempurl.com/delete_rental_location.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_locations);

        spinnerRegion = findViewById(R.id.spinnerRegion);
        spinnerCity = findViewById(R.id.spinnerCity);
        etLocationName = findViewById(R.id.etLocationName);
        etLocationDetails = findViewById(R.id.etLocationDetails);
        btnSelectMap = findViewById(R.id.btnSelectMap);
        btnAddLocation = findViewById(R.id.btnAddLocation);
        tableLocations = findViewById(R.id.tableLocations);

        regionList = getResources().getStringArray(R.array.saudi_regions);
        ArrayAdapter<String> regionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, regionList);
        spinnerRegion.setAdapter(regionAdapter);

        spinnerRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                fetchCities(regionList[pos]);
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCityID = cityIDs.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        btnAddLocation.setOnClickListener(v -> addLocation());
        btnSelectMap.setOnClickListener(v -> {
            Intent intent = new Intent(ManageLocationsActivity.this, SelectLocationActivity.class);
            startActivityForResult(intent, 101);
        });
        fetchRentalLocations();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            String latlng = data.getStringExtra("latlng");
            etLocationDetails.setText(latlng); // إدخال الإحداثيات في الحقل
        }
    }

    private void fetchCities(String regionName) {
        cityNames.clear();
        cityIDs.clear();

        StringRequest request = new StringRequest(Request.Method.POST, URL_GET_CITIES,
                response -> {
                    try {
                        JSONArray arr = new JSONArray(response);
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            cityNames.add(obj.getString("city_name"));
                            cityIDs.add(obj.getString("city_id"));
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cityNames);
                        spinnerCity.setAdapter(adapter);
                    } catch (Exception e) {
                        Toast.makeText(this, "City Parse Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error loading cities", Toast.LENGTH_SHORT).show()) {
            @Override protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("region_name", regionName);
                return map;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    private void addLocation() {
        String name = etLocationName.getText().toString().trim();
        String details = etLocationDetails.getText().toString().trim();

        if (name.isEmpty() || details.isEmpty() || selectedCityID.isEmpty()) {
            Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST, URL_ADD_LOCATION,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            Toast.makeText(this, "Location added", Toast.LENGTH_SHORT).show();
                            etLocationName.setText("");
                            etLocationDetails.setText("");
                            fetchRentalLocations();
                        } else {
                            Toast.makeText(this, "Error: " + obj.getString("error"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show()) {
            @Override protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("city_id", selectedCityID);
                map.put("location_name", name);
                map.put("location_details", details);
                return map;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    private void fetchRentalLocations() {
        tableLocations.removeAllViews();

        // إنشاء صف العناوين
        TableRow header = new TableRow(this);
        header.setPadding(8, 8, 8, 8);
        String[] headers = {"Region", "City", "Location Name",  "Action"};
        for (String col : headers) {
            TextView tv = new TextView(this);
            tv.setText(col);
            tv.setPadding(16, 8, 16, 8);
            tv.setTextSize(16);
            tv.setTypeface(null, android.graphics.Typeface.BOLD);
            header.addView(tv);
        }
        tableLocations.addView(header);

        StringRequest request = new StringRequest(Request.Method.GET, URL_GET_LOCATIONS,
                response -> {
                    try {
                        JSONArray arr = new JSONArray(response);
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject loc = arr.getJSONObject(i);

                            TableRow row = new TableRow(this);
                            row.setPadding(8, 8, 8, 8);

                            TextView region = new TextView(this);
                            region.setText(loc.getString("region_name"));
                            region.setPadding(8, 4, 8, 4);

                            TextView city = new TextView(this);
                            city.setText(loc.getString("city_name"));
                            city.setPadding(8, 4, 8, 4);

                            TextView name = new TextView(this);
                            name.setText(loc.getString("location_name"));
                            name.setPadding(8, 4, 8, 4);


                            Button delete = new Button(this);
                            delete.setText("Delete");
                            delete.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                            delete.setTextColor(getResources().getColor(android.R.color.white));
                            String id = loc.getString("location_id");
                            delete.setOnClickListener(v -> deleteLocation(id));

                            row.addView(region);
                            row.addView(city);
                            row.addView(name);

                            row.addView(delete);
                            tableLocations.addView(row);
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Error parsing locations: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Failed to load locations", Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(this).add(request);
    }


    private void deleteLocation(String locationId) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Location")
                .setMessage("Are you sure you want to delete this rental location?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    StringRequest request = new StringRequest(Request.Method.POST, URL_DELETE_LOCATION,
                            response -> {
                                fetchRentalLocations();
                                Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                            },
                            error -> Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show()) {
                        @Override protected Map<String, String> getParams() {
                            Map<String, String> map = new HashMap<>();
                            map.put("location_id", locationId);
                            return map;
                        }
                    };
                    Volley.newRequestQueue(this).add(request);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
