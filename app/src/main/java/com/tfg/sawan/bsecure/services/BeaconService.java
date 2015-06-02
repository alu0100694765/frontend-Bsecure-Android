package com.tfg.sawan.bsecure.services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.os.SystemClock;
import android.util.Log;

import com.naddiaz.tfg.physicalweblibrary.beacon.ConfigUriBeacon;
import com.naddiaz.tfg.physicalweblibrary.beacon.UriBeacon;
import com.naddiaz.tfg.physicalweblibrary.config.UriBeaconConfig;
import com.naddiaz.tfg.physicalweblibrary.utils.ScanRecord;
import com.naddiaz.tfg.physicalweblibrary.utils.ScanResult;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nad on 1/03/15.
 */
public class BeaconService extends Service {


    private static final byte TX_POWER_DEFAULT = -22;
    private static final String TAG = "UriBeaconScan";
    public static final ParcelUuid CONFIG_SERVICE_UUID_V1 = ParcelUuid
            .fromString("b35d7da6-eed4-4d59-8f89-f6573edea967");
    public static final ParcelUuid CONFIG_SERVICE_UUID_V2 = ParcelUuid.fromString(
            "ee0c2080-8786-40ba-ab96-99b91ac981d8");
    private static final Handler mHandler = new Handler();
    private final BluetoothAdapter.LeScanCallback mLeScanCallbackRead = new LeScanCallbackRead();
    private BluetoothAdapter mBluetoothAdapter;
    private long mScanTime = 10000;
    private HashMap<BluetoothDevice, ScanResult> scanResult;
    private CountDownTimer countDown;
    private static final ParcelUuid[] mScanFilterUuids = new ParcelUuid[]{CONFIG_SERVICE_UUID_V2, CONFIG_SERVICE_UUID_V1};
    private UriBeaconConfig mUriBeaconConfig;

    private final UriBeaconConfig.UriBeaconCallback mUriBeaconCallback = new UriBeaconConfig.UriBeaconCallback() {
        @Override
        public void onUriBeaconRead(ConfigUriBeacon configUriBeacon, int status) {

        }

        @Override
        public void onUriBeaconWrite(int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                mUriBeaconConfig.closeUriBeacon();
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        scanResult = new HashMap<>();
        scanLeDevice(true,false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"on Destroy");
        scanLeDevice(false,true);
        countDown.cancel();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Runnable mScan = new Runnable() {
        @Override
        public void run() {
            scanLeDevice(false,false);
        }
    };

    public void sender(){
        if(scanResult != null) {
            for(final Map.Entry<BluetoothDevice, ScanResult> beacon : scanResult.entrySet()){
                new Runnable() {
                    @Override
                    public void run() {
                        //WSBeacons sendBeacons = new WSBeacons(getApplicationContext(), beacon.getKey(), beacon.getValue().getRssi(), beacon.getValue().getTimestampNanos());
                        //sendBeacons.postLocation();
                        Log.i(TAG, "DISCOVERED : " + beacon);
                        UriBeacon uriBeacon = UriBeacon.parseFromBytes(beacon.getValue().getScanRecord().getBytes());
                        if( uriBeacon != null) {
                            Log.i(TAG, "ADDRESS : " + beacon.getValue().getDevice().getAddress());
                            Log.i(TAG, "RSSI : " + beacon.getValue().getRssi());
                            Log.i(TAG, "TXPOWER : " + uriBeacon.getTxPowerLevel());
                            Log.i(TAG, "URL : " + uriBeacon.getUriString());
                        }
                    }
                }.run();
            }
        }
        scanLeDevice(true,false);
    }

    @SuppressWarnings("deprecation")
    private void scanLeDevice(final boolean enable, boolean kill) {
        if (enable) {
            mHandler.postDelayed(mScan, mScanTime);
            mBluetoothAdapter.startLeScan(mLeScanCallbackRead);
        } else if(!kill){
            // Cancel the scan timeout callback if still active or else it may fire later.
            mHandler.removeCallbacks(mScan);
            mBluetoothAdapter.stopLeScan(mLeScanCallbackRead);
            countDown = new CountDownTimer(5000, 1000) {

                public void onTick(long millisUntilFinished) {
                    //Log.i(TAG, "WAIT: " + millisUntilFinished);
                }

                public void onFinish() {
                    sender();
                }
            };
            countDown.start();
        }
        else{
            // Cancel the scan timeout callback if still active or else it may fire later.
            mHandler.removeCallbacks(mScan);
            mBluetoothAdapter.stopLeScan(mLeScanCallbackRead);
        }
    }


    /**
     * Callback for LE scan results.
     */
    private class LeScanCallbackRead implements BluetoothAdapter.LeScanCallback {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanBytes) {

            ScanRecord record = ScanRecord.parseFromBytes(scanBytes);
            ScanResult result = new ScanResult(device, record, rssi, SystemClock.elapsedRealtimeNanos());
            if(scanResult.containsKey(device)){
                scanResult.remove(device);
                scanResult.put(device, result);
            }
            else{
                scanResult.put(device,result);
            }
            ParcelUuid filteredUuid = leScanMatches(record);
            if (filteredUuid != null) {
                /*mUriBeaconConfig = new UriBeaconConfig(getBaseContext(), mUriBeaconCallback, filteredUuid);
                if (mUriBeaconConfig != null) {
                    mUriBeaconConfig.connectUriBeacon(result.getDevice());

                    try {
                        ConfigUriBeacon configUriBeacon = new ConfigUriBeacon.Builder()
                                .uriString("http://hello.com")
                                .txPowerLevel(Byte.parseByte("-22"))
                                .build();
                        Log.i(TAG,configUriBeacon.toString());
                        mUriBeaconConfig.writeUriBeacon(configUriBeacon);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }*/
                Log.i(TAG,"CONFIG UUID: " + filteredUuid.toString());
            }
        }
    }

    private ParcelUuid leScanMatches(ScanRecord scanRecord) {
        List services = scanRecord.getServiceUuids();
        if (services != null) {
            for (ParcelUuid uuid : mScanFilterUuids) {
                if (services.contains(uuid)) {
                    return uuid;
                }
            }
        }
        return null;
    }

    class UriBeaconConfigCallback implements UriBeaconConfig.UriBeaconCallback {

        @Override
        public void onUriBeaconRead(ConfigUriBeacon configUriBeacon, int status) {
            Log.i(TAG,"READ : " + configUriBeacon + ", " + status);
        }

        @Override
        public void onUriBeaconWrite(int status) {
            Log.i(TAG, "WRITE : " + status);
        }
    }
}
