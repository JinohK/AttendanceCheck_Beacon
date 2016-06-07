package kr.waytech.attendancecheck_beacon.server;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 진오 on 16. 6. 07..
 * 공지 등록
 * 타이틀 / 내용 / 강의실
 */
public class InsertNoticeDB extends AsyncTask<String, Integer, String> {
    public static final String strUrl = "http://waytech.kr/AtdCheck/insertNotice.php";
    public static final int HANDLE_INSERT_FAIL = 5345;
    public static final int HANDLE_INSERT_OK = 234;

    private final String TAG = "InsertNoticeDB";
    private Handler mHandler;

    private boolean isError;


    public InsertNoticeDB(Handler mHandler) {
        this.mHandler = mHandler;
        isError = false;
    }

    @Override
    protected String doInBackground(String... v) {
        StringBuilder jsonHtml = new StringBuilder();
        try {
            // 연결 url 설정
            URL url = new URL(strUrl);
            // 커넥션 객체 생성
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();


            // 연결되었으면.
            if (conn != null) {
                conn.setConnectTimeout(10000);
                conn.setUseCaches(false);

                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.connect();


                OutputStream outputStream = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(outputStream, "UTF-8"));
                v[1] = v[1].replace("+", "%2B");
                writer.write("title=" + v[0]);
                writer.write("&value=" + v[1]);
                writer.write("&class=" + v[2]);

                writer.flush();
                writer.close();
                outputStream.close();


                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    for (; ; ) {
                        // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
                        String line = br.readLine();
                        if (line == null) break;
                        jsonHtml.append(line + "\n");
                    }
                    br.close();
                    Log.d(TAG, jsonHtml.toString());
                }

                conn.disconnect();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            isError = true;
        }
        return jsonHtml.toString();

    }

    protected void onPostExecute(String str) {

        if (str.contains("ERROR")) isError = true;


        if (!isError) {
            mHandler.sendEmptyMessage(HANDLE_INSERT_OK);
        } else {
            mHandler.obtainMessage(HANDLE_INSERT_FAIL, str).sendToTarget();
        }
    }

}