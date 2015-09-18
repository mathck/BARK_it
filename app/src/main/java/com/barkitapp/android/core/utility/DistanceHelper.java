package com.barkitapp.android.core.utility;

import com.barkitapp.android.core.objects.Coordinates;
import com.parse.ParseGeoPoint;

import java.util.Date;

public class DistanceHelper {

    /**
     *
     * @param offset in meters
     * @return
     */
    public static ParseGeoPoint GetOffsetLocation(ParseGeoPoint location, double offset) {
        //Position, decimal degrees
        double lat = location.getLatitude();
        double lon = location.getLongitude();

        //Earth’s radius, sphere
        double R  = 6378137;

        //Coordinate offsets in radians
        double dLat = offset / R;
        double dLon = offset / (R * Math.cos(Math.PI * lat / 180));

        ParseGeoPoint result = new ParseGeoPoint();
        result.setLatitude(lat + dLat * 180 / Math.PI);
        result.setLongitude(lon + dLon * 180 / Math.PI);

        return result;
    }
}
