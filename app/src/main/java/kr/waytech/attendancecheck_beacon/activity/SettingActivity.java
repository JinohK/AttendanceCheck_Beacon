package kr.waytech.attendancecheck_beacon.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import kr.waytech.attendancecheck_beacon.R;
import kr.waytech.attendancecheck_beacon.other.Utils;
import kr.waytech.attendancecheck_beacon.service.BeaconService;

public class SettingActivity extends AppCompatActivity {
    private Button btn_logout;
    private SharedPreferences pref;
    private SharedPreferences.Editor edit;
    private TextView tvId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        pref = getSharedPreferences(getPackageName(), 0);
        edit = pref.edit();
        btn_logout = (Button) findViewById(R.id.btn_logout);
        tvId = (TextView) findViewById(R.id.id);
        tvId.setText(pref.getString(Utils.PREF_NAME,""));
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;

                edit.putBoolean(Utils.PREF_AUTOLOGIN, false);
                edit.commit();

                stopService(new Intent(SettingActivity.this, BeaconService.class));
                intent = new Intent(SettingActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                Toast.makeText(SettingActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();

                startActivity(intent);
                finish();
            }
        });

        TextView tvVersion = (TextView) findViewById(R.id.tvVersion);
        PackageInfo pi = null;

        try {
            pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            String verSion = pi.versionName;
            tvVersion.setText("  V " + verSion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


    }

}
