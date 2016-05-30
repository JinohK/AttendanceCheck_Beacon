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
 * Created by 진오 on 16. 5. 20..
 * DB Class 리스트 가져옴
 */
public class SelectClassDB extends AsyncTask<String, Integer, String> {
    public static final String strUrl = "http://waytech.kr/AtdCheck/selectClass.php";
    public static final int HANDLE_SELECT_FAIL = 23425;
    public static final int HANDLE_SELECT_OK = 65478;

    private ArrayList<ClassData> data = new ArrayList<>();
    private Handler mHandler;
    private final String TAG = "SelectClassDB";


    public SelectClassDB(Handler mHandler) {
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
                    writer.write("id=" + v[0]);
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
        String uuid;
        int major;
        int minor;
        String className;
        String classEdu;
        String classNumber;
        String classDayWeek;
        String classStart;
        String classEnd;


        try {
            JSONArray root = new JSONArray(str);

            for (int i = 0; i < root.length(); i++) {
                JSONObject jo = root.getJSONObject(i);
                className = jo.getString("CLASS_NAME");
                classEdu = jo.getString("CLASS_EDU");
                classNumber= jo.getString("CLASS_NUMBER");
                classDayWeek = jo.getString("CLASS_DAYWEEK");
                classStart = jo.getString("CLASS_START");
                classEnd = jo.getString("CLASS_END");
                uuid = jo.getString("CLASS_UUID");
                major = jo.getInt("CLASS_MAJOR");
                minor = jo.getInt("CLASS_MINOR");

                data.add(new ClassData(uuid, major, minor,
                        className, classEdu, classNumber, classDayWeek, classStart, classEnd));
            }
            mHandler.obtainMessage(HANDLE_SELECT_OK, data).sendToTarget();

        } catch (JSONException e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(HANDLE_SELECT_FAIL);
        }

    }

}
