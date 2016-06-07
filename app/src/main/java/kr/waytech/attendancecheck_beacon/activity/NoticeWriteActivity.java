package kr.waytech.attendancecheck_beacon.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import kr.waytech.attendancecheck_beacon.R;
import kr.waytech.attendancecheck_beacon.server.ClassData;
import kr.waytech.attendancecheck_beacon.server.InsertNoticeDB;


public class NoticeWriteActivity extends AppCompatActivity {
    private EditText edit;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_write);

        final ClassData data = (ClassData) getIntent().getSerializableExtra(NoticeActivity.INTENT_CLASS);

        edit = (EditText)findViewById(R.id.editText);
        Button button_ok = (Button)findViewById(R.id.button_ok);
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new ProgressDialog(NoticeWriteActivity.this);
                dialog.show();
                new InsertNoticeDB(mHandler).execute("", edit.getText().toString(), data.getClassName());
            }
        });
        Button button_cancel = (Button)findViewById(R.id.button_cancel);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(dialog.isShowing()) dialog.dismiss();
            switch(msg.what){
                case InsertNoticeDB.HANDLE_INSERT_OK:
                    Intent intent = new Intent();
                    intent.putExtra("INPUT_TEXT", edit.getText().toString());

                    setResult(RESULT_OK, intent);
                    finish();
                    break;

                case InsertNoticeDB.HANDLE_INSERT_FAIL:
                    break;
            }
        }
    };
}
