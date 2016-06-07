package kr.waytech.attendancecheck_beacon.server;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by 진오 on 16. 6. 7..
 * DB 공지사항 가져옴
 * 강의명
 */
public class SelectNoticeDB extends AsyncTask<String, Integer, String> {
    public static final String strUrl = "http://waytech.kr/AtdCheck/selectNotice.php";
    public static final int HANDLE_SELECT_FAIL = 3241;
    public static final int HANDLE_SELECT_OK = 6354;

    private ArrayList<NoticeData> data = new ArrayList<>();
    private Handler mHandler;
    private final String TAG = "SelectNoticeDB";


    public SelectNoticeDB(Handler mHandler) {
        this.mHandler = mHandler;
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

                if (v.length > 0) {
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.connect();

                    OutputStream outputStream = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(outputStream, "UTF-8"));
                    writer.write("class=" + v[0]);
                    writer.flush();
                    writer.close();

                }
                Log.d(TAG, conn.getResponseCode() + "");
                // 연결되었음 코드가 리턴되면.
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    for (; ; ) {
                        // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
                        String line = br.readLine();
                        if (line == null) break;
                        // 저장된 텍스트 라인을 jsonHtml에 붙여넣음
                        jsonHtml.append(line + "\n");
                    }
                    br.close();
                }
                conn.disconnect();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Log.d(TAG, jsonHtml.toString());

        return jsonHtml.toString();

    }


    protected void onPostExecute(String str) {
        String title;
        String value;
        String className;


        try {
            JSONArray root = new JSONArray(str);

            for (int i = 0; i < root.length(); i++) {
                JSONObject jo = root.getJSONObject(i);
                title = jo.getString("NOTICE_TITLE");
                value = jo.getString("NOTICE_VALUE");
                className = jo.getString("NOTICE_CLASS");


                data.add(new NoticeData(title, value, className));
            }
            mHandler.obtainMessage(HANDLE_SELECT_OK, data).sendToTarget();

        } catch (JSONException e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(HANDLE_SELECT_FAIL);
        }

    }

}
