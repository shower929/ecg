package com.swm.engineering;


/**
 * Created by yangzhenyu on 2016/11/2.
 */

class LocationData  {
    public final double latitude;
    public final double longitude;
    public final float accuracy;
    public final long time;

    LocationData(double latitude, double longitude, float accuracy, long time) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.time = time;
    }

    byte[] dump() {
        return ("(" + latitude + "," + longitude + ");" + accuracy + ";" + time).getBytes();
    }

}
