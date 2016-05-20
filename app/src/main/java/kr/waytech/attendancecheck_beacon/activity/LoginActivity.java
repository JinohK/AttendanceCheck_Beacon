package kr.waytech.attendancecheck_beacon.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import kr.waytech.attendancecheck_beacon.R;
import kr.waytech.attendancecheck_beacon.other.Utils;
import kr.waytech.attendancecheck_beacon.server.SelectUserDB;
import kr.waytech.attendancecheck_beacon.server.UserData;

/**
 * 모바일프로그래밍 프로젝트
 * beacon을 이용한 출석체크 앱
 * 로그인 액티비티
 */
public class LoginActivity extends AppCompatActivity {

    private EditText etId;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvSign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findById();
        init();
    }


    private void findById(){
        etId = (EditText) findViewById(R.id.etId);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        tvSign = (TextView) findViewById(R.id.tvSign);
    }

    private void init(){
        tvSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utils.isOnline(LoginActivity.this)){
                    Toast.makeText(LoginActivity.this, "인터넷 상태를 확인해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(etId.getText().length() == 0 || etPassword.getText().length() == 0){
                    Toast.makeText(LoginActivity.this, "빈칸을 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                new SelectUserDB(mHandler).execute(etId.getText().toString(), etPassword.getText().toString());
            }
        });
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case SelectUserDB.HANDLE_SELECT_OK:
                    UserData data = (UserData) msg.obj;
                    if(data.getType().equals("학생"))
                        Toast.makeText(LoginActivity.this, "로그인 성공-학생", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(LoginActivity.this, "로그인 성공-교직원", Toast.LENGTH_SHORT).show();
                    break;

                case SelectUserDB.HANDLE_SELECT_FAIL:
                    Toast.makeText(LoginActivity.this, "아이디와 비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

}