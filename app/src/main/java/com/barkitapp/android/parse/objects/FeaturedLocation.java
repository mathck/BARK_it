package com.barkitapp.android.parse.objects;

import android.support.annotation.NonNull;

import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

public class FeaturedLocation implements Comparable<FeaturedLocation> {

    public FeaturedLocation(ParseObject parseObject) {
        this.name = parseObject.getString("name");
        this.location = parseObject.getParseGeoPoint("location");
        this.priority = parseObject.getInt("priority");
        this.radius = parseObject.getInt("radius");
    }

    @Override
    public int compareTo(@NonNull FeaturedLocation another) {
        int priority_this = getPriority();
        int priority_another = another.getPriority();

        if (priority_this == priority_another)
            return 0;
        else if (priority_this < priority_another)
            return -1;
        else
            return 1;
    }

    private ParseGeoPoint location;
    private String name;
    private int priority;
    private int radius;

    public ParseGeoPoint getLocation() {
        return location;
    }

    public void setLocation(ParseGeoPoint location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}
