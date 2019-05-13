package esgi.fr.citydriveapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button btnLogin;
    private TextView register;
    private EditText login;
    private EditText password;

    static final private String url = "https://www.citdrive.online/api/api/employee/list.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnLogin = (Button) findViewById(R.id.btn_login);
        register = (TextView) findViewById(R.id.register);
        login = (EditText) findViewById(R.id.et_login);
        password = (EditText) findViewById(R.id.et_password);

        String html = "<a href=\"https://www.citdrive.online/contact.php\">" + getResources().getString(R.string.register) + "</a>";
        Spanned result;
        result = Html.fromHtml(html);
        register.setText(result);
        register.setMovementMethod(LinkMovementMethod.getInstance());

        //Login Action
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //Login
                confirm(login.getText().toString(), password.getText().toString());
            }
        });
    }

    //Verify password of the user
    private void confirm(final String username, final String password) {
        //Initialization
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //Method and where we seek data
        JsonArrayRequest objectRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                int count = 0;
                try {
                    JSONArray jsonarray = new JSONArray(response.toString());

                    //Going through the json array
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(i);
                        //Look if the usernames correspond with one listed by the api
                        if (username.equals(jsonobject.getString("username"))) {
                            count++;
                            //Verify if password is the same in the edit text and the listed one by the api
                            if (password.equals(jsonobject.getString("password"))) {
                                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                                intent.putExtra("id", jsonobject.getString("id"));
                                intent.putExtra("name", jsonobject.getString("firstName"));
                                connected(jsonobject.getString("id"));
                                startActivity(intent);
                            } else {
                                Toast.makeText(MainActivity.this, getResources().getString(R.string.notValidPass), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    if (count == 0) {
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.notValidUser), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception err) {
                    Log.v("api", err.getMessage());
                    Toast.makeText(MainActivity.this, err.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,"Oh dear we are in trouble",Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(objectRequest);
    }

    //Connect the user in the database
    private void connected(final String idDriver) {
        try {
            StringRequest request = new StringRequest(Request.Method.POST, "https://www.citdrive.online/api/connected.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("Success", "" + response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError err) {
                    Log.i("Error : ", "" + err.getMessage());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> map = new HashMap<>();
                    map.put("idEmployee", idDriver);
                    return map;
                }
            };
            // Creating RequestQueue.
            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
            // Adding the StringRequest object into requestQueue.
            requestQueue.add(request);
        } catch (Exception err) {
            Log.v("Update status : ", err.getMessage());
        }
    }
}
