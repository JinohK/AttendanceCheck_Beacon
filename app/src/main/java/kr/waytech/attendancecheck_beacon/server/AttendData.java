package kr.waytech.attendancecheck_beacon.server;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Kim-Jinoh on 16. 6. 2..
 */
public class AttendData {
    private String className;
    private String userId;
    private String inTime;
    private String outTime;

    private Calendar calIn;
    private Calendar calOut;


    public AttendData(String className, String userId, String inTime, String outTime) {
        this.className = className;
        this.userId = userId;
        this.inTime = inTime;
        this.outTime = outTime;
        calIn = Calendar.getInstance();
        calOut = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            calIn.setTime(format.parse(inTime));
            calOut.setTime(format.parse(outTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


    public Calendar getCalIn() {
        return calIn;
    }

    public void setCalIn(Calendar calIn) {
        this.calIn = calIn;
    }

    public Calendar getCalOut() {
        return calOut;
    }

    public void setCalOut(Calendar calOut) {
        this.calOut = calOut;
    }

    public String getInTime() {
        return inTime;
    }

    public void setInTime(String inTime) {
        this.inTime = inTime;
    }

    public String getOutTime() {
        return outTime;
    }

    public void setOutTime(String outTime) {
        this.outTime = outTime;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
