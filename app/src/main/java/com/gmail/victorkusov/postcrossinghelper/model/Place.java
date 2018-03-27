package com.gmail.victorkusov.postcrossinghelper.model;


import com.gmail.victorkusov.postcrossinghelper.model.interfaces.IFirebaseNotes;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.annotations.SerializedName;

import io.realm.annotations.Ignore;

public class Place implements IFirebaseNotes {

//            "placeName": "Полоцк",
//            "countryCode": "BY",
//            "postalcode": "211400"

    @SerializedName("placeName")
    private String placeName;

    @SerializedName("countryCode")
    private String countryCode;

    @SerializedName("postalCode")
    private String postalCode;

    @Ignore
    private boolean isSavedToFirebase;


    public Place() {
    }

    public Place(RealmPlace code) {
        placeName = code.getPlaceName();
        countryCode = code.getCountryCode();
        postalCode = code.getPostalCode();
    }

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

    public boolean isSavedToFirebase() {
        return isSavedToFirebase;
    }

    public void setSavedToFirebase(boolean savedToFirebase) {
        isSavedToFirebase = savedToFirebase;
    }

    @Override
    public String toString() {
        return "Place{" +
                "placeName='" + placeName + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", postalCode=" + postalCode +
                ", isSavedToFirebase=" + isSavedToFirebase +
                '}';
    }


    @Override
    public void addNoteToFirebase() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(ModelConstants.TABLE_NAME_PLACE).child(placeName).setValue(this);
    }

    @Override
    public void deleteNoteFromFirebase() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(ModelConstants.TABLE_NAME_PLACE).child(placeName).setValue(null);
    }
}
