package kr.waytech.attendancecheck_beacon.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.ArrayList;
import java.util.List;

import kr.waytech.attendancecheck_beacon.server.BeaconData;
import kr.waytech.attendancecheck_beacon.server.SelectBeaconDB;

/**
 * Created by Kim-Jinoh on 16. 5. 20..
 */
public class BeaconService extends Service {
    private BeaconManager beaconManager;
    private Region mRegion;

    private static final String TAG = "BeaconService";
    public static final String BROADCAST_BEACON = "BEACON";

    private static ArrayList<BeaconData> beaconDataArrayList;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("BeaconService", "OnCreate");
        mRegion = new Region("beacon", null, null, null);
        mThread.start();

        startScan();
    }

    private Thread mThread = new Thread(){
        @Override
        public void run() {
            try {
                new SelectBeaconDB(mHandler).execute();
                sleep(1000 * 60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case SelectBeaconDB.HANDLE_SELECT_OK:
                    beaconDataArrayList = (ArrayList<BeaconData>) msg.obj;
                    break;

                case SelectBeaconDB.HANDLE_SELECT_FAIL:

                    break;
            }
        }
    };

    private void startScan(){
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {

                if(list.size() == 0) return;

                for(int i = 0 ; i <list.size(); i++){
                    Beacon beacon = list.get(i);
                    for(int j = 0 ; j < beaconDataArrayList.size() ; j++){
                        BeaconData beaconData = beaconDataArrayList.get(j);
                        if(equalsBeacon(beacon, beaconData)){

                        }
                    }
                }


                Intent intent = new Intent();
                intent.setAction(BROADCAST_BEACON);
                sendBroadcast(intent);
            }
        });

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(mRegion);
            }
        });
    }

    private boolean equalsBeacon(Beacon beacon, BeaconData beaconData){
        if(beacon.getProximityUUID().toString().toUpperCase().equals(beaconData.getUuid().toString().toUpperCase()) &&
                beacon.getMajor() == beaconData.getMajor() &&
                beacon.getMinor() == beaconData.getMinor())
            return true;

        return false;
    }

    private void stopScan() {
        Log.d(TAG, "stopScan()");
        beaconManager.stopRanging(mRegion);

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestory");
        stopScan();
    }


}