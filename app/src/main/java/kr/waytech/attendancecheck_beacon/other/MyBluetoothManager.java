package kr.waytech.attendancecheck_beacon.other;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

/**
 * Created by Kim-Jinoh on 16. 5. 31..
 * 블루투스 관련 클래스
 */
public class MyBluetoothManager {

    private static BluetoothAdapter bluetoothAdapter;

    public static final int REQUEST_BT = 3432;

    private MyBluetoothManager(){}


    public static void checkInit(){
        if(bluetoothAdapter == null){
            synchronized (MyBluetoothManager.class){
                if(bluetoothAdapter == null)
                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            }
        }
    }


    /**
     * 블루투스 켜있는지
     * @return
     */
    public static boolean isBtEnabled(){
        checkInit();

        return bluetoothAdapter.isEnabled();
    }

    /**
     * 블루투스 지원하는지
     * @return
     */
    public static boolean isBtModule(){
        checkInit();
        return bluetoothAdapter!=null;
    }


    /**
     * 블루투스 ON 요청
     * @param activity
     */
    public static void RequestBtOn(Activity activity){
        activity.startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_BT);
    }

}