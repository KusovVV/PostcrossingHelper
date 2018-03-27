package com.gmail.victorkusov.postcrossinghelper.model;


import io.realm.RealmObject;

public class RealmPlace extends RealmObject{

    private String placeName;
    private String countryCode;
    private String postalCode;

    public RealmPlace(Place place) {
        placeName = place.getPlaceName();
        countryCode = place.getCountryCode();
        postalCode = place.getPostalCode();
    }

    public RealmPlace(){}

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @Override
    public String toString() {
        return "Place{" +
                "placeName='" + placeName + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", postalCode=" + postalCode +
                '}';
    }
}
