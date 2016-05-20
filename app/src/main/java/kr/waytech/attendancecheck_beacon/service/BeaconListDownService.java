package kr.waytech.attendancecheck_beacon.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import kr.waytech.attendancecheck_beacon.server.SelectBeaconDB;

/**
 * Created by Kim-Jinoh on 16. 5. 20..
 */
public class BeaconListDownService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mThread.start();
        return super.onStartCommand(intent, flags, startId);
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

                    break;

                case SelectBeaconDB.HANDLE_SELECT_FAIL:

                    break;
            }
        }
    };
}
