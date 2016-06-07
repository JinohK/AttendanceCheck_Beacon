package kr.waytech.attendancecheck_beacon.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import kr.waytech.attendancecheck_beacon.R;
import kr.waytech.attendancecheck_beacon.other.Utils;

/**
 * Created by JinOh on 2016-05-26.
 * 교직원 액티비티
 */
public class EduActivity extends AppCompatActivity {

    public static final String INTENT_EDU_ATTEND = "INTENTEDU";
    public static final String INTENT_EDU_NOTICE = "INTENTEDU_NOTI";

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
