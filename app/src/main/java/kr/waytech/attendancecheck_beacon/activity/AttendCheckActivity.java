package kr.waytech.attendancecheck_beacon.activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import kr.waytech.attendancecheck_beacon.R;
import kr.waytech.attendancecheck_beacon.other.AttendListAdapter;
import kr.waytech.attendancecheck_beacon.other.AttendListData;
import kr.waytech.attendancecheck_beacon.other.Utils;
import kr.waytech.attendancecheck_beacon.server.AttendData;
import kr.waytech.attendancecheck_beacon.server.ClassData;
import kr.waytech.attendancecheck_beacon.server.SelectAttendDB;
import kr.waytech.attendancecheck_beacon.server.SelectSitDB;

/**
 * Created by Kim-Jinoh on 16. 6. 2..
 * 출석확인 액티비티
 */
public class AttendCheckActivity extends AppCompatActivity {

    private static final String TAG = "AttendCheckActivity";
    private CaldroidFragment caldroidFragment;
    private LinearLayout llCalendar;
    private ListView lvList;
    private AttendListAdapter adapter;
    private TextView tvDate;

    private ClassData classData;
    private ProgressDialog dialog;
    private SharedPreferences pref;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private boolean isStd;

    ArrayList<AttendListData> attendListDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attend_check);
        findById();
        init();
        // Setup caldroid fragment
        caldroidFragment = new CaldroidFragment();
        if (savedInstanceState != null) {
            caldroidFragment.restoreStatesFromKey(savedInstanceState,
                    "CALDROID_SAVED_STATE");
            GridView g = caldroidFragment.getWeekdayGridView();

        } else {
            Bundle args = new Bundle();
            Calendar cal = Calendar.getInstance();
            args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
            args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
            args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
            args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);

            // Uncomment this to customize startDayOfWeek
            // args.putInt(CaldroidFragment.START_DAY_OF_WEEK,
            // CaldroidFragment.TUESDAY); // Tuesday

            // Uncomment this line to use Caldroid in compact mode
//             args.putBoolean(CaldroidFragment.SQUARE_TEXT_VIEW_CELL, false);

            // Uncomment this line to use dark theme
            args.putInt(CaldroidFragment.THEME_RESOURCE, R.style.CaldroidDefaultDark);

            caldroidFragment.setArguments(args);
        }


        // Attach to the activity
        android.support.v4.app.FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.llCalendar, caldroidFragment);
        t.commit();

        final CaldroidListener listener = new CaldroidListener() {

            @Override
            public void onSelectDate(Date date, View view) {
                Calendar selectCalendar = Calendar.getInstance();
                selectCalendar.setTime(date);

                tvDate.setText(dateFormat.format(date));
                dialog = new ProgressDialog(AttendCheckActivity.this);
                dialog.show();
                if (isStd)
                    new SelectAttendDB(mHandler).execute("", dateFormat.format(date), pref.getString(Utils.PREF_ID, ""));
                else
                    new SelectAttendDB(mHandler).execute(classData.getClassName(), dateFormat.format(date));
            }

            @Override
            public void onChangeMonth(int month, int year) {
            }

            @Override
            public void onLongClickDate(Date date, View view) {
            }

            @Override
            public void onCaldroidViewCreated() {
            }

        };
        caldroidFragment.setCaldroidListener(listener);

    }

    private void findById() {
        llCalendar = (LinearLayout) findViewById(R.id.llCalendar);
        lvList = (ListView) findViewById(R.id.lvList);
        tvDate = (TextView) findViewById(R.id.tvDate);
    }

    private void init() {


        pref = getSharedPreferences(getPackageName(), 0);

        classData = (ClassData) getIntent().getSerializableExtra(ClassListActivity.INTENT_CLASS);
        tvDate.setText(dateFormat.format(new Date()));
        dialog = new ProgressDialog(this);
        dialog.show();
        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AttendListData data = adapter.getItem(i);
                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                if (data.getCalIn() != null) {
                    Toast.makeText(AttendCheckActivity.this, "입실: " + format.format(data.getCalIn().getTime()) +
                            " 퇴실: " + format.format(data.getCalOut().getTime()), Toast.LENGTH_SHORT).show();
                }

            }
        });
        if (getIntent().getStringExtra(StdActivity.INTENT_STD) != null) {
            isStd = true;
            new SelectSitDB(mHandler).execute("", pref.getString(Utils.PREF_ID,""));
            Log.d(TAG, "학생");
        } else {
            Log.d(TAG, "교직원");
            isStd = false;
            new SelectSitDB(mHandler).execute(classData.getClassName());
        }

        if(isStd)
            adapter = new AttendListAdapter(this,true);
        else
            adapter = new AttendListAdapter(this);
        lvList.setAdapter(adapter);


    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SelectSitDB.HANDLE_SELECT_OK:
                    ArrayList<AttendListData> data = (ArrayList<AttendListData>) msg.obj;
                    for(AttendListData datas : data) {
                        datas.setSelectCal(tvDate.getText().toString());
                    }
                    if(isStd){
                        attendListDatas = data;
                        new SelectAttendDB(mHandler).execute("",dateFormat.format(new Date()), pref.getString(Utils.PREF_ID, ""));
                    }else {
                        adapter.setData(data);
                        new SelectAttendDB(mHandler).execute(classData.getClassName(), dateFormat.format(new Date()));
                    }
                    break;

                case SelectSitDB.HANDLE_SELECT_FAIL:
                    break;

                case SelectAttendDB.HANDLE_SELECT_OK:
                    ArrayList<AttendData> ary = (ArrayList<AttendData>) msg.obj;
                    adapter.reset();

                    // 학생
                    if (isStd) {
                        ArrayList<AttendListData> attendListData = new ArrayList<>();
                        Log.d(TAG, ary.size() + " " + attendListDatas.size());
                        for(int i = 0 ; i < ary.size() ; i++) {
                            for (int j = 0; j < attendListDatas.size(); j++) {
                                if(ary.get(i).getClassName().equals(attendListDatas.get(j).getClassData().getClassName())) {
                                    Calendar calIn = ary.get(i).getCalIn();
                                    Calendar calOut = ary.get(i).getCalOut();
                                    Calendar calStart = attendListDatas.get(j).getClassData().getCalStart();
                                    Calendar calEnd = attendListDatas.get(j).getClassData().getCalEnd();
                                    calStart.set(Calendar.YEAR, calIn.get(Calendar.YEAR));
                                    calStart.set(Calendar.MONTH, calIn.get(Calendar.MONTH));
                                    calStart.set(Calendar.DATE, calIn.get(Calendar.DATE));
                                    calEnd.set(Calendar.YEAR, calIn.get(Calendar.YEAR));
                                    calEnd.set(Calendar.MONTH, calIn.get(Calendar.MONTH));
                                    calEnd.set(Calendar.DATE, calIn.get(Calendar.DATE));

//                                Log.d(TAG, calIn.get(Calendar.HOUR_OF_DAY) + ":" + calIn.get(Calendar.MINUTE) + "," +
//                                        calStart.get(Calendar.HOUR_OF_DAY) + ":" + calStart.get(Calendar.MINUTE));
//                                Log.d(TAG, calOut.get(Calendar.HOUR_OF_DAY) + ":" + calOut.get(Calendar.MINUTE) + "," +
//                                        calEnd.get(Calendar.HOUR_OF_DAY) + ":" + calEnd.get(Calendar.MINUTE));
//                                Log.d(TAG, calIn.before(calStart) + "" + calOut.after(calEnd));
                                    AttendListData d;
                                    // 퇴실시간없을시
                                    if (Integer.parseInt(ary.get(i).getOutTime().substring(0, 2)) == 0) {
                                        d = new AttendListData(ary.get(i).getUserId(), ary.get(i).getClassName(), android.R.drawable.presence_invisible);
                                        d.setCalIn(ary.get(i).getCalIn());
                                        d.setCalOut(ary.get(i).getCalOut());
                                        attendListData.add(d);
                                        Log.d(TAG, ary.get(i).getOutTime().substring(0, 1));
                                    }
                                    // 정상
                                    else if (calIn.before(calStart) && calOut.after(calEnd)) {
                                        d = new AttendListData(ary.get(i).getUserId(), ary.get(i).getClassName(), android.R.drawable.presence_online);
                                        d.setCalIn(ary.get(i).getCalIn());
                                        d.setCalOut(ary.get(i).getCalOut());
                                        attendListData.add(d);
                                        Log.d(TAG, "ok");
                                    }
                                    // 지각, 출튀
                                    else {
                                        d = new AttendListData(ary.get(i).getUserId(), ary.get(i).getClassName(), android.R.drawable.presence_invisible);
                                        d.setCalIn(ary.get(i).getCalIn());
                                        d.setCalOut(ary.get(i).getCalOut());
                                        attendListData.add(d);
                                        Log.d(TAG, "other");
                                    }
                                }
                            }
                        }

                        adapter.setData(attendListData);
                    }
                    // 교직원
                    else {
                        ArrayList<AttendListData> datas = adapter.getData();
                        for (int i = 0; i < ary.size(); i++) {

                            for (int j = 0; j < datas.size(); j++) {
                                if (ary.get(i).getUserId().equals(datas.get(j).getId())) {
                                    Calendar calIn = ary.get(i).getCalIn();
                                    Calendar calOut = ary.get(i).getCalOut();
                                    Calendar calStart = classData.getCalStart();
                                    Calendar calEnd = classData.getCalEnd();
                                    calStart.set(Calendar.YEAR, calIn.get(Calendar.YEAR));
                                    calStart.set(Calendar.MONTH, calIn.get(Calendar.MONTH));
                                    calStart.set(Calendar.DATE, calIn.get(Calendar.DATE));
                                    calEnd.set(Calendar.YEAR, calIn.get(Calendar.YEAR));
                                    calEnd.set(Calendar.MONTH, calIn.get(Calendar.MONTH));
                                    calEnd.set(Calendar.DATE, calIn.get(Calendar.DATE));

//                                Log.d(TAG, calIn.get(Calendar.HOUR_OF_DAY) + ":" + calIn.get(Calendar.MINUTE) + "," +
//                                        calStart.get(Calendar.HOUR_OF_DAY) + ":" + calStart.get(Calendar.MINUTE));
//                                Log.d(TAG, calOut.get(Calendar.HOUR_OF_DAY) + ":" + calOut.get(Calendar.MINUTE) + "," +
//                                        calEnd.get(Calendar.HOUR_OF_DAY) + ":" + calEnd.get(Calendar.MINUTE));
//                                Log.d(TAG, calIn.before(calStart) + "" + calOut.after(calEnd));
                                    datas.get(j).setCalIn(calIn);
                                    datas.get(j).setCalOut(calOut);
                                    datas.get(j).setSelectCal(tvDate.getText().toString());
                                    // 퇴실시간없을시
                                    if (Integer.parseInt(ary.get(i).getOutTime().substring(0, 2)) == 0) {
                                        datas.get(j).setImage(android.R.drawable.presence_invisible);
                                        Log.d(TAG, ary.get(i).getOutTime().substring(0, 1));
                                    }
                                    // 정상
                                    else if (calIn.before(calStart) && calOut.after(calEnd)) {
                                        datas.get(j).setImage(android.R.drawable.presence_online);
                                        Log.d(TAG, "ok");
                                    }
                                    // 지각, 출튀
                                    else {
                                        datas.get(j).setImage(android.R.drawable.presence_invisible);
                                        Log.d(TAG, "oter");
                                    }
                                    break;

                                }
                            }
                        }

                        adapter.setData(datas);
                    }
                    if (dialog.isShowing()) dialog.dismiss();
                    break;

                case SelectAttendDB.HANDLE_SELECT_FAIL:
                    if (dialog.isShowing()) dialog.dismiss();

                    break;
            }
        }
    };
}
