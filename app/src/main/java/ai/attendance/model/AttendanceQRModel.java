package ai.attendance.model;

public class AttendanceQRModel {

    String CompanyAddress,CompanyName,DateTime,
    LATITUDE,LONGITUDE,UserID;

    public AttendanceQRModel(String CompanyAddress, String CompanyName, String DateTime, String LATITUDE, String LONGITUDE, String UserID) {
        this.CompanyAddress = CompanyAddress;
        this.CompanyName = CompanyName;
        this.DateTime = DateTime;
        this.LATITUDE = LATITUDE;
        this.LONGITUDE = LONGITUDE;
        this.UserID = UserID;
    }

    public String getCompanyAddress() {
        return CompanyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        CompanyAddress = companyAddress;
    }

    public String getCompanyName() {
        return CompanyName;
    }

    public void setCompanyName(String companyName) {
        CompanyName = companyName;
    }

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }

    public String getLATITUDE() {
        return LATITUDE;
    }

    public void setLATITUDE(String LATITUDE) {
        this.LATITUDE = LATITUDE;
    }

    public String getLONGITUDE() {
        return LONGITUDE;
    }

    public void setLONGITUDE(String LONGITUDE) {
        this.LONGITUDE = LONGITUDE;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }
}
