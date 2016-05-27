package kr.waytech.attendancecheck_beacon.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import kr.waytech.attendancecheck_beacon.activity.StdActivity;
import kr.waytech.attendancecheck_beacon.other.Utils;
import kr.waytech.attendancecheck_beacon.server.BeaconData;
import kr.waytech.attendancecheck_beacon.server.InsertAtdDB;
import kr.waytech.attendancecheck_beacon.server.SelectBeaconDB;
import kr.waytech.attendancecheck_beacon.server.UpdateAtdDB;

/**
 * Created by Kim-Jinoh on 16. 5. 20..
 * 비컨 서비스
 */
public class BeaconService extends Service {
    private BeaconManager beaconManager;
    private Region mRegion;

    public static final String INTENT_CLASS_NAME = "CLASS_NAME";
    public static final String INTENT_CLASS_NUMBER = "CLASS_NUMBER";

    private static final String TAG = "BeaconService";
    public static final String BROADCAST_BEACON = "BEACON";

    private static ArrayList<BeaconData> beaconDataArrayList;

    private SharedPreferences pref;

    private Calendar calInTime;
    private BeaconData scanBeacon;

    private boolean threadRun;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "OnCreate");
        mRegion = new Region("beacon", null, null, null);
        threadRun = true;
        mThread.start();
        pref = getSharedPreferences(getPackageName(), 0);
        calInTime = null;
        beaconDataArrayList = new ArrayList<>();
    }

    /**
     * 비컨 리스트 업데이트를 위한 쓰레드
     */
    private Thread mThread = new Thread() {
        @Override
        public void run() {
            try {
                while (threadRun) {
                    new SelectBeaconDB(mHandler).execute();
                    sleep(1000 * 60);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SelectBeaconDB.HANDLE_SELECT_OK:
                    beaconDataArrayList = (ArrayList<BeaconData>) msg.obj;
                    break;

                case InsertAtdDB.HANDLE_INSERT_OK:
                    break;

                case UpdateAtdDB.HANDLE_INSERT_OK:
                    break;


                case UpdateAtdDB.HANDLE_INSERT_FAIL:
                case InsertAtdDB.HANDLE_INSERT_FAIL:
                case SelectBeaconDB.HANDLE_SELECT_FAIL:

                    break;
            }
        }
    };

    private void startScan() {
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {

                if (list.size() == 0) return;

                for (int i = 0; i < list.size(); i++) {
                    Beacon beacon = list.get(i);
                    for (int j = 0; j < beaconDataArrayList.size(); j++) {
                        BeaconData beaconData = beaconDataArrayList.get(j);
                        if (equalsBeacon(beacon, beaconData)) {
                            String id = pref.getString(Utils.PREF_ID, null);
                            String type = pref.getString(Utils.PREF_TYPE, null);
                            scanBeacon = beaconData;
                            if (type.equals(Utils.USER_STD)) {

                                if (calInTime == null) {
                                    calInTime = Calendar.getInstance();
                                    new InsertAtdDB(mHandler).execute(id, Utils.calToStr(calInTime), beaconData.getClassName());
                                }

                                Intent intent = new Intent();
                                intent.setAction(StdActivity.RECEIVER_BEACON);
                                intent.putExtra(INTENT_CLASS_NAME, beaconData.getClassName());
                                intent.putExtra(INTENT_CLASS_NUMBER, beaconData.getClassNumber());
                                sendBroadcast(intent);

                            } else {
                                return;
                            }


                        }
                    }
                }


            }
        });

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(mRegion);
            }
        });
    }


    /**
     * 같은 비컨인지 체크
     *
     * @param beacon
     * @param beaconData
     * @return boolean
     */
    private boolean equalsBeacon(Beacon beacon, BeaconData beaconData) {
        if (beacon.getProximityUUID().toString().toUpperCase().equals(beaconData.getUuid().toString().toUpperCase()) &&
                beacon.getMajor() == beaconData.getMajor() &&
                beacon.getMinor() == beaconData.getMinor())
            return true;

        return false;
    }


    /**
     * 비컨 스캔 중지
     */
    private void stopScan() {
        Log.d(TAG, "stopScan()");
        beaconManager.stopRanging(mRegion);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startScan();
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestory");
        stopScan();
        threadRun = false;
    }


}