package com.barkitapp.android.com.barkitapp.utility;

public class DistanceConverter {

    private static final String KM = " km";

    /**
     * 0,1-0,9km, afterwards 1km, 2km, ...
     * @param distanceInMeters distance provided by Location.distanceTo()
     * @return returns a string with the distance in KM
     */
    public static String GetDistanceInKm(float distanceInMeters) {

        if(distanceInMeters < 1000.0f) {
            // 0,1 - 0,9 km
            return String.format("%.1f", (distanceInMeters / 1000.0f)) + KM;
        }
        else if(distanceInMeters > 1000.0f) {
            return (int) (distanceInMeters + 0.5f) + KM;
        }

        return "1" + KM;
    }
}
