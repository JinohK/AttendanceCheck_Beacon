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
import android.widget.Toast;

import java.util.ArrayList;

import kr.waytech.attendancecheck_beacon.R;
import kr.waytech.attendancecheck_beacon.other.ClassListAdapter;
import kr.waytech.attendancecheck_beacon.other.Utils;
import kr.waytech.attendancecheck_beacon.server.ClassData;
import kr.waytech.attendancecheck_beacon.server.SelectClassDB;

/**
 * Created by Kim-Jinoh on 16. 5. 31..
 */
public class ClassListActivity extends AppCompatActivity {

    public static String INTENT_CLASS = "INTENTCLASS";
    public static int RESULT_CODE = 34;

    private ListView lvClass;
    private ClassListAdapter classListAdapter;
    private SharedPreferences pref;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_list);
        findById();
        init();
    }

    private void findById(){
        lvClass = (ListView) findViewById(R.id.lvClass);
    }

    private void init(){
        classListAdapter = new ClassListAdapter(this, ClassListAdapter.TYPE_DELETE);
        lvClass.setAdapter(classListAdapter);
        pref = getSharedPreferences(getPackageName(), 0);

        lvClass.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ClassData data = classListAdapter.getItem(i);
                Intent intent = new Intent(ClassListActivity.this, ClassSetActivity.class);
                intent.putExtra(INTENT_CLASS, data);
                startActivityForResult(intent, RESULT_CODE);
            }
        });

        progressDialog = new ProgressDialog(ClassListActivity.this);
        progressDialog.show();
        new SelectClassDB(mHandler).execute(pref.getString(Utils.PREF_ID,""));
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
