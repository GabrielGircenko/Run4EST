package com.gircenko.gabriel.run4est.models;

/**
 * Created by Gabriel Gircenko on 27-Oct-16.
 */
public class JogModel {

    /** In {@link com.gircenko.gabriel.run4est.Constants#DATE_FORMAT} */
    private String date;
    private String time;
    private long distance;

    public JogModel() {}

    public JogModel(String date, String time, long distance) {
        this.date = date;
        this.time = time;
        this.distance = distance;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getDistance() {
        return distance;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }
}
