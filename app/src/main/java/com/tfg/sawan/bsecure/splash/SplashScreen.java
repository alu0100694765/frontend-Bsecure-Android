package com.tfg.sawan.bsecure.splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.tfg.sawan.bsecure.MainActivity;
import com.tfg.sawan.bsecure.R;
import com.tfg.sawan.bsecure.credentials.Login;
import com.tfg.sawan.bsecure.credentials.Token;
import com.tfg.sawan.bsecure.utils.Preferences;

/**
 * File name:	com.tfg.sawan.bsecure
 * Version:		1.0
 * Date:		26/05/2015 12:39
 * Author:		Sawan J. Kapai Harpalani
 * Copyright:	Copyright 200X Sawan J. Kapai Harpalani
 * <p/>
 * This file is part of Bsecure.
 * <p/>
 * Bsecure is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software
 * Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * <p/>
 * Bsecure is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details.
 * <p/>
 * You should have received a copy of the GNU General
 * Public License along with Bsecure. If not, see
 * http://www.gnu.org/licenses/.
 */


public class SplashScreen extends Activity {
    // Splash screen timer
    private final static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        // Load Token from SharedPreferences
        Token.loadToken(this);
        Log.d("Token", Token.getToken());
        Log.d("Expiry", Token.getExpiry_date());

        new Handler().postDelayed(new Runnable() {

            /*
            * Showing splash screen with a timer.
            */
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent main_activity;

                // Check if the token exists and its not expired
                if (Token.tokenExists() && !Token.isExpired()) {
                    main_activity = new Intent(SplashScreen.this, MainActivity.class);
                } else {
                    main_activity = new Intent(SplashScreen.this, Login.class);
                }
                startActivity(main_activity);

                // close the activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
