package my.edu.tarc.accountdetail;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "my.edu.tarc.accountdetail";
    ListView listViewCourse;

    List<Account> caList;
    private ProgressDialog pDialog;
    private static String GET_URL = "https://cash-on-wise.000webhostapp.com/account_detail.php";
    Account account = new Account();
    RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pDialog = new ProgressDialog(this);
        caList = new ArrayList<>();
        if (!isConnected()) {
            Toast.makeText(getApplicationContext(), "No network", Toast.LENGTH_LONG).show();
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        downloadCourse(getApplicationContext(), GET_URL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }
    private void downloadCourse(Context context, String url) {
        // Instantiate the RequestQueue
        queue = Volley.newRequestQueue(context);

        if (!pDialog.isShowing())
            pDialog.setMessage("Loading...");
        pDialog.show();

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            caList.clear();
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject courseResponse = (JSONObject) response.get(i);

                                String id = courseResponse.getString("id");

                                if(id.matches("COW00001")) {
                                    String name = courseResponse.getString("name");
                                    String icnum = courseResponse.getString("icnum");
                                    String contactnum = courseResponse.getString("contactnum");
                                    String address = courseResponse.getString("address");
                                    String email = courseResponse.getString("email");
                                    String password = courseResponse.getString("password");
                                    String pin = courseResponse.getString("pin");

                                    Account account = new Account(name, icnum, contactnum, address, email, password, pin);
                                    //caList.add(account);
                                    //loadCourse();
                                    TextView textViewName, textViewIC, textViewContact, textViewAddress, textViewEmail, textViewPassword, textViewPIN;

                                    textViewName = (TextView) findViewById(R.id.textViewName);
                                    textViewIC = (TextView) findViewById(R.id.textViewIC);
                                    textViewContact = (TextView) findViewById(R.id.textViewContact);
                                    textViewAddress = (TextView) findViewById(R.id.textViewAddress);
                                    textViewEmail = (TextView) findViewById(R.id.textViewEmail);
                                    textViewPassword = (TextView) findViewById(R.id.textViewPassword);
                                    textViewPIN = (TextView) findViewById(R.id.textViewPIN);

                                    textViewName.setText(textViewName.getText() + ":" + account.getName());
                                    textViewIC.setText(textViewIC.getText() + ":" + account.getIcnum());
                                    textViewContact.setText(textViewContact.getText() + ":" + account.getContactnum());
                                    textViewAddress.setText(textViewAddress.getText() + ":" + account.getAddress());
                                    textViewEmail.setText(textViewEmail.getText() + ":" + account.getEmail());
                                    textViewPassword.setText(textViewPassword.getText() + ":" + account.getPassword());
                                    textViewPIN.setText(textViewPIN.getText() + ":" + account.getPin());
                                }
                            }

                            if (pDialog.isShowing())
                                pDialog.dismiss();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Error:" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getApplicationContext(), "Error" + volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        if (pDialog.isShowing())
                            pDialog.dismiss();
                    }
                });

        // Set the tag on the request.
        jsonObjectRequest.setTag(TAG);

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private void loadCourse() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (queue != null) {
            queue.cancelAll(TAG);
        }
    }
}
