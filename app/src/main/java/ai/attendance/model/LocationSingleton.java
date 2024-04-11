package ai.attendance.model;
public class LocationSingleton {

    private static double sharedLatitude;
    private static double sharedLongitude;

    public static double getSharedLatitude() {
        return sharedLatitude;
    }

    public static void setSharedLatitude(double sharedLatitude) {
        LocationSingleton.sharedLatitude = sharedLatitude;
    }

    public static double getSharedLongitude() {
        return sharedLongitude;
    }

    public static void setSharedLongitude(double sharedLongitude) {
        LocationSingleton.sharedLongitude = sharedLongitude;
    }
}
