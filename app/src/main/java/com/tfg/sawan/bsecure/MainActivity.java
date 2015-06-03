package com.tfg.sawan.bsecure;


import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.tfg.sawan.bsecure.beacon.BeaconConfigFragment;
import com.tfg.sawan.bsecure.beacon.NearbyBeaconsFragment;
import com.tfg.sawan.bsecure.credentials.Login;
import com.tfg.sawan.bsecure.credentials.Token;
import com.tfg.sawan.bsecure.beacon.UriBeaconDiscoveryService;
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

    protected Button logout_button;

    protected Button config_beacon_button;

    protected TextView user_name_textView;

    protected final static int DELAY_TIME_EXIT = 3 * 1000;

    protected final static String TOAST_MESSAGE = "Press Back again to Exit.";

    protected BluetoothAdapter bluetooth_adapter;

    protected  RotateAnimation scan_animation;

    protected final static int REQUEST_ENABLE_BT   = 1;

    protected final static String BLUETOOTH_ENABLE_MESSAGE  = "Bluetooth enabled successfully";

    protected final static String BLUETOOTH_NOT_SUPPORTED  = "Bluetooth not supported";

    protected final static String WELCOME_MESSAGE = "Welcome ";

    protected final static int SCAN_PERIOD = 10000;

    private static final String NEARBY_BEACONS_FRAGMENT_TAG = "NearbyBeaconsFragmentTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadCredentials();

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
        logout_button = (Button) findViewById(R.id.btnLogout);
        config_beacon_button = (Button) findViewById(R.id.btnConfigBeacon);

        scan_animation = new RotateAnimation(0f, 350f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scan_animation.setInterpolator(new LinearInterpolator());
        scan_animation.setRepeatCount(Animation.INFINITE);
        scan_animation.setDuration(4000);

        scan_button.startAnimation(scan_animation);

        scan_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onScanning();
            }
        });
        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
        config_beacon_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConfig();
            }
        });
    }

    protected void logout() {
        Preferences.removeAllPreferences(this);

        Intent login_activity = new Intent(MainActivity.this, Login.class);
        startActivity(login_activity);

        finish();
    }

    protected void onConfig() {
        showBeaconConfigFragment();
    }


    protected void loadCredentials () {
        user_name_textView = (TextView) findViewById(R.id.user_name_text);
        user_name_textView.setText(WELCOME_MESSAGE + Token.getUser_name());
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

    protected  void onScanning() {
        /**TextView text_tap_me = (TextView) findViewById(R.id.tapMe);
        text_tap_me.setVisibility(View.INVISIBLE);

        user_name_textView.setVisibility(View.INVISIBLE);

        logout_button.setVisibility(View.INVISIBLE);

        scan_animation.cancel();

        scan_button.setVisibility(View.INVISIBLE);

        ImageView scanning_image = (ImageView) findViewById(R.id.scanning_image);

        scanning_image.setBackgroundResource(R.drawable.scan_animation_list);
        AnimationDrawable scan_animation =  (AnimationDrawable) scanning_image.getBackground();

        scan_animation.start();
        **/
        startUriBeaconDiscoveryService();
        showNearbyBeaconsFragment(false);
    }

    /**
     * Stop the beacon discovery service from running.
     */
    private void stopUriBeaconDiscoveryService() {
        Intent intent = new Intent(this, UriBeaconDiscoveryService.class);
        stopService(intent);
    }

    /**
     * Start up the BeaconDiscoveryService
     */
    private void startUriBeaconDiscoveryService() {
        Intent intent = new Intent(this, UriBeaconDiscoveryService.class);
        startService(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    /**
     * Show the fragment scanning nearby UriBeacons.
     */
    private void showNearbyBeaconsFragment(boolean isDemoMode) {
        if (!isDemoMode) {
            // Look for an instance of the nearby beacons fragment
            Fragment nearbyBeaconsFragment = getFragmentManager().findFragmentByTag(NEARBY_BEACONS_FRAGMENT_TAG);
            // If the fragment does not exist
            if (nearbyBeaconsFragment == null) {
                // Create the fragment
                getFragmentManager().beginTransaction()
                        .replace(R.id.main_activity_container, NearbyBeaconsFragment.newInstance(isDemoMode), NEARBY_BEACONS_FRAGMENT_TAG).addToBackStack(null)
                        .commit();
                // If the fragment does exist
            } else {
                // If the fragment is not currently visible
                if (!nearbyBeaconsFragment.isVisible()) {
                    // Assume another fragment is visible, so pop that fragment off the stack
                    getFragmentManager().popBackStack();
                }
            }
        } else {
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_up_fragment, R.anim.fade_out_fragment, R.anim.fade_in_activity, R.anim.fade_out_fragment)
                    .replace(R.id.main_activity_container, NearbyBeaconsFragment.newInstance(isDemoMode))
                    .addToBackStack(null)
                    .commit();
        }
    }

    /**
     * Show the fragment configuring a beacon.
     */
    private void showBeaconConfigFragment() {
        BeaconConfigFragment beaconConfigFragment = BeaconConfigFragment.newInstance();
        getFragmentManager().beginTransaction()
                .replace(R.id.main_activity_container, beaconConfigFragment)
                .addToBackStack(null)
                .commit();
    }
}
