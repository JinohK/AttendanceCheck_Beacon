package kr.waytech.attendancecheck_beacon.activity;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import kr.waytech.attendancecheck_beacon.R;
import kr.waytech.attendancecheck_beacon.other.BeaconListAdapter;
import kr.waytech.attendancecheck_beacon.other.BeaconListData;
import kr.waytech.attendancecheck_beacon.other.Utils;
import kr.waytech.attendancecheck_beacon.server.ClassData;
import kr.waytech.attendancecheck_beacon.server.InsertClassDB;
import kr.waytech.attendancecheck_beacon.server.UpdateClassDB;

/**
 * Created by Kim-Jinoh on 16. 5. 30..
 */
public class ClassSetActivity extends AppCompatActivity {

    private static final String TAG = "ClassSetActivity";
    private EditText etClassName;
    private EditText etClassNumber;
    private Button btnStartTime;
    private Button btnEndTime;
    private TextView tvStartTime;
    private TextView tvEndTime;
    private CheckBox cbMonth;
    private CheckBox cbTuesday;
    private CheckBox cbWednesday;
    private CheckBox cbThursday;
    private CheckBox cbFriday;
    private Button btnReg;
    private Button btnCancel;
    private ListView lvBeacon;
    private BeaconListAdapter beaconListAdapter;

    private BeaconManager beaconManager;
    private Region mRegion;

    private String strStartTime = "", strEndTime = "";

    private boolean isUpdate = false;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_set);
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
//                .setRationaleMessage("블루투스 사용을 위해 다음과 같은 권한이 필요합니다.")
//                .setDeniedMessage("블루투스 사용을 위해 권한이 필요합니다\n\n[앱설정] > [권한]에 가셔서 권한 승인해주세요")
//                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION,
//                        Manifest.permission.ACCESS_FINE_LOCATION)
//                .check();


        // 강의 목록을 통해 수정으로 들어왔을때 선택한 강의 데이터를 셋팅
        Intent intent = getIntent();
        ClassData data = (ClassData) intent.getSerializableExtra(ClassListActivity.INTENT_CLASS);
        if (data != null) {
            etClassName.setText(data.getClassName());
            etClassNumber.setText(data.getClassNumber());
            isUpdate = true;
            strStartTime = data.getClassStart().substring(0, data.getClassStart().length() - 3);
            strEndTime = data.getClassEnd().substring(0, data.getClassEnd().length() - 3);
            tvStartTime.setText(strStartTime);
            tvEndTime.setText(strEndTime);
            for (int i = 0; i < data.getClassDayWeek().length(); i++) {
                switch (data.getClassDayWeek().charAt(i)) {
                    case '월':
                        cbMonth.setChecked(true);
                        break;
                    case '화':
                        cbTuesday.setChecked(true);
                        break;
                    case '수':
                        cbWednesday.setChecked(true);
                        break;
                    case '목':
                        cbThursday.setChecked(true);
                        break;
                    case '금':
                        cbFriday.setChecked(true);
                        break;
                }
            }
            beaconListAdapter.setData(new BeaconListData(data.getUuid(), data.getMajor(), data.getMinor(), 0));
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    while (lvBeacon.getChildCount() == 0) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    beaconListAdapter.setSelectPos(0);
                    lvBeacon.getChildAt(0).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }
            });

            etClassName.setEnabled(false);
        }

    }

    private void findById() {
        etClassName = (EditText) findViewById(R.id.etClassName);
        etClassNumber = (EditText) findViewById(R.id.etClassNumber);
        btnStartTime = (Button) findViewById(R.id.btnStartTime);
        btnEndTime = (Button) findViewById(R.id.btnEndTime);
        cbMonth = (CheckBox) findViewById(R.id.cbMonDay);
        cbTuesday = (CheckBox) findViewById(R.id.cbTuesday);
        cbWednesday = (CheckBox) findViewById(R.id.cbWednesday);
        cbThursday = (CheckBox) findViewById(R.id.cbThursday);
        cbFriday = (CheckBox) findViewById(R.id.cbFriday);
        btnReg = (Button) findViewById(R.id.btnReg);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        lvBeacon = (ListView) findViewById(R.id.lvBeacon);
        tvStartTime = (TextView) findViewById(R.id.tvStartTime);
        tvEndTime = (TextView) findViewById(R.id.tvEndTime);
    }

    private void init() {
        mRegion = new Region("beacon", null, null, null);
        beaconManager = new BeaconManager(this);

        beaconListAdapter = new BeaconListAdapter();
        lvBeacon.setAdapter(beaconListAdapter);

        lvBeacon.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                beaconListAdapter.setSelectPos(i);
                lvBeacon.getChildAt(0).setBackgroundColor(Color.argb(0, 0, 0, 0));
            }
        });

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {

                if (list.size() == 0) return;

                for (Beacon beacon : list) {
                    BeaconListData data = new BeaconListData(beacon.getProximityUUID().toString().toUpperCase(),
                            beacon.getMajor(), beacon.getMinor(), beacon.getRssi());
                    beaconListAdapter.setData(data);
                }
            }
        });

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(mRegion);
            }
        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isAllWirte()) {
                    Toast.makeText(ClassSetActivity.this, "모든칸을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                SharedPreferences pref;
                pref = getSharedPreferences(getPackageName(), 0);

                String week = "";
                if (cbMonth.isChecked()) week += "월";
                if (cbTuesday.isChecked()) week += "화";
                if (cbWednesday.isChecked()) week += "수";
                if (cbThursday.isChecked()) week += "목";
                if (cbFriday.isChecked()) week += "금";

                BeaconListData listData = beaconListAdapter.getItem(beaconListAdapter.getSelectPos());
                // 아이디 / 강의명 / 강의실 / 강의요일 / 강의시작시간 / 강의종료시간 / uuid / major/ minor

                progressDialog = new ProgressDialog(ClassSetActivity.this);
                progressDialog.show();
                if (isUpdate) {
                    new UpdateClassDB(mHandler).execute(pref.getString(Utils.PREF_ID, ""), etClassName.getText().toString(),
                            etClassNumber.getText().toString(), week, strStartTime + ":00", strEndTime + ":00",
                            listData.getUuid(), listData.getMajor() + "", listData.getMinor() + "");
                } else {
                    new InsertClassDB(mHandler).execute(pref.getString(Utils.PREF_ID, ""), etClassName.getText().toString(),
                            etClassNumber.getText().toString(), week, strStartTime + ":00", strEndTime + ":00",
                            listData.getUuid(), listData.getMajor() + "", listData.getMinor() + "");
                }


            }
        });

        btnStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TimePickerDialog dialog = new TimePickerDialog(ClassSetActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        strStartTime = i + ":" + i1;
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.HOUR_OF_DAY, i);
                        cal.set(Calendar.MINUTE, i1);
                        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                        tvStartTime.setText(formatter.format(cal.getTime()));
                    }
                }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), false);
                dialog.show();
            }
        });

        btnEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog dialog = new TimePickerDialog(ClassSetActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        strEndTime = i + ":" + i1;
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.HOUR_OF_DAY, i);
                        cal.set(Calendar.MINUTE, i1);
                        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                        tvEndTime.setText(formatter.format(cal.getTime()));
                    }
                }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), false);
                dialog.show();
            }
        });
    }

    /**
     * 데이터 모두 입력했는지 확인
     *
     * @return boolean
     */
    private boolean isAllWirte() {
        if (etClassNumber.length() == 0) return false;
        if (etClassName.length() == 0) return false;
        if (!cbMonth.isChecked() && !cbTuesday.isChecked() && !cbWednesday.isChecked() &&
                !cbThursday.isChecked() && !cbFriday.isChecked()) return false;
        if (beaconListAdapter.getSelectPos() == -1) return false;
        if (strStartTime.length() == 0 || strEndTime.length() == 0) return false;

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.stopRanging(mRegion);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (progressDialog.isShowing())
                progressDialog.dismiss();

            switch (msg.what) {
                case InsertClassDB.HANDLE_INSERT_OK:
                    Toast.makeText(ClassSetActivity.this, "등록 완료", Toast.LENGTH_SHORT).show();
                    finish();
                    break;

                case UpdateClassDB.HANDLE_INSERT_OK:
                    setResult(RESULT_OK);
                    Toast.makeText(ClassSetActivity.this, "수정 완료", Toast.LENGTH_SHORT).show();
                    finish();
                    break;

                case UpdateClassDB.HANDLE_INSERT_FAIL:
                case InsertClassDB.HANDLE_INSERT_FAIL:
                    Toast.makeText(ClassSetActivity.this, "error", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

}
