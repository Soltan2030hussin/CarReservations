package com.example.carreservations;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button btnLogin;
    TextView tvRegister;

    String URL = "http://smarthostsite-002-site13.jtempurl.com/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        btnLogin.setOnClickListener(v -> loginUser());

        tvRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    private void loginUser() {
        String mail = email.getText().toString();
        String pass = password.getText().toString();

        StringRequest request = new StringRequest(Request.Method.POST, URL,
                response -> {
                    try {
                        Log.d("LOGIN_RESPONSE", response);
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            JSONObject user = obj.getJSONObject("user");

                            // تخزين بيانات الدخول في PublicData
                            PublicData.UserID = user.getString("user_id");
                            PublicData.UserName = user.getString("full_name");
                            PublicData.UserKind = user.getString("user_kind");

                            Toast.makeText(this, "Welcome, " + PublicData.UserName, Toast.LENGTH_SHORT).show();

                            // التنقل حسب نوع المستخدم
                            Intent intent;
                            switch (PublicData.UserKind.toLowerCase()) {
                                case "admin":
                                    intent = new Intent(this, AdminPanelActivity.class);
                                    break;
                                case "employee":
                                    intent = new Intent(this, EmployeePanelActivity.class);
                                    break;
                                case "client":
                                    intent = new Intent(this, ClientPanelActivity.class);
                                    break;
                                default:
                                    Toast.makeText(this, "Unknown user type", Toast.LENGTH_LONG).show();
                                    return;
                            }

                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(this, "Error: " + obj.getString("error"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "JSON Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this, "Volley Error: " + error.getMessage(), Toast.LENGTH_LONG).show()) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("email", mail);
                map.put("password", pass);
                return map;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
