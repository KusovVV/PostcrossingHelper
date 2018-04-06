package com.gmail.victorkusov.postcrossinghelper.model;


import android.os.Parcel;
import android.os.Parcelable;

public class InputData implements Parcelable{
    private String dateTime;
    private String event;
    private String place;



    public InputData() {
    }

    protected InputData(Parcel in) {
        dateTime = in.readString();
        event = in.readString();
        place = in.readString();
    }

    public static final Creator<InputData> CREATOR = new Creator<InputData>() {
        @Override
        public InputData createFromParcel(Parcel in) {
            return new InputData(in);
        }

        @Override
        public InputData[] newArray(int size) {
            return new InputData[size];
        }
    };

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getPlace() {
        return place != null ? place : "";
    }

    public void setPlace(String place) {
        this.place = place;
    }

    @Override
    public String toString() {
        return "InputData{" +
                "dateTime='" + dateTime + '\'' +
                ", event='" + event + '\'' +
                ", place='" + place + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dateTime);
        dest.writeString(event);
        dest.writeString(place);
    }
}
