package com.gmail.victorkusov.postcrossinghelper.model;


import com.google.gson.annotations.SerializedName;

public class PostalCode {

    @SerializedName("adminName1")
    private String region;
    @SerializedName("adminName2")
    private String place;
    @SerializedName("adminName3")
    private String land;
    @SerializedName("postalCode")
    private String postalCode;
    @SerializedName("countryCode")
    private String countryCode;
    @SerializedName("lat")
    private double latitude;
    @SerializedName("lng")
    private double longitude;
    @SerializedName("ISO3166-2")
    private String iso;

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getLand() {
        return land;
    }

    public void setLand(String land) {
        this.land = land;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }


    @Override
    public String toString() {
        return "PostalCode{" +
                "region='" + region + '\'' +
                ", place='" + place + '\'' +
                ", land='" + land + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", iso='" + iso + '\'' +
                '}';
    }
}
