package com.tfg.sawan.bsecure;


import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TextView;

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


public class MainActivity extends Activity {

    /** Token */
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get Extras (Token)
        Bundle extras =  getIntent().getExtras();
        if (extras != null) {
            token = Preferences.readPreferences(this, "token", Preferences.NOT_FOUND_MESSAGE);
            Log.d("Http Post Response:", token);
        }

        TextView test = (TextView) findViewById(R.id.token);
        test.setText(token);
    }
}
