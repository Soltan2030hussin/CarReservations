// 1. تأكد أنك أضفت dependencies في build.gradle:
// implementation 'androidx.recyclerview:recyclerview:1.3.1'
// implementation 'com.squareup.picasso:picasso:2.8'

// 2. كود Java لـ ManageCarsActivity باستخدام RecyclerView + CardView
package com.example.carreservations;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class ManageCarsActivity extends AppCompatActivity {

    Spinner spinnerRegion, spinnerCity;
    RecyclerView recyclerCars;

    String[] regionList;
    List<String> cityNames = new ArrayList<>();
    List<String> cityIDs = new ArrayList<>();
    String selectedCityID = "";
    List<JSONObject> carList = new ArrayList<>();
    CarAdapter adapter;

    String URL_GET_CITIES = "http://smarthostsite-002-site13.jtempurl.com/get_cities_by_region.php";
    String URL_GET_CARS = "http://smarthostsite-002-site13.jtempurl.com/get_cars_by_city.php";
    String URL_DELETE_CAR = "http://smarthostsite-002-site13.jtempurl.com/delete_car.php";
    String URL_APPROVE_CAR = "http://smarthostsite-002-site13.jtempurl.com/approve_car.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_cars);

        spinnerRegion = findViewById(R.id.spinnerRegion);
        spinnerCity = findViewById(R.id.spinnerCity);
        recyclerCars = findViewById(R.id.recyclerCars);

        regionList = getResources().getStringArray(R.array.saudi_regions);
        ArrayAdapter<String> regionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, regionList);
        spinnerRegion.setAdapter(regionAdapter);

        recyclerCars.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CarAdapter();
        recyclerCars.setAdapter(adapter);

        spinnerRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                fetchCities(regionAdapter.getItem(pos));
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCityID = cityIDs.get(position);
                fetchCars(selectedCityID);
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
                },
                error -> Toast.makeText(this, "Error loading cities", Toast.LENGTH_SHORT).show()) {
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("region_name", regionName);
                return map;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    private void fetchCars(String cityId) {
        carList.clear();

        StringRequest request = new StringRequest(Request.Method.POST, URL_GET_CARS,
                response -> {
                    try {
                        JSONArray arr = new JSONArray(response);
                        for (int i = 0; i < arr.length(); i++) {
                            carList.add(arr.getJSONObject(i));
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Toast.makeText(this, "Car Parse Error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error fetching cars", Toast.LENGTH_SHORT).show()) {
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("city_id", cityId);
                return map;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {

        class CarViewHolder extends RecyclerView.ViewHolder {
            TextView tvInfo;
            ImageView img;
            Button btnApprove, btnDelete;

            CarViewHolder(View itemView) {
                super(itemView);
                tvInfo = itemView.findViewById(R.id.tvCarInfo);
                img = itemView.findViewById(R.id.imgCar);
                btnApprove = itemView.findViewById(R.id.btnApprove);
                btnDelete = itemView.findViewById(R.id.btnDelete);
            }
        }

        @Override
        public CarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_car_item, parent, false);
            return new CarViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CarViewHolder holder, int position) {
            JSONObject car = carList.get(position);
            try {
                holder.tvInfo.setText("Model: " + car.getString("car_model") +
                        "\nYear: " + car.getString("car_year") +
                        "\nPrice/Day: " + car.getString("price_per_day") +
                        "\nPickup: " + car.getString("pickup_location_details") +
                        "\nOwner: " + car.getString("full_name") +
                        "\nUser Type: " + car.getString("user_kind") +
                        "\nStatus: " + car.getString("approval_status"));

                String imageUrl = "http://smarthostsite-002-site13.jtempurl.com/" + car.optString("image_url", "");
                Picasso.get().load(imageUrl).placeholder(R.drawable.car_placeholder).into(holder.img);

                String carId = car.getString("car_id");

                holder.btnDelete.setOnClickListener(v -> deleteCar(carId));
                holder.btnApprove.setOnClickListener(v -> approveCar(carId));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return carList.size();
        }
    }

    private void deleteCar(String carId) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Car")
                .setMessage("Are you sure you want to delete this car?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    StringRequest request = new StringRequest(Request.Method.POST, URL_DELETE_CAR,
                            response -> fetchCars(selectedCityID),
                            error -> Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show()) {
                        protected Map<String, String> getParams() {
                            Map<String, String> map = new HashMap<>();
                            map.put("car_id", carId);
                            return map;
                        }
                    };
                    Volley.newRequestQueue(this).add(request);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void approveCar(String carId) {
        StringRequest request = new StringRequest(Request.Method.POST, URL_APPROVE_CAR,
                response -> fetchCars(selectedCityID),
                error -> Toast.makeText(this, "Approval failed", Toast.LENGTH_SHORT).show()) {
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("car_id", carId);
                return map;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }
}
