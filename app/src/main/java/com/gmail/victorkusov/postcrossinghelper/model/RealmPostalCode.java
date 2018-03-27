package com.gmail.victorkusov.postcrossinghelper.model;


import com.gmail.victorkusov.postcrossinghelper.database.RealmDBHelper;

import io.realm.Realm;
import io.realm.RealmObject;


public class RealmPostalCode extends RealmObject{

    private String region;
    private String place;
    private String land;
    private String postalCode;
    private String countryCode;
    private double latitude;
    private double longitude;
    private String iso;

    private boolean isSavedToFirebase;

    public RealmPostalCode() {
    }

    public RealmPostalCode(PostalCode code){
        region = code.getRegion();
        place = code.getPlace();
        land = code.getLand();
        postalCode = code.getPostalCode();
        countryCode = code.getCountryCode();
        latitude = code.getLatitude();
        longitude = code.getLongitude();
        iso = code.getIso();
        isSavedToFirebase = true;
    }

    public boolean isSavedToFirebase() {
        return isSavedToFirebase;
    }

    public void setIsSavedToRealm(boolean deleteIcon) {
        isSavedToFirebase = deleteIcon;
    }

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

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
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
