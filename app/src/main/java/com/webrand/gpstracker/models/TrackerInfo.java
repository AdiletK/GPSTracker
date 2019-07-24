package com.webrand.gpstracker.models;

public class TrackerInfo {
   private int id;
   private double start_latitude;
   private double start_longitude;
   private double end_latitude;
   private double end_longitude;
   private String time;
   private String distance;

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String date;

    public TrackerInfo(int id,double start_latitude, double start_longitude, double end_latitude, double end_longitude,
                       String time,String date,String distance) {
        this.id = id;
        this.start_latitude = start_latitude;
        this.start_longitude = start_longitude;
        this.end_latitude = end_latitude;
        this.end_longitude = end_longitude;
        this.time = time;
        this.date = date;
        this.distance = distance;
    }
    public TrackerInfo(double start_latitude, double start_longitude, double end_latitude, double end_longitude,
                       String time,String date,String distance) {
        this.start_latitude = start_latitude;
        this.start_longitude = start_longitude;
        this.end_latitude = end_latitude;
        this.end_longitude = end_longitude;
        this.time = time;
        this.date = date;
        this.distance = distance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getStart_latitude() {
        return start_latitude;
    }

    public void setStart_latitude(double start_latitude) {
        this.start_latitude = start_latitude;
    }

    public double getStart_longitude() {
        return start_longitude;
    }

    public void setStart_longitude(double start_longitude) {
        this.start_longitude = start_longitude;
    }

    public double getEnd_latitude() {
        return end_latitude;
    }

    public void setEnd_latitude(double end_latitude) {
        this.end_latitude = end_latitude;
    }

    public double getEnd_longitude() {
        return end_longitude;
    }

    public void setEnd_longitude(double end_longitude) {
        this.end_longitude = end_longitude;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
