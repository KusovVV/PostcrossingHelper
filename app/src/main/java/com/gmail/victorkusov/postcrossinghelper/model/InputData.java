package com.gmail.victorkusov.postcrossinghelper.model;


public class InputData {
    private String dateTime;
    private String event;
    private String place;


    public InputData() {
    }


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
}
