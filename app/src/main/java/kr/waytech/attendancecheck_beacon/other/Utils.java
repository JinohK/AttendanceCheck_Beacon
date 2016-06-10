package kr.waytech.attendancecheck_beacon.other;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Calendar;

/**
 * Created by Kim-Jinoh on 16. 5. 20..
 */
public class Utils {

    public static final String PREF_ID = "PREF_ID";
    public static final String PREF_TYPE = "PREF_TYPE";
    public static final String PREF_AUTOLOGIN = "PREF_AUTOLOGIN";
    public static final String PREF_NAME = "PREF_NAME";
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


    public static String calToStr(Calendar cal) {
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DATE);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        int sec = cal.get(Calendar.SECOND);

        String str = year + "-" + month + "-" + day + " " + hour + ":" + min + ":" + sec;
        return str;
    }

    // 종료시 팝업 창
    public static void closePopup(final Activity activity) {
        new AlertDialog.Builder(activity)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("BYE")
                .setMessage("종료 하시겠습니까?")
                .setPositiveButton(activity.getString(android.R.string.yes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                activity.moveTaskToBack(true);
                                activity.finish();
                            }
                        }
                ).setNegativeButton(activity.getString(android.R.string.no), null).show(); //��ҹ�ư�� ��������..

    }
}
