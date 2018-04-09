package com.gmail.victorkusov.diffprocess;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DistanceCodeList {
    @SerializedName("postalCodes")
    private List<DistanceCode> distanceCodes;

    public List<DistanceCode> getDistanceCodes() {
        return distanceCodes;
    }

    public void setDistanceCodes(List<DistanceCode> distanceCodes) {
        this.distanceCodes = distanceCodes;
    }
}
