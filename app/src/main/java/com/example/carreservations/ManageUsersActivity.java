package com.example.carreservations;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

public class ManageUsersActivity extends AppCompatActivity {

    RecyclerView recyclerUsers;
    ArrayList<User> userList;
    UserAdapter adapter;

    String URL = "http://smarthostsite-002-site13.jtempurl.com/get_users.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        recyclerUsers = findViewById(R.id.recyclerUsers);
        recyclerUsers.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        adapter = new UserAdapter(userList);
        recyclerUsers.setAdapter(adapter);

        loadUsers();
    }

    private void loadUsers() {
        StringRequest request = new StringRequest(Request.Method.GET, URL,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            userList.add(new User(
                                    obj.getString("user_id"),
                                    obj.getString("full_name"),
                                    obj.getString("email"),
                                    obj.getString("user_kind"),
                                    obj.getString("mobile")
                            ));
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Toast.makeText(this, "Parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show());

        Volley.newRequestQueue(this).add(request);
    }
}
