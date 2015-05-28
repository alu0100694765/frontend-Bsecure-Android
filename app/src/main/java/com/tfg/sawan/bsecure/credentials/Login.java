package com.tfg.sawan.bsecure.credentials;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tfg.sawan.bsecure.MainActivity;
import com.tfg.sawan.bsecure.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class Login extends Activity {
    /**
     * Login button
     */
    private Button login_button;

    /**
     * Username
     */
    protected String username;

    /**
     * Password
     */
    protected String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initiate the button
        login_button = (Button) findViewById(R.id.btnLogin);

        // Add click listener
        login_button.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                try {
                                                    onLogin();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }

        );

    }


    /**
     * Executes on click the login button
     */
    public void onLogin() throws IOException {
        // Get username and password from editTexts
        EditText user_name_editText = (EditText) findViewById(R.id.user_name);
        username = user_name_editText.getText().toString();
        EditText password_editText = (EditText) findViewById(R.id.password);
        password = password_editText.getText().toString();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Create client
        HttpClient client = new DefaultHttpClient();

        // Create Post object
        HttpPost post = new HttpPost("http://10.0.3.2:3000/login-android");

        // Add post parameters
        ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("username", username));
        parameters.add(new BasicNameValuePair("password", password));

        // Encode data
        try {
            post.setEntity(new UrlEncodedFormEntity(parameters));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // Make the HTTP POST request
        HttpResponse response = null;
        try {
            response = client.execute(post);
            Log.d("Response", response.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Get token
        String token = null;
        if (response.getStatusLine().getStatusCode() == 200) {
            HttpEntity entity = response.getEntity();
            JSONObject json = null;
            try {
                json = new JSONObject(EntityUtils.toString(entity));
                token = json.getString("token");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Switch to main activity
        Intent main_activity = new Intent(Login.this, MainActivity.class);
        main_activity.putExtra("token", token);
        startActivity(main_activity);
    }
}
