package kr.waytech.attendancecheck_beacon.other;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Kim-Jinoh on 16. 5. 20..
 */
public class Utils {

    public static final String PREF_ID = "PREF_ID";
    public static final String PREF_TYPE = "PREF_TYPE";
    public static final String PREF_AUTOLOGIN = "PREF_AUTOLOGIN";

    public static final String USER_STD = "학생";
    public static final String USER_EDU = "교직원";

    public static boolean isOnline(Activity activity) { // network 연결 상태 확인
        try {
            ConnectivityManager conMan = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo.State wifi = conMan.getNetworkInfo(1).getState(); // wifi
            if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
                return true;
            }

            NetworkInfo.State mobile = conMan.getNetworkInfo(0).getState(); // mobile ConnectivityManager
            // .TYPE_MOBILE
            if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING) {
                return true;
            }

        } catch (NullPointerException e) {
            return false;
        }

        return false;
    }
}
