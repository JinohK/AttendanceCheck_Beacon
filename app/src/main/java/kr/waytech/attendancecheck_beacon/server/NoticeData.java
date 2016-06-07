package kr.waytech.attendancecheck_beacon.server;

/**
 * Created by Kim-Jinoh on 16. 6. 7..
 */
public class NoticeData {
    private String title;
    private String value;
    private String className;

    public NoticeData(String title, String value, String className) {
        this.title = title;
        this.value = value;
        this.className = className;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
