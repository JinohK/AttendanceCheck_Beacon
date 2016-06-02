package kr.waytech.attendancecheck_beacon.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import kr.waytech.attendancecheck_beacon.R;
import kr.waytech.attendancecheck_beacon.other.AttendListData;
import kr.waytech.attendancecheck_beacon.other.ClassListAdapter;
import kr.waytech.attendancecheck_beacon.other.Utils;
import kr.waytech.attendancecheck_beacon.server.ClassData;
import kr.waytech.attendancecheck_beacon.server.SelectClassDB;
import kr.waytech.attendancecheck_beacon.server.SelectSitDB;

/**
 * Created by Kim-Jinoh on 16. 5. 31..
 */
public class ClassListActivity extends AppCompatActivity {

    private static final String TAG = "ClassListActivity";
    public static String INTENT_CLASS = "INTENTCLASS";
    public static int RESULT_CODE = 34;

    private ListView lvClass;
    private ClassListAdapter classListAdapter;
    private SharedPreferences pref;
    private ProgressDialog progressDialog;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_list);
        findById();
        init();
    }

    private void findById(){
        lvClass = (ListView) findViewById(R.id.lvClass);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
    }

    private void init(){

        pref = getSharedPreferences(getPackageName(), 0);

        final String type = getIntent().getStringExtra(INTENT_CLASS);

        // 강의목록
        if(type == null)
            tvTitle.setText(tvTitle.getText() + " - 과목 선택시 수정");
        // 출결확인
        else if(type.equals(EduActivity.INTENT_EDU))
            tvTitle.setText(tvTitle.getText() + " - 과목 선택시 출결확인");

        lvClass.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ClassData data = classListAdapter.getItem(i);
                // 강의목록을 통해 들어왔을시
                if (type == null) {
                    Intent intent = new Intent(ClassListActivity.this, ClassSetActivity.class);
                    intent.putExtra(INTENT_CLASS, data);
                    startActivityForResult(intent, RESULT_CODE);
                // 출결 - 교수
                } else if (type.equals(EduActivity.INTENT_EDU)) {
                    Intent intent = new Intent(ClassListActivity.this, AttendCheckActivity.class);
                    intent.putExtra(INTENT_CLASS, data);
                    startActivity(intent);
                // 출결 - 학생
                } else if (type.equals(StdActivity.INTENT_STD)) {

                }
            }
        });

        progressDialog = new ProgressDialog(ClassListActivity.this);
        progressDialog.show();
        // 강의목록을 통해왔을때
        if(type == null){
            classListAdapter = new ClassListAdapter(this, ClassListAdapter.TYPE_DELETE);
            new SelectClassDB(mHandler).execute(pref.getString(Utils.PREF_ID, ""));
        }
        // 출결확인 - 교직원
        else if(type.equals(EduActivity.INTENT_EDU)) {
            classListAdapter = new ClassListAdapter(this, ClassListAdapter.TYPE_NORMAL);
            new SelectClassDB(mHandler).execute(pref.getString(Utils.PREF_ID, ""));
        }
        // 출결확인 - 학생
        else if(type.equals(StdActivity.INTENT_STD)){
            classListAdapter = new ClassListAdapter(this, ClassListAdapter.TYPE_NORMAL);
            new SelectSitDB(mHandler).execute(null, pref.getString(Utils.PREF_ID, ""));
        }

        lvClass.setAdapter(classListAdapter);
    }
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(progressDialog.isShowing())
                progressDialog.dismiss();

            switch(msg.what){
                case SelectClassDB.HANDLE_SELECT_OK:
                    ArrayList<ClassData> data = (ArrayList<ClassData>) msg.obj;
                    classListAdapter.setData(data);
                    break;
                case SelectSitDB.HANDLE_SELECT_OK:
                    ArrayList<AttendListData> ary = (ArrayList<AttendListData>) msg.obj;
                    ArrayList<ClassData> classDatas = new ArrayList<>();

                    for(AttendListData d : ary){
                        classDatas.add(new ClassData(d.getClassData().getClassName(), d.getClassData().getClassStart(), d.getClassData().getClassEnd()));
                    }
                    classListAdapter.setData(classDatas);
                    break;

                case SelectSitDB.HANDLE_SELECT_FAIL:
                case SelectClassDB.HANDLE_SELECT_FAIL:
                    Toast.makeText(ClassListActivity.this, "error", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == RESULT_CODE){
            new SelectClassDB(mHandler).execute(pref.getString(Utils.PREF_ID,""));
            classListAdapter.clear();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
