package kr.waytech.attendancecheck_beacon.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import kr.waytech.attendancecheck_beacon.R;
import kr.waytech.attendancecheck_beacon.other.Utils;
import kr.waytech.attendancecheck_beacon.server.ClassData;
import kr.waytech.attendancecheck_beacon.service.BeaconService;

/**
 * Created by JinOh on 2016-05-26.
 * 학생 액티비티
 */
public class StdActivity extends AppCompatActivity {


    public static final String INTENT_STD = "INTENTSTD";
    private TextView tvClass;
    private ImageView btnSetting;
    private ImageView btnAttend;
    private ImageView btnNotice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_std);

        // 리시버 등록
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BeaconService.BROADCAST_BEACON_IN);
        intentFilter.addAction(BeaconService.BROADCAST_BEACON_OUT);
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
        btnSetting = (ImageView) findViewById(R.id.btnSetting);
        btnAttend = (ImageView) findViewById(R.id.btnAttend);
        btnNotice = (ImageView) findViewById(R.id.btnNotice);
    }

    private void init(){
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StdActivity.this, SettingActivity.class));
            }
        });

        btnAttend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StdActivity.this, AttendCheckActivity.class);
                intent.putExtra(INTENT_STD, INTENT_STD);
                startActivity(intent);
            }
        });
        btnNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StdActivity.this, ClassListActivity.class);
                intent.putExtra(ClassListActivity.INTENT_CLASS, INTENT_STD);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    // 비컨 리시버
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(BeaconService.BROADCAST_BEACON_IN)){
                ClassData data = (ClassData) intent.getSerializableExtra(BeaconService.INTENT_CLASS);
                String str = "강  의  실 : " + data.getClassName() + "(" + data.getClassNumber() + ")" + "\n";
                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                str += "강의시간 : " + formatter.format(data.getCalStart().getTime());
                str += "~" + formatter.format(data.getCalEnd().getTime()) + "\n";
                str += "담당교수 : " + data.getClassEduName();
                tvClass.setText(str);
            }else if(intent.getAction().equals(BeaconService.BROADCAST_BEACON_OUT)){
                tvClass.setText("강  의  실 : ");
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
