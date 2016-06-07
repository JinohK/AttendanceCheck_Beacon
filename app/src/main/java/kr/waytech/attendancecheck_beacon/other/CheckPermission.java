package kr.waytech.attendancecheck_beacon.other;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by Kim-Jinoh on 16. 6. 8..
 * 마시멜로우대응 블투관련 권한 체크/요청
 */
public class CheckPermission {

    public static final int PERMISSIONS_REQUEST = 124;

    /**
     * 블루투스 관련 권한받았는지 체
     * @param con
     * @return 권한있음(true) 없음(false)
     */
    public static boolean checkSelfPermission(Context con){
        boolean check = true;
        int permissionCheck = ContextCompat.checkSelfPermission(con, Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionCheck== PackageManager.PERMISSION_DENIED){
            // 권한 없음
            check = false;
        }else{
            // 권한 있음
        }
        permissionCheck = ContextCompat.checkSelfPermission(con, Manifest.permission.ACCESS_COARSE_LOCATION);

        if(permissionCheck== PackageManager.PERMISSION_DENIED){
            // 권한 없음
            check = false;
        }else{
            // 권한 있음
        }

        return check;
    }

    public static void requestPermission(Activity activity){
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                PERMISSIONS_REQUEST);
    }
}
