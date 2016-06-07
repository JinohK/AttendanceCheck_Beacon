package kr.waytech.attendancecheck_beacon.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import kr.waytech.attendancecheck_beacon.R;
import kr.waytech.attendancecheck_beacon.other.CheckPermission;
import kr.waytech.attendancecheck_beacon.other.MyBluetoothManager;
import kr.waytech.attendancecheck_beacon.other.Utils;

/**
 * Created by JinOh on 2016-05-26.
 * 교직원 액티비티
 */
public class EduActivity extends AppCompatActivity {

    public static final String INTENT_EDU_ATTEND = "INTENTEDU";
    public static final String INTENT_EDU_NOTICE = "INTENTEDU_NOTI";
    private static final String TAG = "EduActivity";

    private ImageView btnSetting;
    private ImageView btnReg;
    private ImageView btnList;
    private ImageView btnAttend;
    private ImageView btnNotice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_edu);
        findById();
        init();
    }

    private void findById(){
        btnSetting = (ImageView) findViewById(R.id.btnSetting);
        btnReg = (ImageView) findViewById(R.id.btnClassReg);
        btnList = (ImageView)findViewById(R.id.btnClassList);
        btnAttend = (ImageView) findViewById(R.id.btnAttend);
        btnNotice = (ImageView) findViewById(R.id.btnNotice);
    }

    private void init(){

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EduActivity.this, SettingActivity.class));
            }
        });

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EduActivity.this, ClassSetActivity.class));
            }
        });

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EduActivity.this, ClassListActivity.class));
            }
        });

        btnAttend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EduActivity.this, ClassListActivity.class);
                intent.putExtra(ClassListActivity.INTENT_CLASS, INTENT_EDU_ATTEND);
                startActivity(intent);
            }
        });

        btnNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EduActivity.this, ClassListActivity.class);
                intent.putExtra(ClassListActivity.INTENT_CLASS, INTENT_EDU_NOTICE);
                startActivity(intent);
            }
        });


        // 마시멜로우 권한체크
        if(!CheckPermission.checkSelfPermission(this)){
            CheckPermission.requestPermission(this);
            Log.d(TAG, "need permission");
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case CheckPermission.PERMISSIONS_REQUEST:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 권한 허가
                } else {
                    // 권한 거부
                    Toast.makeText(EduActivity.this, "블루투스 사용을 위해 권한이 필요합니다 \n\n[앱설정]->[권한]에서 권한승인을 해주세요", Toast.LENGTH_SHORT).show();
                }
                return;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!MyBluetoothManager.isBtModule())
            Toast.makeText(EduActivity.this, "블루투스를 지원하지 않는 기기입니다.", Toast.LENGTH_SHORT).show();
        else if(!MyBluetoothManager.isBtEnabled()){
            MyBluetoothManager.RequestBtOn(this);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Utils.closePopup(this);
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
