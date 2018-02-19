package com.gmail.victorkusov.postcrossinghelper.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PostalCodesList {

    @SerializedName("postalCodes")
    private List<PostalCode> mPostalCodes;

    public List<PostalCode> getPostalCodes() {
        return mPostalCodes;
    }

}
