package ai.attendance.AppSharedPreference;

import android.content.Context;
import android.content.SharedPreferences;

public class AttendanceAISharedPreference {

    public static final String PREFS_NAME = "ATTENDANCEAI_PREFS";
    public static final String PHOTO = "photo";
    public static final String MOBILE = "Mobile";
    public static final String USERNAME = "Name";
    public static final String COMPANYNAME = "CompanyName";
    public static final String LATITIDE = "Latitude";
    public static final String LONGITUDE = "Longitude";
    public static final String COMPANYLOCATION = "CompanyLocation";
    public static final String EMPCODE = "EmpCode";
    public static final String SITEID = "Site_Id";
    public static final String OTP = "Otp";
    private Context context;

    public static void saveOtpToPreferences(Context ctx, String otp) {

        try {
            SharedPreferences.Editor editor = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
            editor.putString(OTP, otp);
            editor.commit();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String loadOtpFromPreferences(Context ctx) {
        String otp = "";
        try {
            SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            otp = prefs.getString(OTP, "NA");

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return otp;
    }




    public static void savePhotoToPreferences(Context ctx, String photo) {

        try {
            SharedPreferences.Editor editor = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
            editor.putString(PHOTO, photo);
            editor.commit();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String loadPhotoFromPreferences(Context ctx) {
        String photo = "";
        try {
            SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            photo = prefs.getString(PHOTO, "NA");

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return photo;
    }



    public static void saveMobileToPreferences(Context ctx, String mobile) {

        try {
            SharedPreferences.Editor editor = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
            editor.putString(MOBILE, mobile);
            editor.commit();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String loadMobileFromPreferences(Context ctx) {
        String mobile = "";
        try {
            SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            mobile = prefs.getString(MOBILE, "NA");

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return mobile;
    }


    public static void saveUserNameToPreferences(Context ctx, String username) {

        try {
            SharedPreferences.Editor editor = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
            editor.putString(USERNAME, username);
            editor.commit();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String loadUserNameFromPreferences(Context ctx) {
        String username = "";
        try {
            SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            username = prefs.getString(USERNAME, "NA");

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return username;
    }


    public static void saveCompanyNameToPreferences(Context ctx, String companyname) {

        try {
            SharedPreferences.Editor editor = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
            editor.putString(COMPANYNAME, companyname);
            editor.commit();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String loadCompanyNameFromPreferences(Context ctx) {
        String companyname = "";
        try {
            SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            companyname = prefs.getString(COMPANYNAME, "NA");

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return companyname;
    }

    public static void saveUserLatituteToPreferences(Context ctx, String latitude) {

        try {
            SharedPreferences.Editor editor = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
            editor.putString(LATITIDE, latitude);
            editor.commit();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String loadUserLatitudeFromPreferences(Context ctx) {
        String latitude = "";
        try {
            SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            latitude = prefs.getString(LATITIDE, "NA");

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return latitude;
    }

    public static void saveUserLongitudeToPreferences(Context ctx, String longitude) {

        try {
            SharedPreferences.Editor editor = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
            editor.putString(LONGITUDE, longitude);
            editor.commit();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String loadUserLongitudeFromPreferences(Context ctx) {
        String longitude = "";
        try {
            SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            longitude = prefs.getString(LONGITUDE, "NA");

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return longitude;
    }


    public static void saveUserCompanyLocationToPreferences(Context ctx, String companyLocation) {

        try {
            SharedPreferences.Editor editor = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
            editor.putString(COMPANYLOCATION, companyLocation);
            editor.commit();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String loadUserCompanyLocationFromPreferences(Context ctx) {
        String companyLocation = "";
        try {
            SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            companyLocation = prefs.getString(COMPANYLOCATION, "NA");

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return companyLocation;
    }

    public static void saveUserEmpCodeToPreferences(Context ctx, String empCode) {

        try {
            SharedPreferences.Editor editor = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
            editor.putString(EMPCODE, empCode);
            editor.commit();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String loadUserEmpCodeFromPreferences(Context ctx) {
        String empCode = "";
        try {
            SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            empCode = prefs.getString(EMPCODE, "NA");

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return empCode;
    }


    public static void saveSiteIdToPreferences(Context ctx, String siteId) {

        try {
            SharedPreferences.Editor editor = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
            editor.putString(SITEID, siteId);
            editor.commit();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String loadSiteIdFromPreferences(Context ctx) {
        String siteId = "";
        try {
            SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            siteId = prefs.getString(SITEID, "NA");

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return siteId;
    }

}
