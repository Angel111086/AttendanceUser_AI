package ai.attendance.model;

import android.graphics.Bitmap;

import java.io.Serializable;

public class QRModel implements Serializable {

    String site_id,empdate,emplatitude,emplongitude;
    static String image_path;
    static Bitmap bmp;



    public QRModel(){}

    public QRModel(String site_id, String empdate, String emplatitude, String emplongitude) {
        this.site_id = site_id;
        this.empdate = empdate;
        this.emplatitude = emplatitude;
        this.emplongitude = emplongitude;
    }

    public QRModel(String site_id, String empdate, String emplatitude, String emplongitude,String image_path) {
        this.site_id = site_id;
        this.empdate = empdate;
        this.emplatitude = emplatitude;
        this.emplongitude = emplongitude;
        QRModel.image_path = image_path;
    }
    public String getSite_id() {
        return site_id;
    }

    public void setSite_id(String site_id) {
        this.site_id = site_id;
    }

    public String getEmpdate() {
        return empdate;
    }

    public void setEmpdate(String empdate) {
        this.empdate = empdate;
    }

    public String getEmplatitude() {
        return emplatitude;
    }

    public void setEmplatitude(String emplatitude) {
        this.emplatitude = emplatitude;
    }

    public String getEmplongitude() {
        return emplongitude;
    }

    public void setEmplongitude(String emplongitude) {
        this.emplongitude = emplongitude;
    }
    public static String getImage_path() {
        return image_path;
    }

    public static void setImage_path(String image_path) {
        QRModel.image_path = image_path;
    }

    public static Bitmap getBmp() {
        return bmp;
    }

    public static void setBmp(Bitmap bmp) {
        QRModel.bmp = bmp;
    }
}
