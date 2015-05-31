package com.tfg.sawan.bsecure;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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


public class MainActivity extends Activity {

    /** Token */
    private String token;

    /** Exit the app **/
    private Boolean exit = false;

    protected ImageButton scan_button;

    protected final static int DELAY_TIME_EXIT = 3 * 1000;

    protected final static String TOAST_MESSAGE = "Press Back again to Exit.";

    protected BluetoothAdapter bluetooth_adapter;

    protected  RotateAnimation scan_animation;

    protected final static int REQUEST_ENABLE_BT   = 1;

    protected final static String BLUETOOTH_ENABLE_MESSAGE  = "Bluetooth enabled successfully";

    protected final static String BLUETOOTH_NOT_SUPPORTED  = "Bluetooth not supported";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bluetooth_adapter = BluetoothAdapter.getDefaultAdapter();

        if (isBluetoothSupported()) {
            if (!bluetooth_adapter.isEnabled()) {
                enableBluetooth();
            }
        } else {
            Toast.makeText(this, BLUETOOTH_NOT_SUPPORTED, Toast.LENGTH_SHORT).show();
            finish();
        }

        token = Token.getToken();

        scan_button = (ImageButton) findViewById(R.id.scanButton);

        scan_animation = new RotateAnimation(0f, 350f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scan_animation.setInterpolator(new LinearInterpolator());
        scan_animation.setRepeatCount(Animation.INFINITE);
        scan_animation.setDuration(4000);

        scan_button.startAnimation(scan_animation);

        scan_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLoading();
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, TOAST_MESSAGE,
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, DELAY_TIME_EXIT);

        }

    }

    protected boolean isBluetoothSupported() {
        if (bluetooth_adapter ==  null) {
            return false;
        } else {
            return true;
        }
    }

    protected void enableBluetooth() {
        Intent request_bluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(request_bluetooth, REQUEST_ENABLE_BT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, BLUETOOTH_ENABLE_MESSAGE, Toast.LENGTH_SHORT).show();
            } else {
                enableBluetooth();
            }
        }
    }

    protected  void onLoading() {
        TextView text_tap_me = (TextView) findViewById(R.id.tapMe);
        text_tap_me.setVisibility(View.INVISIBLE);

        scan_animation.cancel();

        scan_button.setVisibility(View.INVISIBLE);

        ImageView scanning_image = (ImageView) findViewById(R.id.scanning_image);

        scanning_image.setBackgroundResource(R.drawable.scan_animation_list);
        AnimationDrawable scan_animation =  (AnimationDrawable) scanning_image.getBackground();
        scan_animation.start();
    }
}
