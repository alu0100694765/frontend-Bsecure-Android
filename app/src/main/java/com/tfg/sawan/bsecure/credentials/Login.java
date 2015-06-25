package com.tfg.sawan.bsecure.credentials;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
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
import android.widget.Toast;

import com.tfg.sawan.bsecure.MainActivity;
import com.tfg.sawan.bsecure.R;
import com.tfg.sawan.bsecure.utils.Preferences;

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
    protected Button login_button;

    /**
     * SignUp button
     */
    protected Button signup_button;

    /**
     * Username
     */
    protected String username;

    /**
     * Password
     */
    protected String password;

    /**
     * Static Messages
     */
    protected static final String FILL_FIELD = "Fill this field";
    protected static final String INVALID_CREDENTIALS_MESSAGE = "Invalid credentials";
    protected static final String EMPTY_FIELDS = "Some fields are incomplete! Fill them!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initiate the button
        login_button = (Button) findViewById(R.id.btnLogin);
        signup_button = (Button) findViewById(R.id.btnSingUp);

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

        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSignUp();
            }
        });
    }

    protected void onSignUp() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://bsecure-sawankapai.rhcloud.com/signup"));
        startActivity(browserIntent);
    }

    /**
     * Executes on click the login button
     */
    protected void onLogin() throws IOException {
        // Get username and password from editTexts
        EditText user_name_editText = (EditText) findViewById(R.id.user_name);
        username = user_name_editText.getText().toString();
        EditText password_editText = (EditText) findViewById(R.id.password);
        password = password_editText.getText().toString();

        // If editText are not empty
        if (!username.trim().equals("") && !password.trim().equals("")) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            // Create client
            HttpClient client = new DefaultHttpClient();

            // Create Post object
            HttpPost post = new HttpPost("http://bsecure-sawankapai.rhcloud.com/login-android");

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
            String expiry_date = null;
            String name = null;
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                JSONObject json = null;
                try {
                    json = new JSONObject(EntityUtils.toString(entity));
                    token = json.getString("token");
                    expiry_date = json.getString("exp").replace("/", "-");
                    name = json.getString("name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, INVALID_CREDENTIALS_MESSAGE, Toast.LENGTH_SHORT).show();
                Intent restart = getIntent();
                finish();
                startActivity(restart);
            }

            // Add to sharedPreferences
            Preferences.savePreferences(this, "token", token);
            Preferences.savePreferences(this, "expiry", expiry_date);
            Preferences.savePreferences(this, "name", name);

            // Set the Token
            Token.setToken(token);
            Token.setExpiry_date(expiry_date);
            Token.setUser_name(name);

            // Switch to main activity
            Intent main_activity = new Intent(Login.this, MainActivity.class);
            startActivity(main_activity);

            // Finish the activity
            finish();

          // Else alert user changing placeholder and Toast
        } else {

            if (username.trim().equals("")) {
                user_name_editText.setHintTextColor(ColorStateList.valueOf(Color.RED));
                user_name_editText.setHint(FILL_FIELD);
            }

            if (password.trim().equals("")) {
                password_editText.setHintTextColor(ColorStateList.valueOf(Color.RED));
                password_editText.setHint(FILL_FIELD);
            }

            Toast.makeText(this, EMPTY_FIELDS, Toast.LENGTH_SHORT).show();
        }
    }
}
