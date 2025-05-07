package com.example.carreservations;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;

public class ManageCitiesActivity extends AppCompatActivity {

    EditText etCityName, etRegionName, etCityDetails;
    Button btnAddCity;
    TableLayout tableCities;
    Spinner spinnerRegion;
    String URL_GET = "http://smarthostsite-002-site13.jtempurl.com/get_cities.php";
    String URL_ADD = "http://smarthostsite-002-site13.jtempurl.com/add_city.php";
    String URL_DELETE = "http://smarthostsite-002-site13.jtempurl.com/delete_city.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_cities);

        etCityName = findViewById(R.id.etCityName);

        etCityDetails = findViewById(R.id.etCityDetails);
        btnAddCity = findViewById(R.id.btnAddCity);
        tableCities = findViewById(R.id.tableCities);
         spinnerRegion = findViewById(R.id.spinnerRegion);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.saudi_regions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRegion.setAdapter(adapter);
        btnAddCity.setOnClickListener(view -> addCity());
        loadCities();
    }

    private void addCity() {
        String name = etCityName.getText().toString();
        String region = spinnerRegion.getSelectedItem().toString();
        String details = etCityDetails.getText().toString();

        StringRequest request = new StringRequest(Request.Method.POST, URL_ADD,
                response -> {
                    Toast.makeText(this, "City added", Toast.LENGTH_SHORT).show();
                    etCityName.setText("");  etCityDetails.setText("");
                    loadCities();
                },
                error -> Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("city_name", name);
                map.put("region_name", region);
                map.put("city_details", details);
                return map;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void loadCities() {
        tableCities.removeAllViews();

        StringRequest request = new StringRequest(Request.Method.GET, URL_GET,
                response -> {
                    try {
                        JSONArray arr = new JSONArray(response);
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            TableRow row = new TableRow(this);

                            TextView tvName = new TextView(this);
                            tvName.setText(obj.getString("city_name"));
                            tvName.setPadding(10, 10, 10, 10);

                            TextView tvRegion = new TextView(this);
                            tvRegion.setText(obj.getString("region_name"));
                            tvRegion.setPadding(10, 10, 10, 10);

                            Button btnDelete = new Button(this);
                            btnDelete.setText("Delete");
                            btnDelete.setBackgroundColor(0xFFD32F2F);
                            btnDelete.setTextColor(0xFFFFFFFF);
                            String cityId = obj.getString("city_id");

                            btnDelete.setOnClickListener(v -> deleteCity(cityId));

                            row.addView(tvName);
                            row.addView(tvRegion);
                            row.addView(btnDelete);

                            tableCities.addView(row);
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Error parsing", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error loading cities", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(this).add(request);
    }

    private void deleteCity(String cityId) {
        StringRequest request = new StringRequest(Request.Method.POST, URL_DELETE,
                response -> {
                    Toast.makeText(this, "City deleted", Toast.LENGTH_SHORT).show();
                    loadCities();
                },
                error -> Toast.makeText(this, "Error deleting", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("city_id", cityId);
                return map;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
