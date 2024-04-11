package ai.attendance.utility;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AppGlobalConstants {
    public static final String WEBSERVICE_BASE_URL="http://173.249.47.2:8085/api/";
    public static final String WEBSERVICE_BASE_URL_NEW="http://35.200.220.64:1234/userapi/";
    public static final int WEBSERVICE_TIMEOUT_VALUE_IN_MILLIS = 20000;   // 60 seconds
    public static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    public static String dat = sdf.format(new Date());;

}
