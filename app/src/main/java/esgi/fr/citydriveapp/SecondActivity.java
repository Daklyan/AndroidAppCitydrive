package esgi.fr.citydriveapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class SecondActivity extends AppCompatActivity {

    private Button disconnect;
    private Button refresh;
    private TextView noCourse;
    private String id;
    private Button achieved;
    private Button canceled;
    static final private String url = "https://www.citdrive.online/api/api/booking/list.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        disconnect = (Button) findViewById(R.id.bt_dc);
        refresh = (Button) findViewById(R.id.btn_refresh);
        noCourse = (TextView) findViewById(R.id.tv_nocourse);
        achieved = (Button) findViewById(R.id.btn_achieved);
        canceled = (Button) findViewById(R.id.btn_canceled);
        id = getIntent().getStringExtra("id");

        canceled.setEnabled(false);
        achieved.setEnabled(false);

        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(SecondActivity.this)
                        .setTitle(getResources().getString(R.string.dcTitle))
                        .setMessage(getResources().getString(R.string.dcContent))
                        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                disconnected(id);
                                finish(); //close the activity
                            }
                        }).setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //just close the pop up
                    }
                }).setCancelable(false).show();
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noCourse.setText(getResources().getString(R.string.noCourse));
                booking();
            }
        });

        canceled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(SecondActivity.this)
                        .setTitle(getResources().getString(R.string.sure))
                        .setMessage(getResources().getString(R.string.cancelation))
                        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                connected(id);
                                noCourse.setText(getResources().getString(R.string.noCourse));
                                achieved.setEnabled(false);
                                canceled.setEnabled(false);
                            }
                        }).setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //just close the pop up
                    }
                }).setCancelable(false).show();
            }
        });

        achieved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(SecondActivity.this)
                        .setTitle(getResources().getString(R.string.finished))
                        .setMessage(" ").setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finished(id);
                                connected(id);
                                noCourse.setText(getResources().getString(R.string.noCourse));
                                achieved.setEnabled(false);
                                canceled.setEnabled(false);
                            }
                        }).setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //just close the pop up
                    }
                }).setCancelable(false).show();
            }
        });
    }

    private void booking() {
        //Initialization
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //Method and where we seek data
        JsonArrayRequest objectRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                int count = 0;
                try {
                    JSONArray jsonarray = new JSONArray(response.toString());
                    for (int i = 0; i < jsonarray.length(); i++) {
                        final JSONObject jsonobject = jsonarray.getJSONObject(i);
                        if (id.equals(jsonobject.getString("idEmployee")) && jsonobject.getString("finish").equals("0")) {
                            new AlertDialog.Builder(SecondActivity.this)
                                    .setTitle(getResources().getString(R.string.courseContent))
                                    .setMessage(getResources().getString(R.string.start) + ": " + jsonobject.getString("startPoint") + "\n"
                                            + getResources().getString(R.string.arrival) + ": " + jsonobject.getString("endPoint"))
                                    .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            try {
                                                String text = getResources().getString(R.string.start) + " : " + jsonobject.getString("startPoint") + "\n"
                                                        + getResources().getString(R.string.arrival) + " : " + jsonobject.getString("endPoint");
                                                noCourse.setText(text);
                                                canceled.setEnabled(true);
                                                achieved.setEnabled(true);
                                                disconnected(id);
                                            } catch (Exception err) {
                                                Log.v("Error", err.getMessage());
                                            }
                                        }
                                    }).setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //just close the pop up
                                }
                            }).setCancelable(false).show();
                            count++;
                        }
                    }

                    if (count == 0) {
                        noCourse.setText(getResources().getString(R.string.noCourse));
                        Toast.makeText(SecondActivity.this, getResources().getString(R.string.noCourse), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception err) {
                    Log.v("api", err.getMessage());
                    Toast.makeText(SecondActivity.this, err.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(objectRequest);
    }

    private void disconnected(final String idDriver) {
        try {
            StringRequest request = new StringRequest(Request.Method.POST, "https://www.citdrive.online/api/disconnected.php", new Response.Listener<String>() {
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
            RequestQueue requestQueue = Volley.newRequestQueue(SecondActivity.this);
            // Adding the StringRequest object into requestQueue.
            requestQueue.add(request);
        } catch (Exception err) {
            Log.v("Update status : ", err.getMessage());
        }
    }

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
            RequestQueue requestQueue = Volley.newRequestQueue(SecondActivity.this);
            // Adding the StringRequest object into requestQueue.
            requestQueue.add(request);
        } catch (Exception err) {
            Log.v("Update status : ", err.getMessage());
        }
    }

    private void finished(final String idDriver) {
        try {
            StringRequest request = new StringRequest(Request.Method.POST, "https://www.citdrive.online/api/finished.php", new Response.Listener<String>() {
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
            RequestQueue requestQueue = Volley.newRequestQueue(SecondActivity.this);
            // Adding the StringRequest object into requestQueue.
            requestQueue.add(request);
        } catch (Exception err) {
            Log.v("Update status : ", err.getMessage());
        }
    }
}
