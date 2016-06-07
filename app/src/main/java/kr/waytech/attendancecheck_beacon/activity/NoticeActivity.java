package kr.waytech.attendancecheck_beacon.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import kr.waytech.attendancecheck_beacon.R;
import kr.waytech.attendancecheck_beacon.server.ClassData;
import kr.waytech.attendancecheck_beacon.server.NoticeData;
import kr.waytech.attendancecheck_beacon.server.SelectNoticeDB;

public class NoticeActivity extends AppCompatActivity {
    public static final String INTENT_CLASS = "INTENTCLASSNOTI";
    private static final int GET_STRING = 1;
    private TextView text;
    private TextView tvClass;
    private static int num=0;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        Intent intent = getIntent();

        final ClassData data = (ClassData) intent.getSerializableExtra(INTENT_CLASS);

        Button button = (Button) findViewById(R.id.button);
        text = (TextView) findViewById(R.id.text2);
        tvClass = (TextView) findViewById(R.id.tvClass);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(NoticeActivity.this, NoticeWriteActivity.class);
                in.putExtra(INTENT_CLASS, data);
                startActivityForResult(in, GET_STRING);
            }
        });

        tvClass.setText("--- " + data.getClassName() + " ---");

        if (!intent.getBooleanExtra(ClassListActivity.INTENT_ISEDU, false)) {
            button.setVisibility(View.GONE);
        }

        dialog = new ProgressDialog(this);
        dialog.show();

        new SelectNoticeDB(mHandler).execute(data.getClassName());

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(dialog.isShowing()) dialog.dismiss();
            switch (msg.what) {
                case SelectNoticeDB.HANDLE_SELECT_OK:
                    ArrayList<NoticeData> data = (ArrayList<NoticeData>) msg.obj;
                    String str = "";
                    for(int i = 0; i < data.size() ; i++){
                        num++;
                        str += "[" + (i+1) + "] " + data.get(i).getValue() + "\n";
                    }
                    text.setText(str);
                    break;

                case SelectNoticeDB.HANDLE_SELECT_FAIL:
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String str = data.getStringExtra("INPUT_TEXT");
                String str2 = text.getText().toString();
                num++;
                text.setText(str2 + "[" + num + "] " + str );
            } else {
                text.setText("");
            }
        }
    }
}
