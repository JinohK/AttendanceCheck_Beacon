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

import kr.waytech.attendancecheck_beacon.other.Utils;
import kr.waytech.attendancecheck_beacon.server.ClassData;
import kr.waytech.attendancecheck_beacon.server.InsertAtdDB;
import kr.waytech.attendancecheck_beacon.server.SelectClassDB;
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
    public static final String INTENT_CLASS_START = "CLASS_START";
    public static final String INTENT_CLASS_END = "CLASS_END";

    private static final String TAG = "BeaconService";
    public static final String BROADCAST_BEACON = "BEACON";

    private static ArrayList<ClassData> classDataArrayList;

    private SharedPreferences pref;

    private Calendar calInTime;
    private Beacon scanBeacon;

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
        classDataArrayList = new ArrayList<>();
        beaconManager = new BeaconManager(this);
    }

    /**
     * 비컨 리스트 업데이트를 위한 쓰레드
     */
    private Thread mThread = new Thread() {
        @Override
        public void run() {
            try {
                while (threadRun) {
                    new SelectClassDB(mHandler).execute();
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
                case SelectClassDB.HANDLE_SELECT_OK:
                    classDataArrayList = (ArrayList<ClassData>) msg.obj;
                    break;

                case InsertAtdDB.HANDLE_INSERT_OK:
                    break;

                case UpdateAtdDB.HANDLE_INSERT_OK:
                    break;


                case UpdateAtdDB.HANDLE_INSERT_FAIL:
                case InsertAtdDB.HANDLE_INSERT_FAIL:
                case SelectClassDB.HANDLE_SELECT_FAIL:

                    break;
            }
        }
    };

    private void startScan() {
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {

                if (list.size() == 0) return;

                // 출입 체크
                if (calInTime == null) {

                    for (int i = 0; i < list.size(); i++) {
                        Beacon beacon = list.get(i);
                        for (int j = 0; j < classDataArrayList.size(); j++) {
                            ClassData classData = classDataArrayList.get(j);
                            if (equalsBeacon(beacon, classData)) {
                                String id = pref.getString(Utils.PREF_ID, null);
                                String type = pref.getString(Utils.PREF_TYPE, null);
                                scanBeacon = beacon;
                                if (type.equals(Utils.USER_STD)) {

                                    Log.d(TAG, "1");
                                    char dayWeek = 'a';
                                    Calendar cal = Calendar.getInstance();
                                    switch (cal.get(Calendar.DAY_OF_WEEK)) {
                                        case 1:
                                            dayWeek = '일';
                                            break;
                                        case 2:
                                            dayWeek = '월';
                                            break;
                                        case 3:
                                            dayWeek = '화';
                                            break;
                                        case 4:
                                            dayWeek = '수';
                                            break;
                                        case 5:
                                            dayWeek = '목';
                                            break;
                                        case 6:
                                            dayWeek = '금';
                                            break;
                                        case 7:
                                            dayWeek = '토';
                                            break;
                                    }

                                    // 현재 요일과 강의 요일 비교
                                    String strWeek = classData.getClassDayWeek();
                                    boolean check = false;
                                    for(int z = 0; i < strWeek.length(); z++){
                                        char str = strWeek.charAt(z);
                                        if(str == dayWeek){
                                            check = true;
                                            break;
                                        }
                                    }
                                    if(!check) return;

                                    Log.d(TAG, "2");
                                    // 현재 시간과 강의 시간 비교
                                    Log.d(TAG, "start : " + Utils.calToStr(classData.getCalStart()) +" "+ Utils.calToStr(cal));
                                    if (classData.getCalStart().after(cal)) return;
                                    Log.d(TAG, "3");
                                    Log.d(TAG, "end : " + Utils.calToStr(classData.getCalEnd()) +" "+ Utils.calToStr(cal));
                                    if(classData.getCalEnd().before(cal)) return;
                                    Log.d(TAG, "4");

                                    calInTime = Calendar.getInstance();
                                    new InsertAtdDB(mHandler).execute(id, Utils.calToStr(calInTime), classData.getClassName());

                                    Intent intent = new Intent(BROADCAST_BEACON);
                                    intent.putExtra(INTENT_CLASS_NAME, classData.getClassName());
                                    intent.putExtra(INTENT_CLASS_NUMBER, classData.getClassNumber());
                                    intent.putExtra(INTENT_CLASS_START, classData.getCalStart());
                                    intent.putExtra(INTENT_CLASS_END, classData.getCalEnd());
                                    sendBroadcast(intent);
                                    stopScan();

                                } else {
                                    return;
                                }


                            }
                        }
                    }

                }
                // 퇴실 체크
                else {

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
     * @param classData
     * @return boolean
     */
    private boolean equalsBeacon(Beacon beacon, ClassData classData) {
        if (beacon.getProximityUUID().toString().toUpperCase().equals(classData.getUuid().toString().toUpperCase()) &&
                beacon.getMajor() == classData.getMajor() &&
                beacon.getMinor() == classData.getMinor())
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