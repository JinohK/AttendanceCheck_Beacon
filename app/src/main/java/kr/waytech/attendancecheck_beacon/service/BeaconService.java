package kr.waytech.attendancecheck_beacon.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

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

    public static final String INTENT_CLASS = "CLASSINTENT";

    private static final String TAG = "BeaconService";
    public static final String BROADCAST_BEACON_IN = "BEACONIN";
    public static final String BROADCAST_BEACON_OUT = "BEACONOUT";

    public static final int HANDLE_TOAST = 134234;

    private static ArrayList<ClassData> classDataArrayList;

    private SharedPreferences pref;

    private Calendar calInTime;
    private Beacon scanBeacon;
    private ClassData scanClass;

    private boolean threadRun;


    // 퇴실 쓰레드 sleep 타임
    private final int TIMEOUT_SLEEP_CYCLE = 5 * 1000;

    // 얼마 동안 비컨이 안잡히면 퇴실조치 할건지
    private final int TIMEOUT_OUT = 10 * 1000;
    private int mTimeout;

    // 입실 / 퇴실 구분 TYPE_IN=입실함 (퇴실체크진행)
    private int mTypeInOut;

    // 입실 했을때의 타입
    private final int TYPE_IN = 1;


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
        mTimeout = 0;
        mTypeInOut = 0;
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
                    Log.d(TAG, "입실");
                    Toast.makeText(getApplicationContext(), "강의실에 들어왔습니다(입실시간 입력)", Toast.LENGTH_SHORT).show();
                    mTypeInOut = TYPE_IN;
                    // 퇴실 체크 쓰레드 실행
                    new Thread(){
                        boolean run = true;
                        @Override
                        public void run() {
                            while (threadRun) {
                                while (run) {
                                    try {
                                        sleep(TIMEOUT_SLEEP_CYCLE);
                                        mTimeout += TIMEOUT_SLEEP_CYCLE;
                                        Log.d(TAG, "TIMEOUT : " + mTimeout);
                                        if (mTimeout == TIMEOUT_OUT) {
                                            Log.d(TAG, "퇴실");
                                            new UpdateAtdDB(mHandler).execute(pref.getString(Utils.PREF_ID, ""), Utils.calToStr(calInTime),
                                                    Utils.calToStr(Calendar.getInstance()), scanClass.getClassName());
                                            stopScan();
                                            run = false;
                                            mTimeout = 0;
                                            mTypeInOut = 0;
                                            calInTime = null;
                                            sendBroadcast(new Intent(BROADCAST_BEACON_OUT));
                                            mHandler.obtainMessage(HANDLE_TOAST, "강의실을 나갔습니다(퇴실시간 입력)").sendToTarget();

                                        }

                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }.start();
                    break;

                case UpdateAtdDB.HANDLE_INSERT_OK:

                    break;

                case InsertAtdDB.HANDLE_INSERT_FAIL:
                    String str = (String) msg.obj;
                    Log.d(TAG, "insertfail : " +str);
                    int index = str.lastIndexOf(";");
                    String subStr = str.substring(index+1, str.length());
                    Log.d(TAG, "insertfail : " + subStr);

                    // 이미 입실시간이 입력되었을시 입실시간 저장
                    if(str.contains("INTIME")){
                        subStr = subStr.replace("[INTIME]", "");
                        Log.d(TAG, "insertfail intime : " + subStr);
                        calInTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(subStr.substring(11, 13)));
                        calInTime.set(Calendar.MINUTE, Integer.parseInt(subStr.substring(14, 16)));
                        calInTime.set(Calendar.SECOND, Integer.parseInt(subStr.substring(17, 19)));

                        mTypeInOut = TYPE_IN;
                        // 퇴실 체크 쓰레드 실행
                        new Thread(){
                            boolean run = true;
                            @Override
                            public void run() {
                                while (threadRun) {
                                    while (run) {
                                        try {
                                            sleep(TIMEOUT_SLEEP_CYCLE);
                                            mTimeout += TIMEOUT_SLEEP_CYCLE;
                                            Log.d(TAG, "TIMEOUT : " + mTimeout);
                                            if (mTimeout == TIMEOUT_OUT) {
                                                Log.d(TAG, "퇴실");
                                                new UpdateAtdDB(mHandler).execute(pref.getString(Utils.PREF_ID, ""), Utils.calToStr(calInTime),
                                                        Utils.calToStr(Calendar.getInstance()), scanClass.getClassName());
                                                stopScan();
                                                run = false;
                                                mTimeout = 0;
                                                mTypeInOut = 0;
                                                calInTime = null;
                                                sendBroadcast(new Intent(BROADCAST_BEACON_OUT));
                                                mHandler.obtainMessage(HANDLE_TOAST, "강의실을 나갔습니다").sendToTarget();
                                            }

                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }.start();
                    }
                    break;

                case HANDLE_TOAST:
                    Toast.makeText(getApplicationContext(), (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;

                case UpdateAtdDB.HANDLE_INSERT_FAIL:
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
                if (mTypeInOut != TYPE_IN) {

                    for (int i = 0; i < list.size(); i++) {
                        Beacon beacon = list.get(i);
                        for (int j = 0; j < classDataArrayList.size(); j++) {
                            ClassData classData = classDataArrayList.get(j);
                            if (equalsBeacon(beacon, classData)) {
                                String id = pref.getString(Utils.PREF_ID, null);
                                String type = pref.getString(Utils.PREF_TYPE, null);
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
                                    for(int z = 0; z < strWeek.length(); z++){
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

                                    if(calInTime == null)
                                        calInTime = Calendar.getInstance();
                                    scanBeacon = beacon;
                                    scanClass = classData;
                                    new InsertAtdDB(mHandler).execute(id, Utils.calToStr(calInTime), classData.getClassName());

                                    Intent intent = new Intent(BROADCAST_BEACON_IN);
                                    intent.putExtra(INTENT_CLASS, classData);
                                    sendBroadcast(intent);





                                } else {
                                    return;
                                }


                            }
                        }
                    }

                }
                // 퇴실
                else {
                    boolean check = false;
                    for(int i = 0 ; i < list.size(); i++){
                        Beacon beacon = list.get(i);
                        if(beacon.equals(scanBeacon)) check = true;
                    }
                    // 비콘이 잡히면 타임아웃을 0으로 초기화
                    if(check){
                        mTimeout = 0;
                        Log.d(TAG, "강의실 안");
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