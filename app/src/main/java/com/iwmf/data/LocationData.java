package com.iwmf.data;

import java.io.Serializable;

/**
 * <p> Model class to store location of user. </p>
 */
public class LocationData implements Serializable {

    private static final long serialVersionUID = 5599930683054541328L;
    private String name = "";
    private String address = "";
    private double latitude = 0;
    private double longitude = 0;
    private double distance = 0;
    private String distanceString = "";

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getAddress() {

        return address;
    }

    public void setAddress(String address) {

        this.address = address;
    }

    public double getLatitude() {

        return latitude;
    }

    public void setLatitude(double latitude) {

        this.latitude = latitude;
    }

    public double getLongitude() {

        return longitude;
    }

    public void setLongitude(double longitude) {

        this.longitude = longitude;
    }

    public double getDistance() {

        return distance;
    }

    public void setDistance(double distance) {

        this.distance = distance;
    }

    public String getDistanceString() {

        return distanceString;
    }

    public void setDistanceString(String distanceString) {

        this.distanceString = distanceString;
    }

}
