package com.gmail.victorkusov.postcrossinghelper.model;


import com.gmail.victorkusov.postcrossinghelper.model.interfaces.IFirebaseNotes;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.annotations.SerializedName;

import io.realm.annotations.Ignore;


public class PostalCode implements IFirebaseNotes {

    @SerializedName("adminName1")
    private String region;
    @SerializedName("placeName")
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

    @Ignore
    private Boolean isSavedToFirebase;

    public PostalCode() {
    }

    public PostalCode(RealmPostalCode code) {
        region = code.getRegion();
        place = code.getPlace();
        land = code.getLand();
        postalCode = code.getPostalCode();
        countryCode = code.getCountryCode();
        latitude = code.getLatitude();
        longitude = code.getLongitude();
        iso = code.getIso();
    }

    public Boolean isSavedToFirebase() {
        return isSavedToFirebase;
    }

    public void setIsSavedToFirebase(Boolean isSaved) {
        isSavedToFirebase = isSaved;
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

    @Override
    public void addNoteToFirebase() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(ModelConstants.TABLE_NAME_POSTALCODE).child(postalCode).setValue(this);
    }

    @Override
    public void deleteNoteFromFirebase() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(ModelConstants.TABLE_NAME_POSTALCODE).child(postalCode).setValue(null);
    }

}
