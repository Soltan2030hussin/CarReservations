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

public class ClientBookCarActivity extends AppCompatActivity {

    Spinner spinnerRegion, spinnerCity, spinnerLocation;
    TableLayout tableCars;
    String selectedRegion = "", selectedCityID = "", selectedLocationID = "";

    List<String> cityNames = new ArrayList<>();
    List<String> cityIDs = new ArrayList<>();
    List<String> locationNames = new ArrayList<>();
    List<String> locationIDs = new ArrayList<>();

    String URL_GET_CITIES = "http://smarthostsite-002-site13.jtempurl.com/get_cities_by_region.php";
    String URL_GET_LOCATIONS = "http://smarthostsite-002-site13.jtempurl.com/get_rental_locations.php";
    String URL_GET_CARS = "http://smarthostsite-002-site13.jtempurl.com/get_cars_filtered.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_book_car);

        spinnerRegion = findViewById(R.id.spinnerRegion);
        spinnerCity = findViewById(R.id.spinnerCity);
        spinnerLocation = findViewById(R.id.spinnerLocation);
        tableCars = findViewById(R.id.tableCars);

        List<String> regions = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.saudi_regions)));
        regions.add(0, "All Regions");

        ArrayAdapter<String> regionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, regions);
        spinnerRegion.setAdapter(regionAdapter);

        spinnerRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                selectedRegion = regions.get(pos).equals("All Regions") ? "" : regions.get(pos);
                fetchCities(selectedRegion);
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (cityIDs.size() > 0) {
                    selectedCityID = cityIDs.get(pos);
                    fetchLocations(selectedCityID);
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (locationIDs.size() > 0) {
                    selectedLocationID = locationIDs.get(pos);
                    fetchCars();
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void fetchCities(String regionName) {
        cityNames.clear();
        cityIDs.clear();

        StringRequest request = new StringRequest(Request.Method.POST, URL_GET_CITIES,
                response -> {
                    try {
                        cityNames.add("All Cities");
                        cityIDs.add("");

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
                }, error -> Toast.makeText(this, "City Load Error", Toast.LENGTH_SHORT).show()) {
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

        StringRequest request = new StringRequest(Request.Method.POST, URL_GET_LOCATIONS,
                response -> {
                    try {
                        locationNames.add("All Locations");
                        locationIDs.add("");

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
                }, error -> Toast.makeText(this, "Location Load Error", Toast.LENGTH_SHORT).show()) {
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("city_id", cityId);
                return map;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    private void fetchCars() {
        tableCars.removeAllViews();

        StringRequest request = new StringRequest(Request.Method.POST, URL_GET_CARS,
                response -> {
                    try {
                        JSONArray arr = new JSONArray(response);
                        if (arr.length() == 0) {
                            Toast.makeText(this, "No Cars Available", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject car = arr.getJSONObject(i);

                            TableRow row = new TableRow(this);
                            row.setPadding(8, 8, 8, 8);

                            TextView info = new TextView(this);
                            String price = car.optString("price_per_day", "0");
                            info.setText("Model: " + car.optString("car_model", "N/A") +
                                    "\nYear: " + car.optString("car_year", "N/A") +
                                    "\nPrice: " + price + " SAR/day");
                            info.setPadding(10, 0, 10, 0);

                            Button btnBook = new Button(this);
                            btnBook.setText("Book Now");
                            String carId = car.getString("car_id");

                            btnBook.setOnClickListener(v -> {
                                PublicData.price_per_day = price;
                                openBookingScreen(carId);
                            });

                            row.addView(info);
                            row.addView(btnBook);
                            tableCars.addView(row);
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Parsing Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, error -> Toast.makeText(this, "Load Cars Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()) {
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("region", selectedRegion);
                map.put("city_id", selectedCityID);
                map.put("location_id", selectedLocationID);
                return map;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    private void openBookingScreen(String carId) {
        Intent intent = new Intent(ClientBookCarActivity.this, BookCarDetailsActivity.class);
        intent.putExtra("car_id", carId);
        startActivity(intent);
    }
}
