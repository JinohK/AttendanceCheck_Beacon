package kr.waytech.attendancecheck_beacon.server;

/**
 * Created by Kim-Jinoh on 16. 5. 20..
 * 비콘 데이터
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

    public BeaconData(int index, String uuid, int major, int minor, String className, String classEdu, int classNumber) {
        this.index = index;
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
        this.className = className;
        this.classEdu = classEdu;
        this.classNumber = classNumber;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassEdu() {
        return classEdu;
    }

    public void setClassEdu(String classEdu) {
        this.classEdu = classEdu;
    }

    public int getClassNumber() {
        return classNumber;
    }

    public void setClassNumber(int classNumber) {
        this.classNumber = classNumber;
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

    @Override
    public boolean equals(Object o) {
        BeaconData data = (BeaconData) o;

        if(this.uuid.equals(data.getUuid()) &&
                this.major == data.getMajor() &&
                this.minor == data.getMinor() &&
                this.className.equals(data.getClassName())){
            return true;
        }else{
            return false;
        }

    }
}
