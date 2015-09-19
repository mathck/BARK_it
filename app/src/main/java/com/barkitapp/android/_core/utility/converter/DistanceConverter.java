package com.barkitapp.android._core.utility.converter;

import android.content.Context;

import com.barkitapp.android._core.objects.Coordinates;
import com.barkitapp.android._core.services.LocationService;

public class DistanceConverter {

    private static final String KM = " km";

    /**
     * 0,1-0,9km, afterwards 1km, 2km, ...
     * @return returns a string with the distance in KM
     */
    public static String GetDistanceInKm(Context context, double latitude, double longitude) {
        try {
            Coordinates current_location = LocationService.getLocation(context);

            if(current_location == null)
                return "-";

            double distanceInMeters = distFrom(current_location.getLatitude(), current_location.getLongitude(), latitude, longitude);
            return GetDistanceInKm(distanceInMeters);
        }
        catch (Exception e) {
            return "-";
        }
    }

    /**
     * 0,1-0,9km, afterwards 1km, 2km, ...
     * @param distanceInMeters distance provided by Location.distanceTo()
     * @return returns a string with the distance in KM
     */
    public static String GetDistanceInKm(double distanceInMeters) {
        if(distanceInMeters <= 100.0f) {
            return "0,1" + KM;
        }
        else if(distanceInMeters < 5000.0f) {
            // 0,1 - 0,9 km
            return String.format("%.1f", (distanceInMeters / 1000.0f)) + KM;
        }
        else if(distanceInMeters > 5000.0f) {
            return (int) ((distanceInMeters / 1000.0f) - 0.5f) + KM;
        }

        return "1" + KM;
    }

    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000;
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return earthRadius * c;
    }
}
