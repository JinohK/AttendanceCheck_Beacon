package kr.waytech.attendancecheck_beacon.server;

import java.io.Serializable;
import java.util.Calendar;
import java.util.StringTokenizer;

/**
 * Created by Kim-Jinoh on 16. 5. 20..
 * 비콘 데이터
 */
public class ClassData implements Serializable {
    private String uuid;
    private int major;
    private int minor;
    private String className;       // 강의명
    private String classEdu;        // 교수님아이디
    private String classEduName;    // 교수님 이름
    private String classNumber;     // 강의실
    private String classDayWeek;    // 강의요일
    private String classStart;      // 강의시작시간
    private String classEnd;        // 강의종료시간

    private Calendar calStart;
    private Calendar calEnd;


    public ClassData(String uuid, int major, int minor) {
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
    }


    public ClassData(String uuid, int major, int minor, String className, String classEdu, String classNumber, String classDayWeek, String classStart, String classEnd, String classEduName) {
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
        this.className = className;
        this.classEdu = classEdu;
        this.classNumber = classNumber;
        this.classDayWeek = classDayWeek;
        this.classStart = classStart;
        this.classEnd = classEnd;
        this.classEduName = classEduName;

    }


    public String getClassEduName() {
        return classEduName;
    }

    public void setClassEduName(String classEduName) {
        this.classEduName = classEduName;
    }

    public Calendar getCalStart() {
        calStart = Calendar.getInstance();
        StringTokenizer tokenizer = new StringTokenizer(classStart, ":");

        calStart.set(Calendar.HOUR_OF_DAY, Integer.parseInt(tokenizer.nextToken()));
        calStart.set(Calendar.MINUTE, Integer.parseInt(tokenizer.nextToken()));
        calStart.set(Calendar.SECOND, 0);
        calStart.set(Calendar.MILLISECOND, 0);
        return calStart;
    }

    public void setCalStart(Calendar calStart) {
        this.calStart = calStart;
    }

    public Calendar getCalEnd() {
        calEnd = Calendar.getInstance();
        StringTokenizer tokenizer = new StringTokenizer(classEnd, ":");

        calEnd.set(Calendar.HOUR_OF_DAY, Integer.parseInt(tokenizer.nextToken()));
        calEnd.set(Calendar.MINUTE, Integer.parseInt(tokenizer.nextToken()));
        calEnd.set(Calendar.SECOND, 0);
        calEnd.set(Calendar.MILLISECOND, 0);
        return calEnd;
    }

    public void setCalEnd(Calendar calEnd) {
        this.calEnd = calEnd;
    }

    public String getClassDayWeek() {
        return classDayWeek;
    }

    public void setClassDayWeek(String classDayWeek) {
        this.classDayWeek = classDayWeek;
    }

    public String getClassStart() {
        return classStart;
    }

    public void setClassStart(String classStart) {
        this.classStart = classStart;
    }

    public String getClassEnd() {
        return classEnd;
    }

    public void setClassEnd(String classEnd) {
        this.classEnd = classEnd;
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

    public String getClassNumber() {
        return classNumber;
    }

    public void setClassNumber(String classNumber) {
        this.classNumber = classNumber;
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
