package kr.waytech.attendancecheck_beacon.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import kr.waytech.attendancecheck_beacon.R;
import kr.waytech.attendancecheck_beacon.other.Utils;
import kr.waytech.attendancecheck_beacon.server.InsertUserDB;

/**
 * 회원가입 액티비티
 */
public class SignActivity extends AppCompatActivity {
    private EditText etId;
    private EditText etPassword;
    private EditText etName;
    private RadioGroup radioGroup;
    private Button btnSign;
    private Button btnCancel;
    private String selectType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        findById();
        init();
    }

    private void findById() {
        etId = (EditText) findViewById(R.id.etId);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etName = (EditText) findViewById(R.id.etName);
        btnSign = (Button) findViewById(R.id.btnSign);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
    }

    private void init() {
        btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utils.isOnline(SignActivity.this)) {
                    Toast.makeText(SignActivity.this, "인터넷 상태를 확인해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (etId.getText().length() == 0 || etPassword.getText().length() == 0 ||
                        etName.getText().length() == 0) {
                    Toast.makeText(SignActivity.this, "모두 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                new InsertUserDB(mHandler).execute(etId.getText().toString(), etPassword.getText().toString(),
                        etName.getText().toString(), selectType);

            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbStd:
                        selectType = "학생";
                        break;

                    case R.id.rbEdu:
                        selectType = "교직원";
                        break;
                }
            }
        });
        radioGroup.check(R.id.rbStd);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case InsertUserDB.HANDLE_INSERT_OK:
                    Toast.makeText(SignActivity.this, "가입완료", Toast.LENGTH_SHORT).show();
                    finish();
                    break;

                case InsertUserDB.HANDLE_INSERT_FAIL:
                    Toast.makeText(SignActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


}
