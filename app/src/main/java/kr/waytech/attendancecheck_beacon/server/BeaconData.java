package kr.waytech.attendancecheck_beacon.server;

/**
 * Created by Kim-Jinoh on 16. 5. 20..
 */
public class BeaconData {
    private int index;
    private String uuid;
    private int major;
    private int minor;
    private String className;
    private String classEdu;
    private int classNumber;


    public BeaconData(int index, String uuid, int major, int minor) {
        this.index = index;
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
    }


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
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
}
