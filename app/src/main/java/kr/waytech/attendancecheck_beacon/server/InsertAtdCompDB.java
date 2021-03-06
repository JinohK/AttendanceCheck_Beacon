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
 * Created by 진오 on 16. 6. 8..
 * 강제 출석체크
 * 아이디, 강의이름, 날짜
 */
public class InsertAtdCompDB extends AsyncTask<String, Integer, String> {
    public static final String strUrl = "http://waytech.kr/AtdCheck/insertAtdComp.php";
    public static final int HANDLE_INSERT_FAIL = 6765;
    public static final int HANDLE_INSERT_OK = 6484;

    private final String TAG = "InsertAtdCompDB";
    private Handler mHandler;

    private boolean isError;


    public InsertAtdCompDB(Handler mHandler) {
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
                writer.write("id=" + v[0]);
                writer.write("&class=" + v[1]);
                writer.write("&date=" + v[2]);


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

        if (str.contains("ERROR") || str.contains("INTIME"))
            isError = true;


        if (!isError) {
            mHandler.sendEmptyMessage(HANDLE_INSERT_OK);
        } else {
            mHandler.obtainMessage(HANDLE_INSERT_FAIL, str).sendToTarget();
        }
    }

}