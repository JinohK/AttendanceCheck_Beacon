package kr.waytech.attendancecheck_beacon.server;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by 진오 on 16. 5. 20..
 * DB Beacon 리스트 가져옴
 */
public class SelectBeaconDB extends AsyncTask<String, Integer, String> {
    public static final String strUrl = "http://waytech.kr/AtdCheck/selectBeacon.php";
    public static final int HANDLE_SELECT_FAIL = 23425;
    public static final int HANDLE_SELECT_OK = 65478;

    private ArrayList<BeaconData> data;
    private Handler mHandler;
    private final String TAG = "SelectBeaconDB";


    public SelectBeaconDB(Handler mHandler) {
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

//                    OutputStream outputStream = conn.getOutputStream();
//                    BufferedWriter writer = new BufferedWriter(
//                            new OutputStreamWriter(outputStream, "UTF-8"));
//                    writer.write("id=" + v[0]);
//                    writer.write("&pwd=" + v[1]);
//                    writer.flush();
//                    writer.close();

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
        int index;
        String uuid;
        int major;
        int minor;
        String className;
        String classEdu;
        int classNumber;


        try {
            JSONArray root = new JSONArray(str);

            for (int i = 0; i < root.length(); i++) {
                JSONObject jo = root.getJSONObject(i);
                index = jo.getInt("BEACON_INDEX");
                uuid = jo.getString("BEACON_UUID");
                major = jo.getInt("BEACON_MAJOR");
                minor = jo.getInt("BEACON_MINOR");
                className = jo.getString("CLASS_NAME");
                classEdu = jo.getString("CLASS_EDU");
                classNumber= jo.getInt("CLASS_NUMBER");

                data.add(new BeaconData(index, uuid, major, minor, className, classEdu, classNumber));
            }
            mHandler.obtainMessage(HANDLE_SELECT_OK, data).sendToTarget();

        } catch (JSONException e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(HANDLE_SELECT_FAIL);
        }

    }

}
