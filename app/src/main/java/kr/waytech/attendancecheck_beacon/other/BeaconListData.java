package kr.waytech.attendancecheck_beacon.other;

/**
 * Created by Kim-Jinoh on 16. 5. 31..
 * 비컨 리스트뷰 데이터
 */
public class BeaconListData {
    private String uuid;
    private int major;
    private int minor;
    private int rssi;


    public BeaconListData(String uuid, int major, int minor, int rssi) {
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
        this.rssi = rssi;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }
}
