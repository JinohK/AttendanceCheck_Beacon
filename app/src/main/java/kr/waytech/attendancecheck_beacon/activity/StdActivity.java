package kr.waytech.attendancecheck_beacon.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import kr.waytech.attendancecheck_beacon.R;
import kr.waytech.attendancecheck_beacon.service.BeaconService;

/**
 * Created by JinOh on 2016-05-26.
 * 학생 액티비티
 */
public class StdActivity extends AppCompatActivity {

    public static final String RECEIVER_BEACON = "RECEV_BEACON";

    private TextView tvClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_std);

        // 리시버 등록
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BeaconService.BROADCAST_BEACON);
        registerReceiver(receiver, intentFilter);

        findById();
    }

    private void findById(){
        tvClass = (TextView) findViewById(R.id.tvClass);
    }



    // 비컨 리시버
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(BeaconService.BROADCAST_BEACON)){
                String className = intent.getStringExtra(BeaconService.INTENT_CLASS_NAME);
                int classNumber = intent.getIntExtra(BeaconService.INTENT_CLASS_NUMBER, -1);

                String str = className + "(" + classNumber + ")";
                tvClass.setText(str);
            }
        }
    };
}
