package kr.waytech.attendancecheck_beacon.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

import kr.waytech.attendancecheck_beacon.R;
import kr.waytech.attendancecheck_beacon.other.Utils;
import kr.waytech.attendancecheck_beacon.service.BeaconService;

/**
 * Created by JinOh on 2016-05-26.
 * 학생 액티비티
 */
public class StdActivity extends AppCompatActivity {


    private TextView tvClass;
    private Button btnSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_std);

        // 리시버 등록
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BeaconService.BROADCAST_BEACON);
        registerReceiver(receiver, intentFilter);

        findById();
        init();

        // 블루투스 마시멜로우 권한 대응
//        new TedPermission(this)
//                .setPermissionListener(new PermissionListener() {
//                    @Override
//                    public void onPermissionGranted() {
//
//                    }
//
//                    @Override
//                    public void onPermissionDenied(ArrayList<String> arrayList) {
//
//                    }
//                })
//                .setDeniedMessage("블루투스 사용을 위해 권한이 필요합니다\n\n[앱설정] > [권한]에 가셔서 권한 승인을 부탁드립니다")
//                .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
//                .check();

        startService(new Intent(StdActivity.this, BeaconService.class));
    }

    private void findById(){
        tvClass = (TextView) findViewById(R.id.tvClass);
        btnSetting = (Button) findViewById(R.id.btnSetting);
    }

    private void init(){
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StdActivity.this, SettingActivity.class));
            }
        });
    }


    // 비컨 리시버
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(BeaconService.BROADCAST_BEACON)){
                String className = intent.getStringExtra(BeaconService.INTENT_CLASS_NAME);
                String classNumber = intent.getStringExtra(BeaconService.INTENT_CLASS_NUMBER);
                Calendar calStart = (Calendar) intent.getSerializableExtra(BeaconService.INTENT_CLASS_START);
                Calendar calEnd = (Calendar) intent.getSerializableExtra(BeaconService.INTENT_CLASS_END);

                String strStart = calStart.get(Calendar.HOUR_OF_DAY) + ":" + calStart.get(Calendar.MINUTE);
                String strEnd = calEnd.get(Calendar.HOUR_OF_DAY) + ":" + calEnd.get(Calendar.MINUTE);

                String str = "현재 강의실 : " +className + "(" + classNumber + ")" + " " + strStart + "~" + strEnd;
                tvClass.setText(str);
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Utils.closePopup(this);
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

    }



}
