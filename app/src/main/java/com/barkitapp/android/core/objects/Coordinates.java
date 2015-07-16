package com.barkitapp.android.core.objects;

import java.io.Serializable;
import java.util.Date;

public class Coordinates implements Serializable {

    private double mLatitude;
    private double mLongitude;
    private Date mDate;

    public Coordinates(double latitude, double longitude, Date date) {

        mLatitude = latitude;
        mLongitude = longitude;
        mDate = date;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double mLongitude) {
        this.mLongitude = mLongitude;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date mDate) {
        this.mDate = mDate;
    }
}
